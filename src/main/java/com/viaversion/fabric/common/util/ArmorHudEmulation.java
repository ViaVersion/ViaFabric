package com.viaversion.fabric.common.util;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_9;

import java.util.UUID;

// Stolen from https://github.com/ViaVersion/ViaFabricPlus/blob/main/src/main/java/de/florianmichael/viafabricplus/fixes/versioned/visual/ArmorHudEmulation1_8.java
public class ArmorHudEmulation {

    private static final UUID ARMOR_POINTS_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");

    private static double previousArmorPoints = 0;

    public static void sendArmorUpdate(final UserConnection userConnection, final int playerId, final int armorPoints) {
        // We only want to update the armor points if they actually changed.
        if (armorPoints == previousArmorPoints) {
            return;
        }
        previousArmorPoints = armorPoints;

        final PacketWrapper updateAttributes = PacketWrapper.create(ClientboundPackets1_9.UPDATE_ATTRIBUTES, userConnection);
        updateAttributes.write(Types.VAR_INT, playerId);
        updateAttributes.write(Types.INT, 1);
        updateAttributes.write(Types.STRING, "generic.armor");
        updateAttributes.write(Types.DOUBLE, 0.0D);
        updateAttributes.write(Types.VAR_INT, 1);
        updateAttributes.write(Types.UUID, ARMOR_POINTS_UUID);
        updateAttributes.write(Types.DOUBLE, (double) armorPoints);
        updateAttributes.write(Types.BYTE, (byte) 0);
        updateAttributes.scheduleSend(Protocol1_8To1_9.class);
    }
}
