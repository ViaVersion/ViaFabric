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

package com.github.creeper123123321.viafabric.handler.serverside;

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
import java.util.List;

public class FabricDecodeHandler extends ByteToMessageDecoder {
    // https://github.com/MylesIsCool/ViaVersion/blob/1abc3ebea66878192b08b577353dff7d619ed51e/sponge/src/main/java/us/myles/ViaVersion/sponge/handlers/SpongeDecodeHandler.java
    private final ByteToMessageDecoder minecraftDecoder;
    private final UserConnection info;

    public FabricDecodeHandler(UserConnection info, ByteToMessageDecoder minecraftDecoder) {
        this.info = info;
        this.minecraftDecoder = minecraftDecoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> list) throws Exception {
        // use transformers
        if (bytebuf.readableBytes() > 0) {
            // Ignore if pending disconnect
            if (info.isPendingDisconnect()) {
                return;
            }
            // Increment received
            boolean second = info.incrementReceived();
            // Check PPS
            if (second) {
                if (info.handlePPS())
                    return;
            }

            if (info.isActive()) {
                // Handle ID
                int id = Type.VAR_INT.read(bytebuf);
                // Transform
                ByteBuf newPacket = ctx.alloc().buffer();
                try {
                    if (id == PacketWrapper.PASSTHROUGH_ID) {
                        newPacket.writeBytes(bytebuf);
                    } else {
                        PacketWrapper wrapper = new PacketWrapper(id, bytebuf, info);
                        ProtocolInfo protInfo = info.get(ProtocolInfo.class);
                        protInfo.getPipeline().transform(Direction.INCOMING, protInfo.getState(), wrapper);
                        wrapper.writeToBuffer(newPacket);
                    }

                    bytebuf.clear();
                    bytebuf = newPacket;
                } catch (Exception e) {
                    // Clear Buffer
                    bytebuf.clear();
                    // Release Packet, be free!
                    newPacket.release();
                    throw e;
                }
            }

            // call minecraft decoder
            try {
                list.addAll(PipelineUtil.callDecode(this.minecraftDecoder, ctx, bytebuf));
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof Exception) {
                    throw (Exception) e.getCause();
                }
            } finally {
                if (info.isActive()) {
                    bytebuf.release();
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelException.class)) return;
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ProtocolInfo pi = info.get(ProtocolInfo.class);
        if (pi.getUuid() != null) {
            Via.getManager().removePortedClient(pi.getUuid());
        }
    }
}