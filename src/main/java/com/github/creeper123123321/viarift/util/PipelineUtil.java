package com.github.creeper123123321.viarift.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class PipelineUtil {
    public static ChannelHandlerContext getContextBefore(String name, ChannelPipeline pipe) {
        String previous = null;
        for (String current : pipe.names()) {
            if (name.equals(current))
                break;
            previous = current;
        }
        return pipe.context(previous);
    }
}
