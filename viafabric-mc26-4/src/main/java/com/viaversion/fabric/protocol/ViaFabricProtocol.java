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
package com.viaversion.fabric.protocol;

import com.viaversion.fabric.common.protocol.ViaFabricProtocolBase;
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypesProvider;
import com.viaversion.viaversion.api.protocol.packet.provider.SimplePacketTypesProvider;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundConfigurationPackets1_21_9;

import static com.viaversion.viaversion.util.ProtocolUtil.packetTypeMap;

public final class ViaFabricProtocol extends ViaFabricProtocolBase<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> {

    public static final ViaFabricProtocol INSTANCE = new ViaFabricProtocol();

    public ViaFabricProtocol() {
        super(ClientboundPacket26_1.class, ClientboundPacket26_1.class, ServerboundPacket26_1.class, ServerboundPacket26_1.class);
    }

    @Override
    protected PacketTypesProvider<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> createPacketTypesProvider() {
        return new SimplePacketTypesProvider<>(
            packetTypeMap(unmappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class),
            packetTypeMap(unmappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class)
        );
    }

}
