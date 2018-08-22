package com.github.creeper123123321.viarift.platform;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class VRViaAPI implements ViaAPI<Void> {
    @Override
    public int getPlayerVersion(Void o) {
        throw new UnsupportedOperationException("WHAT??? A INSTANCE OF VOID???");
    }

    @Override
    public int getPlayerVersion(UUID uuid) {
        if (!isPorted(uuid)) {
            try {
                return Via.getManager().getInjector().getServerProtocolVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Via.getManager().getPortedPlayers().get(uuid).get(ProtocolInfo.class).getProtocolVersion();
    }

    @Override
    public boolean isPorted(UUID uuid) {
        return Via.getManager().getPortedPlayers().containsKey(uuid);
    }

    @Override
    public String getVersion() {
        return Via.getPlatform().getPluginVersion();
    }

    @Override
    public void sendRawPacket(Void o, ByteBuf byteBuf) throws IllegalArgumentException {
        throw new UnsupportedOperationException("WHAT??? A INSTANCE OF VOID???");
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf byteBuf) throws IllegalArgumentException {
        UserConnection ci = Via.getManager().getPortedPlayers().get(uuid);
        ci.sendRawPacket(byteBuf);
    }

    @Override
    public BossBar createBossBar(String s, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, 1f, bossColor, bossStyle);
    }

    @Override
    public BossBar createBossBar(String s, float v, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, v, bossColor, bossStyle);
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
        outputSet.removeAll(Via.getPlatform().getConf().getBlockedProtocols());

        return outputSet;
    }
}
