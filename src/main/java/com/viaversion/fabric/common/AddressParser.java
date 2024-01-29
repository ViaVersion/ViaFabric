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
        return parse(address, "viafabric");
    }

    public String getSuffixWithOptions() {
        if (viaOptions != null && !viaOptions.isEmpty()) {
            return viaOptions + "." + viaSuffix;
        }
        return viaSuffix;
    }

    public AddressParser parse(String address, String viaHostName) {
        address = StringUtils.removeEnd(address, ".");
        String suffixRemoved = StringUtils.removeEnd(address, "." + viaHostName);

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