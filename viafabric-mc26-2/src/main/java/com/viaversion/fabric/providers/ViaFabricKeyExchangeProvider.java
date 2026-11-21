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
package com.viaversion.fabric.providers;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v26_3to26_4.crypto.SessionKeys26_4;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.KeyExchangeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class ViaFabricKeyExchangeProvider implements KeyExchangeProvider {

    @Override
    public void handleKeyExchange(final UserConnection connection, final KeyExchange keyExchange) {
        connection.put(new SessionKeysStorage(keyExchange.sessionKeys()));
        if (!keyExchange.shouldAuthenticate()) {
            keyExchange.sendResponse();
            return;
        }

        Util.ioPool().execute(() -> {
            final Minecraft minecraft = Minecraft.getInstance();
            final User user = minecraft.getUser();
            try {
                minecraft.services().sessionService().joinServer(user.getProfileId(), user.getAccessToken(), keyExchange.sessionKeys().authenticationDigest());
            } catch (final AuthenticationException e) {
                final Connection vanillaConnection = (Connection) connection.getChannel().pipeline().get(HandlerNames.PACKET_HANDLER);
                vanillaConnection.disconnect(Component.translatable("disconnect.loginFailedInfo", e.getMessage()));
                return;
            }

            connection.getChannel().eventLoop().execute(keyExchange::sendResponse);
        });
    }

    public record SessionKeysStorage(SessionKeys26_4 sessionKeys) implements StorableObject {
    }
}
