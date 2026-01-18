/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.common.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import com.viaversion.viaversion.platform.ViaEncodeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class FabricDecodeHandler extends ViaDecodeHandler {

    public FabricDecodeHandler(final UserConnection connection) {
        super(connection);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        boolean kryptonReorder = false;
        switch (evt.toString()) {
            case "COMPRESSION_THRESHOLD_UPDATED":
            case "COMPRESSION_ENABLED":
                kryptonReorder = true;
        }
        if (evt instanceof PipelineReorderEvent || kryptonReorder) {
            reorder(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }

    private void reorder(ChannelHandlerContext ctx) {
        int decoderIndex = ctx.pipeline().names().indexOf("decompress");
        if (decoderIndex == -1) {
            return;
        }

        if (decoderIndex > ctx.pipeline().names().indexOf(ViaDecodeHandler.NAME)) {
            ChannelHandler encoder = ctx.pipeline().get(ViaEncodeHandler.NAME);
            ChannelHandler decoder = ctx.pipeline().get(ViaDecodeHandler.NAME);

            ctx.pipeline().remove(encoder);
            ctx.pipeline().remove(decoder);

            ctx.pipeline().addAfter("compress", ViaEncodeHandler.NAME, encoder);
            ctx.pipeline().addAfter("decompress", ViaDecodeHandler.NAME, decoder);
        }
    }

}
