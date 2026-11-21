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
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v26_3to26_4.crypto.LegacyEncryption;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.LegacyKeyExchangeProvider;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class ViaFabricLegacyKeyExchangeProvider implements LegacyKeyExchangeProvider {

    @Override
    public void handleKeyExchange(final UserConnection connection, final LegacyKeyExchange keyExchange) {
        if (!keyExchange.shouldAuthenticate()) {
            sendResponse(connection, keyExchange);
            return;
        }

        Util.ioPool().execute(() -> {
            final Minecraft minecraft = Minecraft.getInstance();
            final User user = minecraft.getUser();
            try {
                minecraft.services().sessionService().joinServer(user.getProfileId(), user.getAccessToken(), keyExchange.serverHash());
            } catch (final AuthenticationException e) {
                vanillaConnection(connection).disconnect(Component.translatable("disconnect.loginFailedInfo", e.getMessage()));
                return;
            }

            connection.getChannel().eventLoop().execute(() -> sendResponse(connection, keyExchange));
        });
    }

    private static void sendResponse(final UserConnection connection, final LegacyKeyExchange keyExchange) {
        keyExchange.sendResponse();
        try {
            vanillaConnection(connection).setEncryptionKey(
                LegacyEncryption.cipher(Cipher.DECRYPT_MODE, keyExchange.secretKey()),
                LegacyEncryption.cipher(Cipher.ENCRYPT_MODE, keyExchange.secretKey())
            );
        } catch (final GeneralSecurityException e) {
            throw new IllegalStateException("Failed to enable encryption", e);
        }
    }

    private static Connection vanillaConnection(final UserConnection connection) {
        return (Connection) connection.getChannel().pipeline().get(HandlerNames.PACKET_HANDLER);
    }
}
