package com.viaversion.fabric.mc18.providers;

import com.viaversion.fabric.mc18.ViaFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VRHandItemProvider extends HandItemProvider {
    public Item clientItem = null;
    public final Map<UUID, Item> serverPlayers = new ConcurrentHashMap<>();

    @Override
    public Item getHandItem(UserConnection info) {
        Item serverItem;
        if (info.isClientSide()) {
            return getClientItem();
        } else if ((serverItem = serverPlayers.get(info.getProtocolInfo().getUuid())) != null) {
            return new DataItem(serverItem);
        }
        return super.getHandItem(info);
    }

    private Item getClientItem() {
        if (clientItem == null) {
            return new DataItem(0, (byte) 0, (short) 0, null);
        }
        return new DataItem(clientItem);
    }

    @Environment(EnvType.CLIENT)
    public void registerClientTick() {
        try {
            ClientTickEvents.END_WORLD_TICK.register(world -> tickClient());
        } catch (NoClassDefFoundError ignored1) {
            ViaFabric.JLOGGER.info("Fabric Lifecycle V1 isn't installed");
        }
    }

    public void registerServerTick() {
        try {
            ServerTickEvents.END_WORLD_TICK.register(this::tickServer);
        } catch (NoClassDefFoundError ignored1) {
            ViaFabric.JLOGGER.info("Fabric Lifecycle V1 isn't installed");
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
        world.playerEntities.forEach(it -> serverPlayers
                .put(it.getUuid(), fromNative(it.inventory.getMainHandStack())));
    }

    private Item fromNative(ItemStack original) {
        if (original == null) return new DataItem(0, (byte) 0, (short) 0, null);
        int id = net.minecraft.item.Item.getRawId(original.getItem());
        return new DataItem(id, (byte) original.count, (short) original.getDamage(), null);
    }
}
