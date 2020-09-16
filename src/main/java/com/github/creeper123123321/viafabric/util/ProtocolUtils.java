/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.util;

import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.Arrays;
import java.util.stream.Stream;

public class ProtocolUtils {
    public static boolean isSupported(int server, int client) {
        return server == client || ProtocolRegistry.getProtocolPath(client, server) != null;
    }

    public static String getProtocolName(int id) {
        ProtocolVersion ver = ProtocolVersion.getProtocol(id);
        if (ver == null) return Integer.toString(id);
        return ver.getName();
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
            return closest.getId();
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
