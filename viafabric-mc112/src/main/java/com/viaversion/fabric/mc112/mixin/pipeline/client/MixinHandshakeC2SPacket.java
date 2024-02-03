package com.viaversion.fabric.mc112.mixin.pipeline.client;

import com.viaversion.fabric.mc112.service.ProtocolAutoDetector;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandshakeC2SPacket.class)
public class MixinHandshakeC2SPacket implements ProtocolAutoDetector.IHandshakeC2SPacket {

    @Shadow
    private int protocolVersion;

    @Override
    public void viaFabric$setProtocolVersion(int version) {
        this.protocolVersion = version;
    }
}
