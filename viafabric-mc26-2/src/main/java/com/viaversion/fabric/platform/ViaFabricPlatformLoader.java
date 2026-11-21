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
package com.viaversion.fabric.platform;

import com.viaversion.fabric.common.provider.FabricSessionAuthenticationProvider;
import com.viaversion.fabric.providers.FabricVersionProvider;
import com.viaversion.fabric.providers.ViaFabricConnectionOptionsProvider;
import com.viaversion.fabric.providers.ViaFabricHandItemProvider;
import com.viaversion.fabric.providers.ViaFabricKeyExchangeProvider;
import com.viaversion.fabric.providers.ViaFabricPlayerAbilitiesProvider;
import com.viaversion.fabric.providers.ViaFabricPlayerLookTargetProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.provider.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.v1_15_2to1_16.provider.PlayerAbilitiesProvider;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.ConnectionOptionsProvider;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.KeyExchangeProvider;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.SessionAuthenticationProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class ViaFabricPlatformLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(VersionProvider.class, new FabricVersionProvider());

        if (Via.getPlatform().getConf().isItemCache()) {
            ViaFabricHandItemProvider handProvider = new ViaFabricHandItemProvider();
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                handProvider.registerClientTick();
            }
            Via.getManager().getProviders().use(HandItemProvider.class, handProvider);
        }

        Via.getManager().getProviders().use(PlayerAbilitiesProvider.class, new ViaFabricPlayerAbilitiesProvider());
        Via.getManager().getProviders().use(PlayerLookTargetProvider.class, new ViaFabricPlayerLookTargetProvider());
        Via.getManager().getProviders().use(SessionAuthenticationProvider.class, FabricSessionAuthenticationProvider.INSTANCE);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Via.getManager().getProviders().use(ConnectionOptionsProvider.class, new ViaFabricConnectionOptionsProvider());
            Via.getManager().getProviders().use(KeyExchangeProvider.class, new ViaFabricKeyExchangeProvider());
        }
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
