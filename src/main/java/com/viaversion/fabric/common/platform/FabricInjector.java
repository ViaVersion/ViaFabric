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
package com.viaversion.fabric.common.platform;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricInjector implements ViaInjector {
    @Override
    public void inject() {
        // *looks at Mixins*
    }

    @Override
    public void uninject() {
        // not possible *plays sad violin*
    }

    @Override
    public String getEncoderName() {
        return CommonTransformer.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return CommonTransformer.HANDLER_DECODER_NAME;
    }

    @Override
    public JsonObject getDump() {
        return new JsonObject();
    }

    @Override
    public IntSortedSet getServerProtocolVersions() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            int version = Via.getManager().getProviders().get(NativeVersionProvider.class).getNativeServerVersion();
            return IntSortedSets.singleton(version);
        }
        // On client-side we can connect to any server version
        IntSortedSet versions = new IntLinkedOpenHashSet();
        versions.add(ProtocolVersion.v1_8.getOriginalVersion());
        versions.add(ProtocolVersion.getProtocols()
                .stream()
                .mapToInt(ProtocolVersion::getOriginalVersion)
                .max().getAsInt());
        return versions;
    }

    @Override
    public int getServerProtocolVersion() {
        return getServerProtocolVersions().firstInt();
    }
}
