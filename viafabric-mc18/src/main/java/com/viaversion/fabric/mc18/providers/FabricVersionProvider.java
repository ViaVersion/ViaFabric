package com.viaversion.fabric.mc18.providers;

import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.provider.AbstractFabricVersionProvider;
import com.viaversion.fabric.mc18.ViaFabric;
import com.viaversion.fabric.mc18.service.ProtocolAutoDetector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

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
}