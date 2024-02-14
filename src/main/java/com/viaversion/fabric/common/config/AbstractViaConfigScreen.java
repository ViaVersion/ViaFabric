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

import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public interface AbstractViaConfigScreen {
    String TITLE_TRANSLATE_ID = "gui.viafabric_config.title";
    String VERSION_TRANSLATE_ID = "gui.protocol_version_field.name";

    default int getProtocolTextColor(ProtocolVersion version, boolean parsedValid) {
        if (!parsedValid) return 0xff0000; // Red
        if (!ProtocolUtils.isValid(version)) return 0x5555FF; // Blue
        if (!ProtocolUtils.isSupportedClientSide(version)) return 0xFFA500; // Orange
        return 0xE0E0E0; // Default
    }

    default int calculatePosX(int width, int entryNumber) {
        return width / 2 - 155 + entryNumber % 2 * 160;
    }

    default int calculatePosY(int height, int entryNumber) {
        return height / 6 + 24 * (entryNumber / 2);
    }
}
