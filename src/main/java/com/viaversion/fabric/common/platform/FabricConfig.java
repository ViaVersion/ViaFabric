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
import java.util.List;
import java.util.logging.Logger;

public final class FabricConfig extends AbstractViaConfig {

    private boolean itemCache;

    public FabricConfig(final File configFile, final Logger logger) {
        super(configFile, logger);
    }

    @Override
    protected void loadFields() {
        super.loadFields();
        itemCache = getBoolean("item-cache", true);
    }

    @Override
    public boolean isItemCache() {
        return itemCache;
    }

    @Override
    public List<String> getUnsupportedOptions() {
        final List<String> unsupported = super.getUnsupportedOptions();
        unsupported.remove("item-cache");
        return unsupported;
    }

}
