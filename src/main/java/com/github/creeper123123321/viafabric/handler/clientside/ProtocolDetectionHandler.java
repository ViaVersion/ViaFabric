/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
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

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.service.ProtocolAutoDetector;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import us.myles.ViaVersion.api.Pair;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProtocolDetectionHandler extends ChannelDuplexHandler {
    private final Queue<Pair<Object, ChannelPromise>> queuedMessages = new ArrayDeque<>();
    private boolean hold = true;
    private boolean pendentFlush;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
            try {
                ScheduledFuture<?> timeoutRun = ctx.executor().schedule(() -> {
                    ViaFabric.JLOGGER.warning("Timeout for protocol auto-detection in "
                            + ctx.channel().remoteAddress() + " server");
                    hold = false;
                    drainQueue(ctx);
                    ctx.pipeline().remove(this);
                }, 10, TimeUnit.SECONDS);
                ProtocolAutoDetector.SERVER_VER.get(((InetSocketAddress) ctx.channel().remoteAddress()))
                        .whenComplete((obj, ex) -> {
                            ctx.pipeline().remove(this);
                            timeoutRun.cancel(false);
                        });
                // Let's cache it before we need it
            } catch (ExecutionException e) {
                ViaFabric.JLOGGER.warning("Protocol auto detector error: " + e);
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!hold) {
            drainQueue(ctx);
            super.write(ctx, msg, promise);
        } else {
            queuedMessages.add(new Pair<>(msg, promise));
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (!hold) {
            drainQueue(ctx);
            super.flush(ctx);
        } else {
            pendentFlush = true;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        drainQueue(ctx);
        super.channelInactive(ctx);
    }

    private void drainQueue(ChannelHandlerContext ctx) {
        queuedMessages.forEach(it -> ctx.write(it.getKey(), it.getValue()));
        queuedMessages.clear();
        if (pendentFlush) ctx.flush();
        pendentFlush = false;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        drainQueue(ctx);
        super.handlerRemoved(ctx);
    }
}
