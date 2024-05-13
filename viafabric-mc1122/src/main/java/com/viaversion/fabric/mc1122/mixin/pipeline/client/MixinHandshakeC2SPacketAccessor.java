package com.viaversion.fabric.mc1122.mixin.pipeline.client;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandshakeC2SPacket.class)
public interface MixinHandshakeC2SPacketAccessor {
    @Accessor
    void setProtocolVersion(int version);
}
