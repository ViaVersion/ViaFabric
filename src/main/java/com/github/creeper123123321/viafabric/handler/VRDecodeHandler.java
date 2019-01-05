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

package com.github.creeper123123321.viafabric.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class VRDecodeHandler extends ByteToMessageDecoder {
    private UserConnection user;
    private ByteToMessageDecoder minecraftDecoder;

    public VRDecodeHandler(UserConnection user, ByteToMessageDecoder minecraftDecoder) {
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
                    buf.release();
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
        if (PipelineUtil.containsCause(cause, CancelException.class)) {
            if (user.isActive()) {
                for (Runnable runnable : user.getPostProcessingTasks().get().pollLast()) {
                    runnable.run();
                }
            }
            return;
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        user.getPostProcessingTasks().get().addLast(new ArrayList<>());
        super.channelRead(ctx, msg);
        if (user.isActive()) {
            for (Runnable runnable : user.getPostProcessingTasks().get().pollLast()) {
                runnable.run();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProtocolInfo info = user.get(ProtocolInfo.class);
        if (info.getUuid() != null) {
            Via.getManager().removePortedClient(info.getUuid());
        }
    }
}
