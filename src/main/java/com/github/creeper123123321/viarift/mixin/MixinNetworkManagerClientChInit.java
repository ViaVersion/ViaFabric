package com.github.creeper123123321.viarift.mixin;

import com.github.creeper123123321.viarift.handler.VRInHandler;
import com.github.creeper123123321.viarift.handler.VROutHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolPipeline;

@Mixin(targets = "net.minecraft.network.NetworkManager$1")
public class MixinNetworkManagerClientChInit {
    @Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At(value = "TAIL"))
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        System.out.println(channel);
        if (channel instanceof SocketChannel) {
            UserConnection user = new UserConnection((SocketChannel) channel);
            new ProtocolPipeline(user);

            MessageToByteEncoder oldEncoder = (MessageToByteEncoder) channel.pipeline().get("encoder");
            ByteToMessageDecoder oldDecoder = (ByteToMessageDecoder) channel.pipeline().get("decoder");

            channel.pipeline().replace("encoder", "encoder", new VROutHandler(user, oldEncoder));
            channel.pipeline().replace("decoder", "decoder", new VRInHandler(user, oldDecoder));
        }
    }
}
