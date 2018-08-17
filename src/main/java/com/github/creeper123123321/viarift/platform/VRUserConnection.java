package com.github.creeper123123321.viarift.platform;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.util.PipelineUtil;


public class VRUserConnection extends UserConnection {
    public VRUserConnection(SocketChannel socketChannel) {
        super(socketChannel);
    }
    // Based on https://github.com/Gerrygames/ClientViaVersion/blob/master/src/main/java/de/gerrygames/the5zig/clientviaversion/reflection/Injector.java

    @Override
    public void sendRawPacket(final ByteBuf packet, boolean currentThread) {
        ByteBuf copy = packet.alloc().buffer();
        try {
            Type.VAR_INT.write(copy, PacketWrapper.PASSTHROUGH_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        copy.writeBytes(packet);
        packet.release();
        final Channel channel = this.getChannel();
        if (currentThread) {
            try {
                PipelineUtil.getContextBefore("decoder", channel.pipeline()).fireChannelRead(copy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            channel.eventLoop().submit(() -> {
                try {
                    PipelineUtil.getContextBefore("decoder", channel.pipeline()).fireChannelRead(copy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public ChannelFuture sendRawPacketFuture(ByteBuf packet) {
        ByteBuf copy = packet.alloc().buffer();
        try {
            Type.VAR_INT.write(copy, PacketWrapper.PASSTHROUGH_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        copy.writeBytes(packet);
        packet.release();
        final Channel channel = this.getChannel();
        try {
            PipelineUtil.getContextBefore("decoder", channel.pipeline()).fireChannelRead(copy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
