/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
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
package com.viaversion.fabric.mc12111.providers;

import com.viaversion.fabric.common.util.RemappingUtil;
import com.viaversion.fabric.mc12111.ViaFabric;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class VFHandItemProvider extends HandItemProvider {
    public Item clientItem = null;

    @Override
    public Item getHandItem(UserConnection info) {
        if (info.isClientSide()) {
            return getClientItem();
        }
        return super.getHandItem(info);
    }

    private Item getClientItem() {
        if (clientItem == null) {
            return new DataItem(0, (byte) 0, null);
        }
        return clientItem.copy();
    }

    @Environment(EnvType.CLIENT)
    public void registerClientTick() {
        try {
            ClientTickEvents.END_WORLD_TICK.register(clientWorld -> tickClient());
        } catch (NoClassDefFoundError ignored) {
            ViaFabric.JLOGGER.info("Fabric Lifecycle V1 isn't installed");
        }
    }

    private void tickClient() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p != null) {
            clientItem = fromNative(p.getInventory().getSelectedItem());
        }
    }

    private Item fromNative(ItemStack original) {
        Identifier iid = BuiltInRegistries.ITEM.getKey(original.getItem());
        int id = RemappingUtil.swordId(iid.toString());
        return new DataItem(id, (byte) original.getCount(), (short) original.getDamageValue(), null);
    }
}
