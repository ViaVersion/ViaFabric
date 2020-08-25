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

package com.github.creeper123123321.viafabric;

import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.Locale;

public class ViaFabricAddress {
    public int protocol = 0;
    public String viaSuffix = null;
    public String realAddress = null;

    public ViaFabricAddress parse(String address) {
        if (address == null) return null;
        String[] parts = address.split("\\.");

        boolean foundDomain = false;
        boolean foundOptions = false;

        StringBuilder ourParts = new StringBuilder();
        StringBuilder realAddrBuilder = new StringBuilder();

        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i];
            boolean realAddrPart = false;
            if (foundDomain) {
                if (!foundOptions) {
                    if (part.startsWith("_")) {
                        String arg = part.substring(2);
                        if (part.toLowerCase(Locale.ROOT).startsWith("_v")) {
                            try {
                                protocol = Integer.parseInt(arg);
                            } catch (NumberFormatException e) {
                                ProtocolVersion closest = ProtocolVersion.getClosest(arg.replace("_", "."));
                                if (closest != null) {
                                    protocol = closest.getId();
                                }
                            }
                        }
                    } else {
                        foundOptions = true;
                    }
                }
                if (foundOptions) {
                    realAddrPart = true;
                }
            } else if (part.equalsIgnoreCase("viafabric")) {
                foundDomain = true;
            }
            if (realAddrPart) {
                realAddrBuilder.insert(0, part + ".");
            } else {
                ourParts.insert(0, part + ".");
            }
        }

        String realAddr = realAddrBuilder.toString().replaceAll("\\.$", "");
        String suffix = ourParts.toString().replaceAll("\\.$", "");

        if (realAddr.isEmpty()) {
            this.realAddress = address;
        } else {
            this.realAddress = realAddr;
            this.viaSuffix = suffix;
        }

        return this;
    }

    @Override
    public String toString() {
        return "ViaFabricAddress{" +
                "protocol=" + protocol +
                ", viaSuffix='" + viaSuffix + '\'' +
                ", realAddress='" + realAddress + '\'' +
                '}';
    }
}
