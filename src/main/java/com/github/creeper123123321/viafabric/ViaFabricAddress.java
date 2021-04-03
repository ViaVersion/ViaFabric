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
