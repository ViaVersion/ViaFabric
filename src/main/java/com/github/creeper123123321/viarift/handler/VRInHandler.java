package com.github.creeper123123321.viarift.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class VRInHandler extends ByteToMessageDecoder {
    private UserConnection user;
    private ByteToMessageDecoder minecraftDecoder;

    public VRInHandler(UserConnection user, ByteToMessageDecoder minecraftDecoder) {
        this.user = user;
        this.minecraftDecoder = minecraftDecoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // Based on ViaVersion Sponge encoder code

        // Increment sent
        user.incrementSent();
        if (user.isActive()) {
            // Handle ID
            int id = Type.VAR_INT.read(msg);
            // Transform
            ByteBuf newPacket = msg.alloc().buffer();

            if (id != PacketWrapper.PASSTHROUGH_ID) {
                try {
                    PacketWrapper wrapper = new PacketWrapper(id, msg, user);
                    ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                    protInfo.getPipeline().transform(Direction.OUTGOING, protInfo.getState(), wrapper);
                    wrapper.writeToBuffer(newPacket);
                    msg = newPacket;
                } catch (Exception e) {
                    if (!(e instanceof CancelException))
                        e.printStackTrace();
                    msg.clear();
                    throw e;
                }
            }
        }

        // call minecraft encoder
        try {
            out.addAll(PipelineUtil.callDecode(this.minecraftDecoder, ctx, msg));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }
}
