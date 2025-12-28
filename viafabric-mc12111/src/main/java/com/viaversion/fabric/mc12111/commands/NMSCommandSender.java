/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.mc12111.commands;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity;

public class NMSCommandSender implements ViaCommandSender {
    private final SharedSuggestionProvider provider;

    public NMSCommandSender(SharedSuggestionProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean hasPermission(String s) {
        // https://gaming.stackexchange.com/questions/138602/what-does-op-permission-level-do
        if (provider instanceof CommandSourceStack commandSourceStack) {
            NameAndId player = commandSourceStack.getPlayer().nameAndId();
            LevelBasedPermissionSet permissions = commandSourceStack.getServer().getProfilePermissions(player);
            return permissions.level().isEqualOrHigherThan(PermissionLevel.ADMINS);
        }
        return true;
    }

    public static MutableComponent fromLegacy(String legacy) {
        return Component.literal(legacy);
    }

    @Override
    public void sendMessage(String s) {
        if (provider instanceof CommandSourceStack) {
            ((CommandSourceStack) provider).sendSuccess(() -> fromLegacy(s), false);
        } else if (provider instanceof FabricClientCommandSource) {
            ((FabricClientCommandSource) provider).sendFeedback(fromLegacy(s));
        }
    }

    @Override
    public UUID getUUID() {
        if (provider instanceof CommandSourceStack) {
            Entity entity = ((CommandSourceStack) provider).getEntity();
            if (entity != null) return entity.getUUID();
        } else if (provider instanceof FabricClientCommandSource) {
            return ((FabricClientCommandSource) provider).getPlayer().getUUID();
        }
        return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        if (provider instanceof CommandSourceStack) {
            return ((CommandSourceStack) provider).getTextName();
        } else if (provider instanceof FabricClientCommandSource) {
            return ((FabricClientCommandSource) provider).getPlayer().getName().getString();
        }
        return "?";
    }
}
