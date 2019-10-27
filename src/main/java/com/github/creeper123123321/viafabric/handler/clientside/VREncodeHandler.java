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

package com.github.creeper123123321.viafabric.handler.clientside;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

public class VREncodeHandler extends MessageToByteEncoder {
    private UserConnection user;
    public static String NAME = "viafabric_encoder_handler";

    public VREncodeHandler(UserConnection user) {
        this.user = user;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // Based on Sponge ViaVersion decoder code
        if (!(msg instanceof ByteBuf)) throw new EncoderException("Received msg isn't ByteBuf");

        ByteBuf outBuffer = ctx.alloc().buffer().writeBytes((ByteBuf) msg);

        try {
            // use transformers
            if (outBuffer.readableBytes() > 0) {
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
                    int id = Type.VAR_INT.read(outBuffer);
                    // Transform
                    if (id != PacketWrapper.PASSTHROUGH_ID) {
                        PacketWrapper wrapper = new PacketWrapper(id, outBuffer, user);
                        ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                        protInfo.getPipeline().transform(Direction.INCOMING, protInfo.getState(), wrapper);

                        ByteBuf newPacket = outBuffer.alloc().buffer();
                        try {
                            wrapper.writeToBuffer(newPacket);
                            outBuffer.clear();
                            outBuffer.writeBytes(newPacket);
                        } finally {
                            newPacket.release();
                        }
                    }
                }
                out.writeBytes(outBuffer);
            }
        } finally {
            outBuffer.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }
}
