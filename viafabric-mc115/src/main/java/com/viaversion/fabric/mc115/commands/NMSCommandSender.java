package com.viaversion.fabric.mc115.commands;

import com.viaversion.fabric.common.util.RemappingUtil;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
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
        return Text.Serializer.fromJson(RemappingUtil.legacyToJson(legacy));
    }

    @Override
    public void sendMessage(String s) {
        if (source instanceof ServerCommandSource) {
            ((ServerCommandSource) source).sendFeedback(fromLegacy(s), false);
        } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            MinecraftClient.getInstance().player.addChatMessage(fromLegacy(s), false);
        }
    }

    @Override
    public UUID getUUID() {
        if (source instanceof ServerCommandSource) {
            Entity entity = ((ServerCommandSource) source).getEntity();
            if (entity != null) return entity.getUuid();
        } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            return MinecraftClient.getInstance().player.getUuid();
        }
        return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        if (source instanceof ServerCommandSource) {
            return ((ServerCommandSource) source).getName();
        } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            return MinecraftClient.getInstance().player.getEntityName();
        }
        return "?";
    }
}
