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
package com.viaversion.fabric.mc1165.commands;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.util.ComponentUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
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
        return source.hasPermissionLevel(3);
    }

    public static Text fromLegacy(String legacy) {
        return Text.Serializer.fromJson(ComponentUtil.legacyToJsonString(legacy));
    }

    @Override
    public void sendMessage(String s) {
        if (source instanceof ServerCommandSource) {
            ((ServerCommandSource) source).sendFeedback(fromLegacy(s), false);
        } else if (source instanceof FabricClientCommandSource) {
            ((FabricClientCommandSource) source).sendFeedback(fromLegacy(s));
        }
    }

    @Override
    public UUID getUUID() {
        if (source instanceof ServerCommandSource) {
            Entity entity = ((ServerCommandSource) source).getEntity();
            if (entity != null) return entity.getUuid();
        } else if (source instanceof FabricClientCommandSource) {
            return ((FabricClientCommandSource) source).getPlayer().getUuid();
        }
        return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        if (source instanceof ServerCommandSource) {
            return ((ServerCommandSource) source).getName();
        } else if (source instanceof FabricClientCommandSource) {
            return ((FabricClientCommandSource) source).getPlayer().getEntityName();
        }
        return "?";
    }
}
