package com.github.creeper123123321.viafabric.util;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.Arrays;
import java.util.stream.Stream;

public class ProtocolUtils {
    public static boolean isSupported(int server, int client) {
        return server == client || Via.getManager().getProtocolManager().getProtocolPath(client, server) != null;
    }

    public static String getProtocolName(int id) {
        if (!ProtocolVersion.isRegistered(id)) return Integer.toString(id);
        return ProtocolVersion.getProtocol(id).getName();
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
