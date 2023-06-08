package com.viaversion.fabric.mc120.mixin.debug.client;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientConnection.class)
public interface MixinClientConnectionAccessor {
    @Accessor
    Channel getChannel();
}
