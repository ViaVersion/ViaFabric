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
import io.netty.handler.codec.ByteToMessageDecoder;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.util.PipelineUtil;

import java.util.List;

public class VRDecodeHandler extends ByteToMessageDecoder {
    private UserConnection user;
    public static final String NAME = "viafabric_decoder_handler";

    public VRDecodeHandler(UserConnection user) {
        this.user = user;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // Based on ViaVersion Sponge encoder code

        ByteBuf outBuf = msg.alloc().buffer().writeBytes(msg);
        try {
            // Increment sent
            user.incrementSent();
            if (user.isActive()) {
                // Handle ID
                int id = Type.VAR_INT.read(outBuf);

                if (id != PacketWrapper.PASSTHROUGH_ID) {
                    // Transform
                    PacketWrapper wrapper = new PacketWrapper(id, outBuf, user);
                    ProtocolInfo protInfo = user.get(ProtocolInfo.class);
                    protInfo.getPipeline().transform(Direction.OUTGOING, protInfo.getState(), wrapper);

                    ByteBuf newPacket = msg.alloc().buffer();
                    try {
                        wrapper.writeToBuffer(newPacket);
                        outBuf.clear();
                        outBuf.writeBytes(newPacket);
                    } finally {
                        newPacket.release();
                    }
                }
            }

            // pass to minecraft encoder
            out.add(outBuf.retain());
        } finally {
            outBuf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ProtocolInfo info = user.get(ProtocolInfo.class);
        if (info.getUuid() != null) {
            Via.getManager().removePortedClient(info.getUuid());
        }
    }
}
