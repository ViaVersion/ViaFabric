package com.github.creeper123123321.viafabric.mixin.pipeline.client;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.handler.CommonTransformer;
import com.github.creeper123123321.viafabric.handler.FabricDecodeHandler;
import com.github.creeper123123321.viafabric.handler.FabricEncodeHandler;
import com.github.creeper123123321.viafabric.handler.clientside.ProtocolDetectionHandler;
import com.github.creeper123123321.viafabric.protocol.ViaFabricHostnameProtocol;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.viaversion.viaversion.api.connection.UserConnection;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnectionChInit {
    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            UserConnection user = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(user).add(ViaFabricHostnameProtocol.INSTANCE);

            channel.pipeline()
                    .addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new FabricEncodeHandler(user))
                    .addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new FabricDecodeHandler(user));
            if (ViaFabric.config.isClientSideEnabled()) {
                channel.pipeline().addAfter(CommonTransformer.HANDLER_ENCODER_NAME, "via-autoprotocol", new ProtocolDetectionHandler());
            }
        }
    }
}
