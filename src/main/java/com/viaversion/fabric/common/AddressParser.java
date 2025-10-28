/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.common;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntImmutableList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

// Based on VIAaaS parser
public class AddressParser {
    private static final String VIA = "viafabric";
    private static final Pattern DOT_SUFFIX = Pattern.compile("\\.$");
    private static final int MAX_PORT = 65535;

    // Retaining a list for now; not exposing it to the end consumer.
    // An idea tossed about is to retry the server for the given protocols.
    @Nullable
    @VisibleForTesting
    final IntList protocols;
    @NotNull
    private final String serverAddress;
    @Nullable
    private final Integer port;

    private AddressParser(@NotNull String address, @Nullable Integer port) {
        this(address, null, port);
    }

    private AddressParser(@NotNull String address, @Nullable IntList protocols, @Nullable Integer port) {
        this.serverAddress = address;
        this.port = port;
        this.protocols = protocols;
    }

    @NotNull
    public static AddressParser parse(String address) {
        return parse(address, VIA);
    }

    @NotNull
    private static AddressParser parse(String address, String viaHostName) {
        int portIndex = address.lastIndexOf(':');
        Integer port = portIndex >= 0 ? Ints.tryParse(address.substring(portIndex + 1)) : null;

        if (port != null && port >= 0 && port < MAX_PORT + 1) {
            if (address.charAt(portIndex - 1) == '.') {
                // Let's not allocate an intermediate string.
                portIndex -= 1;
            } else if (port < 10000 &&
                // I don't like this but there's not really a better way of doing this
                address.lastIndexOf(':', portIndex - 1) > Math.max(
                    address.lastIndexOf(']', portIndex - 1),
                    address.lastIndexOf('.', portIndex - 1)
                )) {
                // We parsed an IPv6, a port isn't ideal here.
                port = null;
            }

            // Keeping a sane flow control.
            if (port != null) {
                // Truncate the port off, as that interferes.
                address = address.substring(0, portIndex);
            }
        }

        address = StringUtils.removeEnd(address, ".");

        String truncated = StringUtils.removeEnd(address, '.' + viaHostName);
        if (!address.equals(truncated)) {
            return parseSuffix(truncated, port);
        }

        truncated = StringUtils.removeStart(address, viaHostName + '.');
        if (!address.equals(truncated)) {
            final AddressParser addr = parsePrefix(truncated, port);
            if (addr != null) {
                return addr;
            }
        }

        return new AddressParser(address, port);
    }

    @NotNull
    private static AddressParser parseSuffix(String address, Integer port) {
        boolean stopOptions = false;
        IntList protocolParts = new IntArrayList();
        List<String> serverParts = new ArrayList<>();

        Integer protocol;
        for (String part : Lists.reverse(Arrays.asList(address.split("\\.")))) {
            if (!stopOptions && (protocol = parseSuffixOption(part)) != null) {
                protocolParts.add(protocol.intValue());
                continue;
            }
            stopOptions = true;
            serverParts.add(part);
        }

        return new AddressParser(
            String.join(".", Lists.reverse(serverParts)),
            new IntImmutableList(Lists.reverse(protocolParts)),
            port
        );
    }

    // Fail condition = returns null; caller must fall through.
    @Nullable
    private static AddressParser parsePrefix(String address, Integer port) {
        IntList protocols = new IntArrayList();
        int index = 0, lastIndex, colonIndex = address.indexOf(';');

        if (colonIndex < 0) {
            return null;
        }

        while ((index = address.indexOf('+', lastIndex = index)) >= 0 && index < colonIndex) {
            parseAndAdd(address.substring(lastIndex, index), protocols);
            index++;
        }

        parseAndAdd(address.substring(lastIndex, colonIndex), protocols);

        return new AddressParser(
            address.substring(colonIndex + 1),
            new IntImmutableList(protocols),
            port
        );
    }

    private static void parseAndAdd(String part, IntList protocols) {
        final Integer protocol = parseSchemeOption(part);
        if (protocol != null) {
            protocols.add(protocol.intValue());
        }
    }

    private static Integer parseSuffixOption(String part) {
        String option;
        if (part.length() < 2) {
            return null;
        } else if (part.startsWith("_")) {
            option = String.valueOf(part.charAt(1));
        } else if (part.charAt(1) == '_') {
            option = String.valueOf(part.charAt(0));
        } else {
            return null;
        }

        String arg = part.substring(2);
        if ("v".equals(option)) {
            return parseProtocol(arg);
        }

        return null;
    }

    private static Integer parseSchemeOption(String part) {
        if (part.length() < 2) {
            return null;
        }
        if (!part.startsWith("v")) {
            return null;
        }
        return parseProtocol(part.substring(1));
    }

    private static Integer parseProtocol(String arg) {
        final Integer protocol = Ints.tryParse(arg);
        if (protocol != null) {
            return protocol;
        }
        ProtocolVersion ver = ProtocolVersion.getClosest(arg.replace('_', '.'));
        if (ver != null) {
            return ver.getVersion();
        }
        return null;
    }

    private static String toProtocolName(int protocol) {
        if (protocol < 0 || !ProtocolVersion.isRegistered(protocol)) {
            return Integer.toString(protocol);
        }
        return ProtocolVersion.getProtocol(protocol).getIncludedVersions().iterator().next();
    }

    public String getSuffixWithOptions() {
        if (protocols == null) {
            return "";
        }
        if (protocols.isEmpty()) {
            return VIA;
        }
        return protocols.intStream()
            .mapToObj(AddressParser::toProtocolName)
            .map(str -> str.replace('.', '_'))
            .collect(Collectors.joining("._v", "_v", "." + VIA));
    }

    public String getPrefixWithOptions() {
        if (protocols == null) {
            return "";
        }
        if (protocols.isEmpty()) {
            return VIA;
        }
        return protocols.intStream()
            .mapToObj(AddressParser::toProtocolName)
            .collect(Collectors.joining("+v", VIA + ".v", ""));
    }

    public boolean hasViaMetadata() {
        return protocols != null;
    }

    public boolean hasProtocol() {
        return protocols != null && !protocols.isEmpty();
    }

    public Integer protocol() {
        if (protocols != null && !protocols.isEmpty()) {
            return protocols.getInt(0);
        }
        return null;
    }

    @NotNull
    public String serverAddress() {
        return this.serverAddress;
    }

    @Nullable
    public Integer port() {
        return this.port;
    }

    public String toAddress() {
        if (port != null) {
            return serverAddress + ':' + port;
        }
        return serverAddress;
    }

    public String toSuffixedViaAddress() {
        final String address = this.addAddressSuffix(serverAddress);
        if (port != null) {
            return address + ':' + port;
        }
        return address;
    }

    public InetAddress resolve() throws UnknownHostException {
        return this.addAddressSuffix(InetAddress.getByName(serverAddress));
    }

    public InetSocketAddress addAddressSuffix(InetSocketAddress address) throws UnknownHostException {
        return new InetSocketAddress(this.addAddressSuffix(address.getAddress()), address.getPort());
    }

    public InetAddress addAddressSuffix(InetAddress address) throws UnknownHostException {
        return InetAddress.getByAddress(this.addAddressSuffix(address.getHostName()), address.getAddress());
    }

    public String addAddressSuffix(String input) {
        if (!this.hasViaMetadata()) {
            return input;
        }
        return DOT_SUFFIX.matcher(input).replaceAll("") + '.' + this.getSuffixWithOptions();
    }

    @Override
    public String toString() {
        return "AddressParser{" + this.toSuffixedViaAddress() + '}';
    }
}
