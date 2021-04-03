package com.github.creeper123123321.viafabric.platform;

import us.myles.ViaVersion.AbstractViaConfig;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VRViaConfig extends AbstractViaConfig {
    // Based on Sponge ViaVersion
    private static List<String> UNSUPPORTED = Arrays.asList("anti-xray-patch", "bungee-ping-interval",
            "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking",
            "velocity-ping-interval", "velocity-ping-save", "velocity-servers",
            "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");

    public VRViaConfig(File configFile) {
        super(configFile);
        // Load config
        reloadConfig();
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("assets/viaversion/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
        // Nothing Currently
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return UNSUPPORTED;
    }

    @Override
    public boolean isAntiXRay() {
        return false;
    }

    @Override
    public boolean isNMSPlayerTicking() {
        return false;
    }

    @Override
    public boolean is1_12QuickMoveActionFix() {
        return false;
    }

    @Override
    public String getBlockConnectionMethod() {
        return "packet";
    }

    @Override
    public boolean is1_9HitboxFix() {
        return false;
    }

    @Override
    public boolean is1_14HitboxFix() {
        return false;
    }
}
