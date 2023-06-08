package com.viaversion.fabric.mc120.mixin.pipeline;

import com.viaversion.fabric.common.handler.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientConnection.class)
public class MixinClientConnection {
	@Shadow
	private Channel channel;

	@Inject(method = "setCompressionThreshold", at = @At("RETURN"))
	private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
		channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
	}
}
