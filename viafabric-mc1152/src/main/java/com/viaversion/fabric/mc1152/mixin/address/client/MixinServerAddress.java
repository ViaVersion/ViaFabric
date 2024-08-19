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

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.viaversion.fabric.common.AddressParser;
import net.minecraft.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {
    @ModifyVariable(method = "parse", at = @At("HEAD"), argsOnly = true)
    private static String modifyAddress(String address, @Share("via") LocalRef<AddressParser> via) {
        AddressParser parser = AddressParser.parse(address);
        via.set(parser);

        return parser.toAddress();
    }

    @ModifyArg(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ServerAddress;<init>(Ljava/lang/String;I)V"))
    private static String injectViaMetadata(String original, int port, @Share("via") LocalRef<AddressParser> via) {
        final AddressParser parser = via.get();
        if (parser == null) {
            return original;
        }
        return parser.addAddressSuffix(original);
    }
}
