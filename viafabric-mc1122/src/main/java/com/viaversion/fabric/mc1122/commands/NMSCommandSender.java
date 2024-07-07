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
package com.viaversion.fabric.mc1122.commands;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.util.ComponentUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NMSCommandSender implements ViaCommandSender {
    private final CommandSource source;

    public NMSCommandSender(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String s) {
        // https://gaming.stackexchange.com/questions/138602/what-does-op-permission-level-do
        return source.canUseCommand(3, "viaversion.admin"); // the string seems to be the command
    }

    @Override
    public void sendMessage(String s) {
        source.sendMessage(Text.Serializer.deserializeText(ComponentUtil.legacyToJsonString(s)));
    }

    @Override
    public UUID getUUID() {
        if (source instanceof Entity) {
            return ((Entity) source).getUuid();
        }
        return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        if (source instanceof Entity) {
            return source.getName().asUnformattedString();
        }
        return "?";
    }
}
