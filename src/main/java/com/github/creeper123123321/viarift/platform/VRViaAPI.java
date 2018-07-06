package com.github.creeper123123321.viarift.platform;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

import java.util.SortedSet;
import java.util.UUID;

public class VRViaAPI implements ViaAPI {
    @Override
    public int getPlayerVersion(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPlayerVersion(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPorted(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersion() {
        return Via.getPlatform().getPluginVersion();
    }

    @Override
    public void sendRawPacket(Object o, ByteBuf byteBuf) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf byteBuf) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
}
