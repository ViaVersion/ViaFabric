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
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

public class CommonTransformer {
    public static final String HANDLER_DECODER_NAME = "viafabric_decoder_handler";
    public static final String HANDLER_ENCODER_NAME = "viafabric_encoder_handler";

    public static void transformClientbound(ByteBuf draft, UserConnection user) throws Exception {
        if (!draft.isReadable()) return;
        // Increment sent
        user.incrementSent();
        transform(draft, user, Direction.OUTGOING);
    }

    public static void transformServerbound(ByteBuf draft, UserConnection user) throws Exception {
        if (!draft.isReadable()) return;
        // Ignore if pending disconnect
        if (user.isPendingDisconnect()) return;
        // Increment received + Check PPS
        if (user.incrementReceived() && user.handlePPS()) return;
        transform(draft, user, Direction.INCOMING);
    }

    private static void transform(ByteBuf draft, UserConnection user, Direction direction) throws Exception {
        if (!user.isActive()) return;

        int id = Type.VAR_INT.read(draft);
        if (id == PacketWrapper.PASSTHROUGH_ID) return;
        PacketWrapper wrapper = new PacketWrapper(id, draft, user);
        ProtocolInfo protInfo = user.get(ProtocolInfo.class);
        protInfo.getPipeline().transform(direction, protInfo.getState(), wrapper);
        ByteBuf transformed = draft.alloc().buffer();
        try {
            wrapper.writeToBuffer(transformed);
            draft.clear().writeBytes(transformed);
        } finally {
            transformed.release();
        }
    }
}
