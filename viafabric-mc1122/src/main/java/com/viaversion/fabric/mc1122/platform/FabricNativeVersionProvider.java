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
package com.viaversion.fabric.mc1122.platform;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.server.ServerMetadata;

public class FabricNativeVersionProvider implements NativeVersionProvider {
    @Override
    public int getNativeServerVersion() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getClientProtocol();
        }

        ServerMetadata.Version version = MinecraftClient.getInstance().getServer().getServerMetadata().getVersion();
        if (version == null) return ProtocolVersion.v1_12_2.getVersion();
        return version.getProtocolVersion();
    }

    @Environment(EnvType.CLIENT)
    private int getClientProtocol() {
        try {
            return RealmsSharedConstants.class.getField("NETWORK_PROTOCOL_VERSION").getInt(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ProtocolVersion.v1_12_2.getVersion(); // fallback
    }
}
