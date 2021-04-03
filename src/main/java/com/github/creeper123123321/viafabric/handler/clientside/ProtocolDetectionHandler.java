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
            ScheduledFuture<?> timeoutRun = ctx.executor().schedule(() -> {
                ViaFabric.JLOGGER.warning("Timeout for protocol auto-detection in "
                        + ctx.channel().remoteAddress() + " server");
                hold = false;
                drainQueue(ctx);
                ctx.pipeline().remove(this);
            }, 10, TimeUnit.SECONDS);
            ProtocolAutoDetector.detectVersion(((InetSocketAddress) ctx.channel().remoteAddress()))
                    .whenComplete((obj, ex) -> {
                        ctx.pipeline().remove(this);
                        timeoutRun.cancel(false);
                    });
            // Let's cache it before we need it
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
