package com.viaversion.fabric.mc114.providers;

import com.viaversion.fabric.common.util.RemappingUtil;
import com.viaversion.fabric.mc114.ViaFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
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
            WorldTickCallback.EVENT.register(world -> {
                if (world.isClient) {
                    tickClient();
                }
            });
        } catch (NoClassDefFoundError ignored2) {
            ViaFabric.JLOGGER.info("Fabric Lifecycle V0 isn't installed");
        }
    }

    public void registerServerTick() {
        WorldTickCallback.EVENT.register(world -> {
            if (!world.isClient) {
                tickServer(world);
            }
        });
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
        int id = RemappingUtil.swordId(iid.toString());
        return new DataItem(id, (byte) original.getCount(), (short) original.getDamage(), null);
    }
}
