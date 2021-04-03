package com.github.creeper123123321.viafabric.commands;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import us.myles.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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

    @Override
    public void sendMessage(String s) {
        if (source instanceof ServerCommandSource) {
            ((ServerCommandSource) source).sendFeedback(Text.Serializer.fromJson(legacyToJson(s)), false);
        } else if (source instanceof CottonClientCommandSource) {
            ((CottonClientCommandSource) source).sendFeedback(Text.Serializer.fromJson(legacyToJson(s)), false);
        }
    }

    private String legacyToJson(String legacy) {
        return GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    @Override
    public UUID getUUID() {
        if (source instanceof ServerCommandSource) {
            Entity entity = ((ServerCommandSource) source).getEntity();
            if (entity != null) return entity.getUuid();
        } else if (source instanceof CottonClientCommandSource) {
            return MinecraftClient.getInstance().player.getUuid();
        }
        return UUID.fromString(getName());
    }

    @Override
    public String getName() {
        if (source instanceof ServerCommandSource) {
            return ((ServerCommandSource) source).getName();
        } else if (source instanceof CottonClientCommandSource) {
            return MinecraftClient.getInstance().player.getEntityName();
        }
        return "?";
    }
}
