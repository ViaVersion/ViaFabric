/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.mixin.client;

import com.github.creeper123123321.viafabric.handler.VRDecodeHandler;
import com.github.creeper123123321.viafabric.handler.VREncodeHandler;
import com.github.creeper123123321.viafabric.platform.VRUserConnection;
import com.github.creeper123123321.viafabric.protocol.Interceptor;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class ClientConnectionChInit {
    @Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            UserConnection user = new VRUserConnection((SocketChannel) channel);
            new ProtocolPipeline(user).add(new Interceptor());

            MessageToByteEncoder oldEncoder = (MessageToByteEncoder) channel.pipeline().get("encoder");
            ByteToMessageDecoder oldDecoder = (ByteToMessageDecoder) channel.pipeline().get("decoder");

            channel.pipeline().replace("encoder", "encoder", new VREncodeHandler(user, oldEncoder));
            channel.pipeline().replace("decoder", "decoder", new VRDecodeHandler(user, oldDecoder));
        }
    }
}
