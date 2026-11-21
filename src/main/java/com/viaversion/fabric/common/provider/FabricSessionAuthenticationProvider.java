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
package com.viaversion.fabric.common.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.v26_3to26_4.provider.SessionAuthenticationProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FabricSessionAuthenticationProvider implements SessionAuthenticationProvider {

    public static final FabricSessionAuthenticationProvider INSTANCE = new FabricSessionAuthenticationProvider();
    private final Map<String, String> redirects = new ConcurrentHashMap<>();

    private FabricSessionAuthenticationProvider() {
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public void redirectSessionCheck(final UserConnection connection, final String serverHash, final String sessionHash) {
        redirects.put(serverHash, sessionHash);
        connection.getChannel().closeFuture().addListener(future -> redirects.remove(serverHash));
    }

    /**
     * Returns the hash the server should check the session with.
     *
     * @param serverHash hash the server computed
     * @return hash the client joined the session server with, or the given hash if not redirected
     */
    public String redirectedHash(final String serverHash) {
        final String sessionHash = redirects.remove(serverHash);
        return sessionHash != null ? sessionHash : serverHash;
    }
}
