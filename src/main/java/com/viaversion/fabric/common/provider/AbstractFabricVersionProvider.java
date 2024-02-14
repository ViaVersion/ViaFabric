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
package com.viaversion.fabric.common.provider;

import com.google.common.primitives.Ints;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_16;
import com.viaversion.viaversion.protocols.base.BaseProtocol1_7;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.base.ClientboundStatusPackets;
import io.netty.channel.ChannelPipeline;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public abstract class AbstractFabricVersionProvider extends BaseVersionProvider {
    private int[] multiconnectSupportedVersions = null;

    {
        multiconnectIntegration();
    }

    private void multiconnectIntegration() {
        if (!FabricLoader.getInstance().isModLoaded("multiconnect")) return;
        try {
            Class<?> mcApiClass = Class.forName("net.earthcomputer.multiconnect.api.MultiConnectAPI");
            Class<?> iProtocolClass = Class.forName("net.earthcomputer.multiconnect.api.IProtocol");
            Object mcApiInstance = mcApiClass.getMethod("instance").invoke(null);
            List<?> protocols = (List<?>) mcApiClass.getMethod("getSupportedProtocols").invoke(mcApiInstance);
            Method getValue = iProtocolClass.getMethod("getValue");
            Method isMulticonnectBeta;
            try {
                isMulticonnectBeta = iProtocolClass.getMethod("isMulticonnectBeta");
            } catch (NoSuchMethodException e) {
                isMulticonnectBeta = null;
            }
            Set<Integer> vers = new TreeSet<>();
            for (Object protocol : protocols) {
                // Do not use versions with beta multiconnect support, which may have stability issues
                if (isMulticonnectBeta == null || !(Boolean) isMulticonnectBeta.invoke(protocol)) {
                    vers.add((Integer) getValue.invoke(protocol));
                }
            }
            multiconnectSupportedVersions = vers.stream().mapToInt(Integer::intValue).toArray();
            getLogger().info("ViaFabric will integrate with multiconnect");
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | ClassCastException ignored) {
        }
    }

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide()) {
            ProtocolInfo info = Objects.requireNonNull(connection.getProtocolInfo());

            if (!getConfig().isClientSideEnabled()) return info.protocolVersion();

            int serverVer = getConfig().getClientSideVersion();
            SocketAddress addr = connection.getChannel().remoteAddress();

            if (addr instanceof InetSocketAddress) {
                AddressParser parser = new AddressParser();
                Integer addrVersion = parser.parse(((InetSocketAddress) addr).getHostName()).protocol;
                if (addrVersion != null) {
                    serverVer = addrVersion;
                }

                try {
                    if (serverVer == -2) {
                        // Hope protocol was autodetected
                        ProtocolVersion autoVer =
                                detectVersion((InetSocketAddress) addr).getNow(null);
                        if (autoVer != null) {
                            serverVer = autoVer.getVersion();
                        }
                    }
                } catch (Exception e) {
                    getLogger().warning("Couldn't auto detect: " + e);
                }
            }

            ProtocolVersion serverVersion = ProtocolVersion.getProtocol(serverVer);

            boolean blocked = checkAddressBlocked(addr);
            boolean supported = ProtocolUtils.isSupported(serverVersion, info.protocolVersion());

            handleMulticonnectPing(connection, info, blocked, serverVersion);

            if (blocked || !supported) serverVer = info.getProtocolVersion();

            return ProtocolVersion.getProtocol(serverVer);
        }
        NativeVersionProvider natProvider = Via.getManager().getProviders().get(NativeVersionProvider.class);
        if (natProvider != null) {
            return natProvider.getNativeServerProtocolVersion();
        }
        return super.getClosestServerProtocol(connection);
    }

    private boolean checkAddressBlocked(SocketAddress addr) {
        return addr instanceof InetSocketAddress && (isDisabled(((InetSocketAddress) addr).getHostString())
                || ((((InetSocketAddress) addr).getAddress() != null) &&
                (isDisabled(((InetSocketAddress) addr).getAddress().getHostAddress())
                        || isDisabled(((InetSocketAddress) addr).getAddress().getHostName()))));
    }

    private void handleMulticonnectPing(UserConnection connection, ProtocolInfo info, boolean blocked, ProtocolVersion serverVer) throws Exception {
        if (info.getState() == State.STATUS
                && info.getProtocolVersion() == -1
                && isMulticonnectHandler(connection.getChannel().pipeline())
                && (blocked || ProtocolUtils.isSupported(serverVer, getVersionForMulticonnect(serverVer)))) { // Intercept the connection
            ProtocolVersion multiconnectSuggestion = blocked ? ProtocolVersion.unknown : getVersionForMulticonnect(serverVer);
            getLogger().info("Sending " + multiconnectSuggestion + " for multiconnect version detector");
            PacketWrapper newAnswer = PacketWrapper.create(ClientboundStatusPackets.STATUS_RESPONSE, null, connection);
            newAnswer.write(Type.STRING, "{\"version\":{\"name\":\"viafabric integration\",\"protocol\":" + multiconnectSuggestion.getVersion() + "}}");
            newAnswer.send(info.getPipeline().contains(BaseProtocol1_16.class) ? BaseProtocol1_16.class : BaseProtocol1_7.class);
            throw CancelException.generate();
        }
    }

    protected boolean isMulticonnectHandler(ChannelPipeline pipe) {
        return false;
    }

    private ProtocolVersion getVersionForMulticonnect(ProtocolVersion clientSideVersion) {
        // https://github.com/ViaVersion/ViaVersion/blob/master/velocity/src/main/java/us/myles/ViaVersion/velocity/providers/VelocityVersionProvider.java
        int[] compatibleProtocols = multiconnectSupportedVersions;

        if (Arrays.binarySearch(compatibleProtocols, clientSideVersion.getVersion()) >= 0) {
            return clientSideVersion;
        }

        if (clientSideVersion.getVersion() < compatibleProtocols[0]) {
            return ProtocolVersion.getProtocol(compatibleProtocols[0]);
        }

        // TODO: This needs a better fix, i.e checking ProtocolRegistry to see if it would work.
        for (int i = compatibleProtocols.length - 1; i >= 0; i--) {
            int protocol = compatibleProtocols[i];
            if (clientSideVersion.getVersion() > protocol && ProtocolVersion.isRegistered(protocol)) {
                return ProtocolVersion.getProtocol(protocol);
            }
        }

        getLogger().severe("multiconnect integration: Panic, no protocol id found for " + clientSideVersion);
        return clientSideVersion;
    }

    private boolean isDisabled(String addr) {
        String[] parts = addr.split("\\.");
        boolean isNumericIp = parts.length == 4 && Arrays.stream(parts).map(Ints::tryParse).allMatch(Objects::nonNull);
        return IntStream.range(0, parts.length).anyMatch(i -> {
            String query;
            if (isNumericIp) {
                query = String.join(".", Arrays.stream(parts, 0, i + 1)
                        .toArray(String[]::new)) + ((i != 3) ? ".*" : "");
            } else {
                query = ((i != 0) ? "*." : "") + String.join(".", Arrays.stream(parts, i, parts.length)
                        .toArray(String[]::new));
            }
            if (getConfig().isForcedDisable(query)) {
                getLogger().info(addr + " is force-disabled. (Matches " + query + ")");
                return true;
            } else {
                return false;
            }
        });
    }

    protected abstract Logger getLogger();

    protected abstract VFConfig getConfig();

    protected abstract CompletableFuture<ProtocolVersion> detectVersion(InetSocketAddress address);
}
