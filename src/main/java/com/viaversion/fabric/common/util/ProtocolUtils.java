/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
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
package com.viaversion.fabric.common.util;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.Arrays;
import java.util.stream.Stream;

public class ProtocolUtils {
    public static boolean isSupportedClientSide(ProtocolVersion server) {
        return isSupported(server, Via.getManager().getProviders()
                .get(NativeVersionProvider.class)
                .getNativeServerProtocolVersion());
    }

    public static boolean isSupported(ProtocolVersion server, ProtocolVersion client) {
        return server.equals(client) || Via.getManager().getProtocolManager().getProtocolPath(client, server) != null;
    }

    public static String getProtocolName(int id) {
        if (!ProtocolVersion.isRegistered(id)) return Integer.toString(id);
        return ProtocolVersion.getProtocol(id).getName();
    }

    public static boolean isValid(final ProtocolVersion version) {
        return version.isKnown() && version.getVersion() != -2;
    }

    public static boolean isStartOfProtocolText(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            try {
                Integer.parseInt(s + '0');
                return true;
            } catch (NumberFormatException e2) {
                if (ProtocolVersion.getClosest(s) != null) return true;
                return ProtocolVersion.getProtocols().stream()
                        .map(ProtocolVersion::getName)
                        .flatMap(str -> Stream.concat(
                                Arrays.stream(str.split("-")),
                                Arrays.stream(new String[]{str})
                        ))
                        .anyMatch(ver -> ver.startsWith(s));
            }
        }
    }

    public static Integer parseProtocolId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            ProtocolVersion closest = ProtocolVersion.getClosest(s);
            if (closest == null) return null;
            return closest.getVersion();
        }
    }

    public static String[] getProtocolSuggestions(String text) {
        return ProtocolVersion.getProtocols().stream()
                .map(ProtocolVersion::getName)
                .flatMap(str -> Stream.concat(
                        Arrays.stream(str.split("-")),
                        Arrays.stream(new String[]{str})
                ))
                .distinct()
                .filter(ver -> ver.startsWith(text))
                .toArray(String[]::new);
    }
}
