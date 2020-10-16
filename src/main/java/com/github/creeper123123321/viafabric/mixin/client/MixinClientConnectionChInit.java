/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
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

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.handler.CommonTransformer;
import com.github.creeper123123321.viafabric.handler.clientside.VRDecodeHandler;
import com.github.creeper123123321.viafabric.handler.clientside.VREncodeHandler;
import com.github.creeper123123321.viafabric.platform.VRClientSideUserConnection;
import com.github.creeper123123321.viafabric.protocol.ViaFabricHostnameProtocol;
import com.github.creeper123123321.viafabric.service.ProtocolAutoDetector;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

import java.util.concurrent.ExecutionException;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnectionChInit {
    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            UserConnection user = new VRClientSideUserConnection(channel);
            new ProtocolPipeline(user).add(ViaFabricHostnameProtocol.INSTANCE);

            channel.pipeline()
                    .addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new VREncodeHandler(user))
                    .addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new VRDecodeHandler(user));

            if (channel.remoteAddress() != null) {
                try {
                    ProtocolAutoDetector.SERVER_VER.get(((SocketChannel) channel).remoteAddress()); // Let's cache it before we need it, and hope we'll not block netty thread
                } catch (ExecutionException e) {
                    ViaFabric.JLOGGER.warning("Protocol auto detector error: " + e);
                }
            }
        }
    }
}
