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

package com.github.creeper123123321.viafabric.mixin.pipeline;

import com.github.creeper123123321.viafabric.handler.CommonTransformer;
import com.github.creeper123123321.viafabric.handler.FabricDecodeHandler;
import com.github.creeper123123321.viafabric.handler.FabricEncodeHandler;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Shadow
    private Channel channel;

    /*
    @Redirect(
            method = "exceptionCaught",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/logging/log4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V"
            ))
    private void redirectDebug(Logger logger, String message, Throwable t) {
        if ("Failed to sent packet".equals(message)) {
            logger.info(message, t);
        } else {
            logger.debug(message, t);
        }
    }
    */

    @Inject(method = "setCompressionThreshold", at = @At(
            value = "RETURN",
            remap = false
    ))
    private void fixCompressionOrder(int compressionThreshold, CallbackInfo ci) {
        if (channel.pipeline().get(FabricEncodeHandler.class) == null) return;
        if (channel.pipeline().names().indexOf("compress")
                < channel.pipeline().names().indexOf(CommonTransformer.HANDLER_ENCODER_NAME)) {
            return; // Order is correct or compression is disabled
        }
        // Fixes the handler order
        FabricDecodeHandler decode = channel.pipeline().remove(FabricDecodeHandler.class);
        FabricEncodeHandler encode = channel.pipeline().remove(FabricEncodeHandler.class);
        channel.pipeline().addAfter("decompress", "via-decoder", decode);
        channel.pipeline().addAfter("compress", "via-encoder", encode);
    }
}
