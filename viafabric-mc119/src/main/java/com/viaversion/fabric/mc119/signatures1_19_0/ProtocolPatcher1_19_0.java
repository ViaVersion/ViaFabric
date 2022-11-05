package com.viaversion.fabric.mc119.signatures1_19_0;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.Protocol1_19_1To1_19;

// This class modifies the 1.19.1 -> 1.19.0 transformers so that they simply pass through the original Minecraft key.
public class ProtocolPatcher1_19_0 {

    public static boolean shouldFixKeys = false;

    public static void patch() {
        final Protocol1_19_1To1_19 protocol1_19_1To1_19 = Via.getManager().getProtocolManager().getProtocol(Protocol1_19_1To1_19.class);

        assert protocol1_19_1To1_19 != null;
        protocol1_19_1To1_19.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.OPTIONAL_PROFILE_KEY);
                this.read(Type.OPTIONAL_UUID);
            }
        }, true);

        protocol1_19_1To1_19.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
            }
        }, true);

        protocol1_19_1To1_19.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
            }
        }, true);
    }
}
