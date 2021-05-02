package com.viaversion.fabric.mc18.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
        source.sendMessage(Text.Serializer.deserialize(legacyToJson(s)));
    }

    private String legacyToJson(String legacy) {
        return GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    @Override
    public UUID getUUID() {
        if (source instanceof Entity) {
            return ((Entity) source).getUuid();
        }
        return UUID.fromString(getName());
    }

    @Override
    public String getName() {
        if (source instanceof Entity) {
            return source.getName().asString();
        }
        return "?";
    }
}
