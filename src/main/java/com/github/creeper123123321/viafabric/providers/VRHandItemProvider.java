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

package com.github.creeper123123321.viafabric.providers;

import com.github.creeper123123321.viafabric.ViaFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VRHandItemProvider extends HandItemProvider {
    public Item clientItem = null;
    public Map<UUID, Item> serverPlayers = new ConcurrentHashMap<>();

    @Override
    public Item getHandItem(UserConnection info) {
        Item serverItem;
        if (info.isClientSide()) {
            return getClientItem();
        } else if ((serverItem = serverPlayers.get(info.getProtocolInfo().getUuid())) != null) {
            return new Item(serverItem);
        }
        return super.getHandItem(info);
    }

    private Item getClientItem() {
        if (clientItem == null) {
            return new Item(0, (byte) 0, (short) 0, null);
        }
        return new Item(clientItem);
    }

    @Environment(EnvType.CLIENT)
    public void registerClientTick() {
        try {
            ClientTickEvents.END_WORLD_TICK.register(clientWorld -> tickClient());
        } catch (NoClassDefFoundError ignored) {
            try {
                WorldTickCallback.EVENT.register(world -> {
                    if (world.isClient) {
                        tickClient();
                    }
                });
            } catch (NoClassDefFoundError ignored2) {
                ViaFabric.JLOGGER.info("Fabric Lifecycle V0/V1 isn't installed");
            }
        }
    }

    public void registerServerTick() {
        try {
            ServerTickEvents.END_WORLD_TICK.register(this::tickServer);
        } catch (NoClassDefFoundError ignored) {
            WorldTickCallback.EVENT.register(world -> {
                if (!world.isClient) {
                    tickServer(world);
                }
            });
        }
    }

    private void tickClient() {
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p != null) {
            clientItem = fromNative(p.inventory.getMainHandStack());
        }
    }

    private void tickServer(World world) {
        serverPlayers.clear();
        world.getPlayers().forEach(it -> serverPlayers
                .put(it.getUuid(), fromNative(it.inventory.getMainHandStack())));
    }

    private Item fromNative(ItemStack original) {
        Identifier iid = Registry.ITEM.getId(original.getItem());
        if (iid == null) return new Item(0, (byte) 0, (short) 0, null);
        int id = swordId(iid.toString());
        return new Item(id, (byte) original.getCount(), (short) original.getDamage(), null);
    }

    private int swordId(String id) {
        // https://github.com/ViaVersion/ViaVersion/blob/8de26a0ad33f5b739f5394ed80f69d14197fddc7/common/src/main/java/us/myles/ViaVersion/protocols/protocol1_9to1_8/Protocol1_9To1_8.java#L86
        switch (id) {
            case "minecraft:iron_sword":
                return 267;
            case "minecraft:wooden_sword":
                return 268;
            case "minecraft:golden_sword":
                return 272;
            case "minecraft:diamond_sword":
                return 276;
            case "minecraft:stone_sword":
                return 283;
        }
        return 0;
    }
}
