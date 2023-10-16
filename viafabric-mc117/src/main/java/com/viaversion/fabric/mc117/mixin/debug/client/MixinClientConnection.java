package com.viaversion.fabric.mc117.mixin.debug.client;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "exceptionCaught", at = @At("TAIL"))
    public void exceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        LOGGER.error("Packet error", ex);
    }
}
