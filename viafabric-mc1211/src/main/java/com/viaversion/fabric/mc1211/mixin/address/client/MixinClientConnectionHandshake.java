/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
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
package com.viaversion.fabric.mc121.mixin.address.client;

import com.viaversion.fabric.common.AddressParser;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Minimally invasive address sanitizer.
 * <p>
 * Placed in ClientConnection to prevent sanitizing input from LAN clients.
 **/
@Mixin(ClientConnection.class)
public class MixinClientConnectionHandshake {
    @ModifyArg(method = "method_52900", at = @At(value = "INVOKE", target = "net/minecraft/network/packet/c2s/handshake/HandshakeC2SPacket.<init>(ILjava/lang/String;ILnet/minecraft/network/packet/c2s/handshake/ConnectionIntent;)V"))
    private static String removeViaMetadataFromAddress(String address) {
        return AddressParser.parse(address).toAddress();
    }
}
