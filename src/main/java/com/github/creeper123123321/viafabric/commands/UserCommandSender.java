package com.github.creeper123123321.viafabric.commands;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.UUID;

public class UserCommandSender implements ViaCommandSender {
    private UserConnection con;

    public UserCommandSender(UserConnection con) {
        this.con = con;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        Via.getPlatform().sendMessage(getUUID(), s);
    }

    @Override
    public UUID getUUID() {
        return con.getProtocolInfo().getUuid();
    }

    @Override
    public String getName() {
        return con.getProtocolInfo().getUsername();
    }
}
