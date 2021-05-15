package com.viaversion.fabric.mc115.mixin.pipeline.client;

import com.viaversion.fabric.mc115.ViaFabric;
import com.viaversion.fabric.mc115.service.ProtocolAutoDetector;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "connect", at = @At("HEAD"))
    private static void onConnect(InetAddress address, int port, boolean shouldUseNativeTransport, CallbackInfoReturnable<ClientConnection> cir) {
        try {
            if (!ViaFabric.config.isClientSideEnabled()) return;
            ProtocolAutoDetector.detectVersion(new InetSocketAddress(address, port)).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            ViaFabric.JLOGGER.log(Level.WARNING, "Could not auto-detect protocol for " + address + " " + e);
        }
    }
}
