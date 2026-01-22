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
package com.viaversion.fabric.common.protocol;

import com.google.common.collect.Lists;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;
import com.viaversion.viaversion.util.Key;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ViaFabricProtocolBase<CU extends ClientboundPacketType, CM extends ClientboundPacketType, SM extends ServerboundPacketType, SU extends ServerboundPacketType> extends AbstractProtocol<CU, CM, SM, SU> {

    public ViaFabricProtocolBase(final Class<CU> unmappedClientboundPacketType, final Class<CM> mappedClientboundPacketType, final Class<SM> mappedServerboundPacketType, final Class<SU> unmappedServerboundPacketType) {
        super(unmappedClientboundPacketType, mappedClientboundPacketType, mappedServerboundPacketType, unmappedServerboundPacketType);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        if (Via.getPlatform().isProxy()) {
            registerServerbound(State.HANDSHAKE, ServerboundHandshakePackets.CLIENT_INTENTION, wrapper -> {
                wrapper.passthrough(Types.VAR_INT); // Protocol version

                final String address = wrapper.read(Types.STRING);
                wrapper.write(Types.STRING, AddressParser.parse(address).serverAddress());
            });
        }

        final ServerboundPacketType customPayload = packetTypesProvider.unmappedServerboundType(State.CONFIGURATION, "CUSTOM_PAYLOAD");
        if (customPayload == null) {
            return;
        }

        // Fixes an issue where the Fabric Particle API causes disconnects when both the client and server have the mod installed and both are 1.21.5+.
        // See https://github.com/ViaVersion/ViaFabric/issues/428
        registerServerbound(State.CONFIGURATION, customPayload, wrapper -> {
            final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
            if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                final List<String> channels = Lists.newArrayList(new String(wrapper.passthrough(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA), StandardCharsets.UTF_8).split("\0"));
                if (channels.remove("fabric:extended_block_state_particle_effect_sync")) {
                    if (!channels.isEmpty()) {
                        wrapper.set(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA, 0, String.join("\0", channels).getBytes(StandardCharsets.UTF_8));
                    } else {
                        wrapper.cancel();
                    }
                }
            }
        });
    }

    public ClientboundPacketType getClientboundCustomPayloadPacketType() {
        return packetTypesProvider.unmappedClientboundType(State.PLAY, "CUSTOM_PAYLOAD");
    }

    public ServerboundPacketType getCustomPayloadPacketType() {
        return packetTypesProvider.unmappedServerboundType(State.PLAY, "CUSTOM_PAYLOAD");
    }

}
