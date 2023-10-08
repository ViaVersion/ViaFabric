package com.viaversion.fabric.common.config;

import com.viaversion.viaversion.util.Config;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VFConfig extends Config {
    public static final String ENABLE_CLIENT_SIDE = "enable-client-side";
    public static final String CLIENT_SIDE_VERSION = "client-side-version";
    public static final String CLIENT_SIDE_FORCE_DISABLE = "client-side-force-disable";
    public static final String HIDE_BUTTON = "hide-button";

    public VFConfig(File configFile) {
        super(configFile);
        reload();
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("assets/viafabric/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> map) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }

    public boolean isClientSideEnabled() {
        return getBoolean(ENABLE_CLIENT_SIDE, false);
    }

    public void setClientSideEnabled(boolean val) {
        set(ENABLE_CLIENT_SIDE, val);
    }

    public int getClientSideVersion() {
        return getInt(CLIENT_SIDE_VERSION, -1);
    }

    public void setClientSideVersion(int val) {
        set(CLIENT_SIDE_VERSION, val);
    }

    public Collection<?> getClientSideForceDisable() {
        return (List<?>) get(CLIENT_SIDE_FORCE_DISABLE, List.class, Collections.emptyList());
    }

    public void setHideButton(boolean val) {
        set(HIDE_BUTTON, val);
    }

    public boolean isHideButton() {
        return getBoolean(HIDE_BUTTON, false);
    }

    public boolean isForcedDisable(String line) {
        return getClientSideForceDisable().contains(line);
    }
}
