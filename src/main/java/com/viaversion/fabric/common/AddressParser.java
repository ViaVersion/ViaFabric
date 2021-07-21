package com.viaversion.fabric.common;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

// Based on VIAaaS parser
public class AddressParser {
    public Integer protocol;
    public String viaSuffix;
    public String serverAddress;
    public String viaOptions;

    public AddressParser parse(String address) {
        return parse(address, ".viafabric");
    }

    public AddressParser parse(String address, String viaHostName) {
        address = StringUtils.removeEnd(address, ".");
        String suffixRemoved = StringUtils.removeEnd(address, viaHostName);

        if (suffixRemoved.equals(address)) {
            serverAddress = address;
            return this;
        }

        boolean stopOptions = false;
        List<String> optionsParts = new ArrayList<>();
        List<String> serverParts = new ArrayList<>();

        for (String part : Lists.reverse(Arrays.asList(suffixRemoved.split(Pattern.quote("."))))) {
            if (!stopOptions && parseOption(part)) {
                optionsParts.add(part);
                continue;
            }
            stopOptions = true;
            serverParts.add(part);
        }

        serverAddress = String.join(".", Lists.reverse(serverParts));
        viaOptions = String.join(".", Lists.reverse(optionsParts));
        viaSuffix = viaHostName;

        return this;
    }

    public boolean parseOption(String part) {
        String option;
        if (part.length() < 2) {
            return false;
        } else if (part.startsWith("_")) {
            option = String.valueOf(part.charAt(1));
        } else if (part.charAt(1) == '_') {
            option = String.valueOf(part.charAt(0));
        } else {
            return false;
        }

        String arg = part.substring(2);
        if ("v".equals(option)) {
            parseProtocol(arg);
        }

        return true;
    }

    public void parseProtocol(String arg) {
        protocol = Ints.tryParse(arg);
        if (protocol == null) {
            ProtocolVersion ver = ProtocolVersion.getClosest(arg.replace("_", "."));
            if (ver != null) protocol = ver.getVersion();
        }
    }
}