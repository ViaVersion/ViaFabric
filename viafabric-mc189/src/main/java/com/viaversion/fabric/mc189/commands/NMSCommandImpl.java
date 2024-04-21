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
package com.viaversion.fabric.mc189.commands;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import com.viaversion.viaversion.api.command.ViaVersionCommand;

import java.util.Arrays;
import java.util.List;

public class NMSCommandImpl extends AbstractCommand {
    private final ViaVersionCommand handler;

    public NMSCommandImpl(ViaVersionCommand handler) {
        this.handler = handler;
    }

    @Override
    public String getCommandName() {
        return "viaversion";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("vvfabric", "viaver");
    }

    @Override
    public String getUsageTranslationKey(CommandSource commandSource) {
        return "/viaversion [help|subcommand]";
    }

    @Override
    public void execute(CommandSource commandSource, String[] strings) {
        handler.onCommand(new NMSCommandSender(commandSource), strings);
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource commandSource, String[] strings, BlockPos blockPos) {
        return handler.onTabComplete(new NMSCommandSender(commandSource), strings);
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
