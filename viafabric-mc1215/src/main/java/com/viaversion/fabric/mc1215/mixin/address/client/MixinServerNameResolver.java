/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
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
package com.viaversion.fabric.mc1215.mixin.address.client;

import com.viaversion.fabric.common.AddressParser;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerNameResolver.class)
public abstract class MixinServerNameResolver {

    @Shadow
    public abstract Optional<ResolvedServerAddress> resolveAddress(final ServerAddress serverAddress);

    @Inject(method = "resolveAddress", at = @At(value = "HEAD"), cancellable = true)
    private void resolveVF(ServerAddress address, CallbackInfoReturnable<Optional<ResolvedServerAddress>> cir) {
        AddressParser viaAddr = new AddressParser().parse(address.getHost());
        if (viaAddr.viaSuffix == null) {
            return;
        }

        ServerAddress realAddress = new ServerAddress(viaAddr.serverAddress, address.getPort());

        cir.setReturnValue(resolveAddress(realAddress).map(it -> viaFabric$addSuffix(it, viaAddr.getSuffixWithOptions())));
    }

    @Unique
    private ResolvedServerAddress viaFabric$addSuffix(ResolvedServerAddress it, String viaSuffix) {
        try {
            return ResolvedServerAddress.from(new InetSocketAddress(
                InetAddress.getByAddress(it.getHostName() + "." + viaSuffix,
                    it.asInetSocketAddress().getAddress().getAddress()), it.getPort()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
