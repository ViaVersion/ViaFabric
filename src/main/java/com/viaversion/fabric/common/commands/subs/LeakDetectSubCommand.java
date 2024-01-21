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
package com.viaversion.fabric.common.commands.subs;

import io.netty.util.ResourceLeakDetector;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LeakDetectSubCommand extends ViaSubCommand {
    @Override
    public String name() {
        return "leakdetect";
    }

    @Override
    public String description() {
        return "Sets ResourceLeakDetector level";
    }

    @Override
    public boolean execute(ViaCommandSender viaCommandSender, String[] strings) {
        if (strings.length == 1) {
            try {
                ResourceLeakDetector.Level level = ResourceLeakDetector.Level.valueOf(strings[0]);
                ResourceLeakDetector.setLevel(level);
                viaCommandSender.sendMessage("Set leak detector level to " + level);
            } catch (IllegalArgumentException e) {
                viaCommandSender.sendMessage("Invalid level (" + Arrays.toString(ResourceLeakDetector.Level.values()) + ")");
            }
        } else {
            viaCommandSender.sendMessage("Current leak detection level is " + ResourceLeakDetector.getLevel());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(ResourceLeakDetector.Level.values())
                    .map(Enum::name)
                    .filter(it -> it.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, args);
    }
}
