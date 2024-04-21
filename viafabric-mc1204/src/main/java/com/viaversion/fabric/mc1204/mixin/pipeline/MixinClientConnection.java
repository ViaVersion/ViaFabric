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
package com.viaversion.fabric.mc1204.mixin.pipeline;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.common.handler.FabricEncodeHandler;
import com.viaversion.fabric.common.handler.PipelineReorderEvent;
import com.viaversion.fabric.common.protocol.HostnameParserProtocol;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
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

    @Inject(method = "addHandlers", at = @At("RETURN"))
    private static void onAddHandlers(ChannelPipeline pipeline, NetworkSide side, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        final Channel channel = pipeline.channel();
        if (channel instanceof SocketChannel) {
            UserConnection user = new UserConnectionImpl(channel, side == NetworkSide.CLIENTBOUND);
            ProtocolPipeline protocolPipeline = new ProtocolPipelineImpl(user);
            if (user.isClientSide()) {
                protocolPipeline.add(HostnameParserProtocol.INSTANCE);
            }

            pipeline.addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new FabricEncodeHandler(user));
            pipeline.addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new FabricDecodeHandler(user));
        }
    }
}
