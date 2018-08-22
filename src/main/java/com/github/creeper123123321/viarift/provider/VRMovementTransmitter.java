package com.github.creeper123123321.viarift.provider;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.MovementTracker;

public class VRMovementTransmitter extends MovementTransmitterProvider {
    @Override
    public Object getFlyingPacket() {
        return null;
    }

    @Override
    public Object getGroundPacket() {
        return null;
    }

    @Override
    public void sendPlayer(UserConnection userConnection) {
        // Based on https://github.com/Gerrygames/ClientViaVersion/blob/master/src/main/java/de/gerrygames/the5zig/clientviaversion/providers/ClientMovementTransmitterProvider.java
        if (userConnection.get(ProtocolInfo.class).getState() != State.PLAY) return;

        PacketWrapper packet = new PacketWrapper(0x03, null, userConnection);
        packet.write(Type.BOOLEAN, userConnection.get(MovementTracker.class).isGround());

        ByteBuf buf = userConnection.getChannel().alloc().buffer();

        try {
            packet.writeToBuffer(buf);
            userConnection.getChannel().pipeline().context("encoder").writeAndFlush(buf);
            userConnection.get(MovementTracker.class).incrementIdlePacket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
