package com.github.creeper123123321.viafabric.platform;

import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.boss.CommonBoss;

public class VRBossBar extends CommonBoss<Void> {
    public VRBossBar(String title, float health, BossColor color, BossStyle style) {
        super(title, health, color, style);
    }
}
