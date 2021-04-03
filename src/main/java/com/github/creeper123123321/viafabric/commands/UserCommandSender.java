package com.github.creeper123123321.viafabric.commands;

import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.data.UserConnection;

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
