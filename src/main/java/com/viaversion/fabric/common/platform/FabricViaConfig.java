/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.common.platform;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FabricViaConfig extends AbstractViaConfig {

    protected final List<String> UNSUPPORTED = new ArrayList<>();

    public FabricViaConfig(File configFile, Logger logger) {
        super(configFile, logger);
        UNSUPPORTED.addAll(BUKKIT_ONLY_OPTIONS);
        UNSUPPORTED.addAll(VELOCITY_ONLY_OPTIONS);

        // Load config
        reload();
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
        // Nothing Currently
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return UNSUPPORTED;
    }
}
