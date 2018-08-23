/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
