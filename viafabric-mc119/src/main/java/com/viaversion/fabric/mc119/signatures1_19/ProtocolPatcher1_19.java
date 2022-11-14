package com.viaversion.fabric.mc119.signatures1_19;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.Protocol1_19_1To1_19;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

// This class modifies the 1.19.1 -> 1.19.0 transformers so that they simply pass through the original Minecraft key.
public class ProtocolPatcher1_19 {
    public static boolean shouldPatchKeys = false;

    public static void patchIfClient() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;

        Protocol1_19_1To1_19 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_19_1To1_19.class);
        if (protocol == null) throw new AssertionError();

        protocol.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.OPTIONAL_PROFILE_KEY);
                this.read(Type.OPTIONAL_UUID);
            }
        }, true);

        protocol.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
            }
        }, true);

        protocol.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
            }
        }, true);
    }
}
