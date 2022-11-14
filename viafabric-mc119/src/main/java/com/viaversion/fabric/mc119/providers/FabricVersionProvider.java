package com.viaversion.fabric.mc119.providers;

import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.provider.AbstractFabricVersionProvider;
import com.viaversion.fabric.mc119.ViaFabric;
import com.viaversion.fabric.mc119.service.ProtocolAutoDetector;
import com.viaversion.fabric.mc119.signatures1_19.ProtocolPatcher1_19;
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
    public void onFinallySetVersion(int realClientsideVersion) {
        ProtocolPatcher1_19.shouldPatchKeys = realClientsideVersion <= ProtocolVersion.v1_19.getVersion();
    }

    @Override
    protected boolean isMulticonnectHandler(ChannelPipeline pipe) {
        return pipe.get(ClientConnection.class).getPacketListener().getClass().getName().startsWith("net.earthcomputer.multiconnect");
    }
}
