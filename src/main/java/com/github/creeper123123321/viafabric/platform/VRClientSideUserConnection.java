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

package com.github.creeper123123321.viafabric.platform;

import com.github.creeper123123321.viafabric.handler.clientside.VRDecodeHandler;
import com.github.creeper123123321.viafabric.handler.clientside.VREncodeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.util.PipelineUtil;


public class VRClientSideUserConnection extends UserConnection {
    public VRClientSideUserConnection(Channel socketChannel) {
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
            PipelineUtil.getPreviousContext(VRDecodeHandler.NAME, channel.pipeline()).fireChannelRead(copy);
        } else {
            channel.eventLoop().submit(() -> {
                PipelineUtil.getPreviousContext(VRDecodeHandler.NAME, channel.pipeline()).fireChannelRead(copy);
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
        PipelineUtil.getPreviousContext(VRDecodeHandler.NAME, channel.pipeline()).fireChannelRead(copy);
        return channel.newSucceededFuture();
    }

    @Override
    public void sendRawPacketToServer(ByteBuf packet, boolean currentThread) {
        if (currentThread) {
            getChannel().pipeline().context(VREncodeHandler.NAME).writeAndFlush(packet);
        } else {
            getChannel().eventLoop().submit(() -> {
                getChannel().pipeline().context(VREncodeHandler.NAME).writeAndFlush(packet);
            });
        }
    }
}
