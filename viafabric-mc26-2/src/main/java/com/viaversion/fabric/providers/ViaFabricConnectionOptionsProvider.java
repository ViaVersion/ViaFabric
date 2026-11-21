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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.ConnectionOptionsProvider;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;

public class ViaFabricConnectionOptionsProvider implements ConnectionOptionsProvider {

    @Override
    public void applyOutbound(final UserConnection connection, final byte options, final int compressionThreshold) {
        final Connection vanillaConnection = (Connection) connection.getChannel().pipeline().get(HandlerNames.PACKET_HANDLER);
        if ((options & COMPRESSION) != 0) {
            vanillaConnection.setupCompression(compressionThreshold, false);
        }

        if ((options & ENCRYPTION) != 0) {
            final ViaFabricKeyExchangeProvider.SessionKeysStorage storage = connection.remove(ViaFabricKeyExchangeProvider.SessionKeysStorage.class);
            if (storage == null) {
                throw new IllegalStateException("Session keys not initialized");
            }

            try {
                vanillaConnection.setEncryptionKey(storage.sessionKeys().clientboundCipher(Cipher.DECRYPT_MODE), storage.sessionKeys().serverboundCipher(Cipher.ENCRYPT_MODE));
            } catch (final GeneralSecurityException e) {
                throw new IllegalStateException("Failed to enable encryption", e);
            }
        }
    }
}
