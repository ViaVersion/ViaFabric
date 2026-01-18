/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.common.provider;

import com.google.common.primitives.Ints;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public abstract class AbstractFabricVersionProvider extends BaseVersionProvider {

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide()) {
            ProtocolInfo info = Objects.requireNonNull(connection.getProtocolInfo());

            if (!getConfig().isClientSideEnabled()) return info.protocolVersion();

            int serverVer = getConfig().getClientSideVersion();
            SocketAddress addr = connection.getChannel().remoteAddress();

            if (addr instanceof InetSocketAddress) {
                AddressParser parser = AddressParser.parse(((InetSocketAddress) addr).getHostName());
                Integer addrVersion = parser.protocol();
                if (addrVersion != null) {
                    serverVer = addrVersion;
                }

                try {
                    if (serverVer == -2) {
                        // Hope protocol was autodetected
                        ProtocolVersion autoVer =
                            detectVersion((InetSocketAddress) addr).getNow(null);
                        if (autoVer != null) {
                            serverVer = autoVer.getVersion();
                        }
                    }
                } catch (Exception e) {
                    getLogger().warning("Couldn't auto detect: " + e);
                }
            }

            ProtocolVersion serverVersion = ProtocolVersion.getProtocol(serverVer);

            boolean blocked = checkAddressBlocked(addr);
            boolean supported = ProtocolUtils.isSupported(serverVersion, info.protocolVersion());

            if (blocked || !supported) serverVer = info.getProtocolVersion();

            return ProtocolVersion.getProtocol(serverVer);
        }
        NativeVersionProvider natProvider = Via.getManager().getProviders().get(NativeVersionProvider.class);
        if (natProvider != null) {
            return natProvider.getNativeServerProtocolVersion();
        }
        return super.getClosestServerProtocol(connection);
    }

    private boolean checkAddressBlocked(SocketAddress addr) {
        return addr instanceof InetSocketAddress && (isDisabled(((InetSocketAddress) addr).getHostString())
            || ((((InetSocketAddress) addr).getAddress() != null) &&
            (isDisabled(((InetSocketAddress) addr).getAddress().getHostAddress())
                || isDisabled(((InetSocketAddress) addr).getAddress().getHostName()))));
    }

    private boolean isDisabled(String addr) {
        String[] parts = addr.split("\\.");
        boolean isNumericIp = parts.length == 4 && Arrays.stream(parts).map(Ints::tryParse).allMatch(Objects::nonNull);
        return IntStream.range(0, parts.length).anyMatch(i -> {
            String query;
            if (isNumericIp) {
                query = String.join(".", Arrays.stream(parts, 0, i + 1)
                    .toArray(String[]::new)) + ((i != 3) ? ".*" : "");
            } else {
                query = ((i != 0) ? "*." : "") + String.join(".", Arrays.stream(parts, i, parts.length)
                    .toArray(String[]::new));
            }
            if (getConfig().isForcedDisable(query)) {
                getLogger().info(addr + " is force-disabled. (Matches " + query + ")");
                return true;
            } else {
                return false;
            }
        });
    }

    protected abstract Logger getLogger();

    protected abstract VFConfig getConfig();

    protected abstract CompletableFuture<ProtocolVersion> detectVersion(InetSocketAddress address);
}
