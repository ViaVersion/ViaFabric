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
package com.viaversion.fabric.mc1204.providers;

import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.provider.AbstractFabricVersionProvider;
import com.viaversion.fabric.mc1204.ViaFabric;
import com.viaversion.fabric.mc1204.service.ProtocolAutoDetector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.ClientConnection;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class FabricVersionProvider extends AbstractFabricVersionProvider {
    @Override
    protected Logger getLogger() {
        return ViaFabric.JLOGGER;
    }

    @Override
    protected VFConfig getConfig() {
        return ViaFabric.config;
    }

    @Override
    protected CompletableFuture<ProtocolVersion> detectVersion(InetSocketAddress address) {
        return ProtocolAutoDetector.detectVersion(address);
    }

    @Override
    protected boolean isMulticonnectHandler(ChannelPipeline pipe) {
        return pipe.get(ClientConnection.class).getPacketListener().getClass().getName().startsWith("net.earthcomputer.multiconnect");
    }
}
