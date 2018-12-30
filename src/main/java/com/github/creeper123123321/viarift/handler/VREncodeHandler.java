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

package com.github.creeper123123321.viarift.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class VREncodeHandler extends MessageToByteEncoder {
    private UserConnection user;
    private MessageToByteEncoder minecraftEncoder;

    public VREncodeHandler(UserConnection user, MessageToByteEncoder minecraftEncoder) {
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
            if (second && user.handlePPS())
                return;

            if (user.isActive()) {
                // Handle ID
                int id = Type.VAR_INT.read(pre);
                // Transform
                ByteBuf newPacket = pre.alloc().buffer();
                try {
                    if (id != PacketWrapper.PASSTHROUGH_ID) {
                        PacketWrapper wrapper = new PacketWrapper(id, pre, user);
                        ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                        protInfo.getPipeline().transform(Direction.INCOMING, protInfo.getState(), wrapper);
                        wrapper.writeToBuffer(newPacket);
                        pre.clear();
                        pre.writeBytes(newPacket);
                    }
                } catch (Exception e) {
                    pre.release();
                    throw e;
                } finally {
                    newPacket.release();
                }
            }
        }

        out.writeBytes(pre);
        pre.release();
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
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        user.getPostProcessingTasks().get().addLast(new ArrayList<>());
        super.write(ctx, msg, promise);
        if (user.isActive()) {
            for (Runnable runnable : user.getPostProcessingTasks().get().pollLast()) {
                runnable.run();
            }
        }
    }
}
