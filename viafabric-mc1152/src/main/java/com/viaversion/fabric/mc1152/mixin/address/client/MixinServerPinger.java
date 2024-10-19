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
package com.viaversion.fabric.mc1152.mixin.address.client;

import com.viaversion.fabric.common.AddressParser;
import net.minecraft.client.network.MultiplayerServerListPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(MultiplayerServerListPinger.class)
public class MixinServerPinger {
    @Redirect(method = "add", at = @At(value = "INVOKE",
            target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
    private static InetAddress resolveViaFabricAddr(String address) throws UnknownHostException {
        AddressParser viaAddr = AddressParser.parse(address);
        if (!viaAddr.hasViaMetadata()) {
            return InetAddress.getByName(address);
        }

        return viaAddr.resolve();
    }
}
