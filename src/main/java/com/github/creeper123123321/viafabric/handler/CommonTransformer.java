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
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.Direction;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

import java.util.function.Function;

// TODO delete this when https://github.com/ViaVersion/ViaVersion/pull/1505 is merged
public class CommonTransformer {
    public static final String HANDLER_DECODER_NAME = "via-decoder";
    public static final String HANDLER_ENCODER_NAME = "via-encoder";

    public static boolean preServerboundCheck(UserConnection user) {
        // Ignore if pending disconnect
        if (user.isPendingDisconnect()) return true;
        // Increment received + Check PPS
        return user.incrementReceived() && user.handlePPS();
    }

    public static void preClientbound(UserConnection user) {
        user.incrementSent();
    }

    public static boolean willTransformPacket(UserConnection user) {
        return user.isActive();
    }

    public static void transformClientbound(ByteBuf draft, UserConnection user, Function<Throwable, Exception> cancelSupplier) throws Exception {
        if (!draft.isReadable()) return;
        transform(draft, user, Direction.OUTGOING, cancelSupplier);
    }

    public static void transformServerbound(ByteBuf draft, UserConnection user, Function<Throwable, Exception> cancelSupplier) throws Exception {
        if (!draft.isReadable()) return;
        transform(draft, user, Direction.INCOMING, cancelSupplier);
    }

    private static void transform(ByteBuf draft, UserConnection user, Direction direction, Function<Throwable, Exception> cancelSupplier) throws Exception {
        int id = Type.VAR_INT.read(draft);
        if (id == PacketWrapper.PASSTHROUGH_ID) return;
        PacketWrapper wrapper = new PacketWrapper(id, draft, user);
        ProtocolInfo protInfo = user.get(ProtocolInfo.class);
        try {
            protInfo.getPipeline().transform(direction, protInfo.getState(), wrapper);
        } catch (CancelException ex) {
            throw cancelSupplier.apply(ex);
        }
        ByteBuf transformed = draft.alloc().buffer();
        try {
            wrapper.writeToBuffer(transformed);
            draft.clear().writeBytes(transformed);
        } finally {
            transformed.release();
        }
    }
}