package com.github.creeper123123321.viafabric.mixin.pipeline;

import com.github.creeper123123321.viafabric.handler.CommonTransformer;
import com.github.creeper123123321.viafabric.handler.FabricDecodeHandler;
import com.github.creeper123123321.viafabric.handler.FabricEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

@Mixin(targets = "net.minecraft.server.ServerNetworkIo$1")
public class MixinServerNetworkIoChInit {
    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            UserConnection user = new UserConnection(channel);
            new ProtocolPipeline(user);

            channel.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new FabricEncodeHandler(user));
            channel.pipeline().addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new FabricDecodeHandler(user));
        }
    }
}
