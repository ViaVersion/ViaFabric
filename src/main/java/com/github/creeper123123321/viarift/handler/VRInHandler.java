package com.github.creeper123123321.viarift.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
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

        ByteBuf buf = msg.alloc().buffer().writeBytes(msg);

        // Increment sent
        user.incrementSent();
        if (user.isActive()) {
            // Handle ID
            int id = Type.VAR_INT.read(buf);

            if (id != PacketWrapper.PASSTHROUGH_ID) {
                // Transform
                ByteBuf newPacket = buf.alloc().buffer();
                try {
                    PacketWrapper wrapper = new PacketWrapper(id, buf, user);
                    ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                    protInfo.getPipeline().transform(Direction.OUTGOING, protInfo.getState(), wrapper);
                    wrapper.writeToBuffer(newPacket);
                    buf.clear();
                    buf.writeBytes(newPacket);
                } catch (Exception e) {
                    buf.clear();
                    throw e;
                } finally {
                    newPacket.release();
                }
            }
        }

        // call minecraft encoder
        try {
            out.addAll(PipelineUtil.callDecode(this.minecraftDecoder, ctx, buf));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
        }
        buf.release();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }
}
