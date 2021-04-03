package com.github.creeper123123321.viafabric.platform;

import us.myles.ViaVersion.api.ViaAPIBase;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

import java.util.UUID;

public class VRViaAPI extends ViaAPIBase<UUID> {
    @Override
    public BossBar<Void> createBossBar(String s, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, 1f, bossColor, bossStyle);
    }

    @Override
    public BossBar<Void> createBossBar(String s, float v, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, v, bossColor, bossStyle);
    }
}
