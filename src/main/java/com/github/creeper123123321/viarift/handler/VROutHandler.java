package com.github.creeper123123321.viarift.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

import java.lang.reflect.InvocationTargetException;

public class VROutHandler extends MessageToByteEncoder {
    private UserConnection user;
    private MessageToByteEncoder minecraftEncoder;

    public VROutHandler(UserConnection user, MessageToByteEncoder minecraftEncoder) {
        this.user = user;
        this.minecraftEncoder = minecraftEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // Based on Sponge ViaVersion decoder code

        ByteBuf pre = out.alloc().buffer();

        // call minecraft encoder
        try {
            PipelineUtil.callEncode(this.minecraftEncoder, ctx, msg, pre);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
        }

        // use transformers
        if (pre.readableBytes() > 0) {
            // Ignore if pending disconnect
            if (user.isPendingDisconnect()) {
                return;
            }
            // Increment received
            boolean second = user.incrementReceived();
            // Check PPS
            if (second) {
                if (user.handlePPS())
                    return;
            }

            if (user.isActive()) {
                // Handle ID
                int id = Type.VAR_INT.read(pre);
                // Transform
                ByteBuf oldPacket = pre.copy();
                try {
                    if (id != PacketWrapper.PASSTHROUGH_ID) {
                        PacketWrapper wrapper = new PacketWrapper(id, oldPacket, user);
                        ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                        protInfo.getPipeline().transform(Direction.INCOMING, protInfo.getState(), wrapper);
                        pre.clear();
                        wrapper.writeToBuffer(pre);
                    }
                } catch (Exception e) {
                    if (!(e instanceof CancelException))
                        e.printStackTrace();
                    throw e;
                } finally {
                    oldPacket.release();
                }
            }
        }

        out.writeBytes(pre);
        pre.release();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }
}
