/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
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
package com.viaversion.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ViaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getAllMods()
            .stream()
            .noneMatch(it -> it.getMetadata().getId().startsWith("viafabric-mc"))) {
            throw new IllegalStateException("ViaFabric sub-mod didn't load correctly. Please ensure you have Fabric API installed from here: https://modrinth.com/mod/fabric-api/versions");
        }
    }
}
