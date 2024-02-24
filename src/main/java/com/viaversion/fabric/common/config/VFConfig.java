/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        return get(CLIENT_SIDE_FORCE_DISABLE, Collections.emptyList());
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
