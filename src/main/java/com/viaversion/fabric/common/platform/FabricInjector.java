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
import com.viaversion.viaversion.libs.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.OptionalInt;

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
    public ObjectSortedSet<ProtocolVersion> getServerProtocolVersions() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            final ProtocolVersion version = Via.getManager().getProviders().get(NativeVersionProvider.class).getNativeServerProtocolVersion();
            return ObjectSortedSets.singleton(version);
        }
        // On client-side we can connect to any server version
        ObjectSortedSet<ProtocolVersion> versions = new ObjectLinkedOpenHashSet<>();
        versions.add(ProtocolVersion.v1_8);
        final OptionalInt highestSupportedVersion = ProtocolVersion.getProtocols().stream().mapToInt(ProtocolVersion::getOriginalVersion).max();
        versions.add(ProtocolVersion.getProtocol(highestSupportedVersion.getAsInt()));
        return versions;
    }

    @Override
    public ProtocolVersion getServerProtocolVersion() {
        return getServerProtocolVersions().first();
    }
}
