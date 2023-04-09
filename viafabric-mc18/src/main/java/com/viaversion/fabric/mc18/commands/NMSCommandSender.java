package com.viaversion.fabric.mc18.commands;

import com.viaversion.fabric.common.util.RemappingUtil;
import com.viaversion.viaversion.api.command.ViaCommandSender;
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
        source.sendMessage(Text.Serializer.deserialize(RemappingUtil.legacyToJson(s)));
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
