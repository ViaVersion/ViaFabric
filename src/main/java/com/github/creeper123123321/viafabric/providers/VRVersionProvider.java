/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.providers;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.platform.VRClientSideUserConnection;
import com.google.common.primitives.Ints;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.base.BaseProtocol1_16;
import us.myles.ViaVersion.protocols.base.BaseProtocol1_7;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.base.VersionProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.stream.IntStream;

public class VRVersionProvider extends VersionProvider {
    private Set<Integer> multiconnectSupportedVersions = null;

    {
        try {
            if (FabricLoader.getInstance().isModLoaded("multiconnect")) {
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
                multiconnectSupportedVersions = new TreeSet<>();
                for (Object protocol : protocols) {
                    // Do not use versions with beta multiconnect support, which may have stability issues
                    if (isMulticonnectBeta == null || !(Boolean) isMulticonnectBeta.invoke(protocol)) {
                        multiconnectSupportedVersions.add((Integer) getValue.invoke(protocol));
                    }
                }
                ViaFabric.JLOGGER.info("ViaFabric will integrate with multiconnect");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | ClassCastException ignored) {
        }
    }

    @Override
    public int getServerProtocol(UserConnection connection) throws Exception {
        if (connection instanceof VRClientSideUserConnection) {
            int clientSideVersion = ViaFabric.config.getClientSideVersion();
            boolean blocked = false;
            if (connection.getChannel() != null) {
                ProtocolInfo info = Objects.requireNonNull(connection.getProtocolInfo());

                SocketAddress addr = connection.getChannel().remoteAddress();
                if (addr instanceof InetSocketAddress && (isDisabled(((InetSocketAddress) addr).getHostString())
                        || ((((InetSocketAddress) addr).getAddress() != null) &&
                        (isDisabled(((InetSocketAddress) addr).getAddress().getHostAddress())
                                || isDisabled(((InetSocketAddress) addr).getAddress().getHostName()))))) {
                    blocked = true;
                }

                if (info.getState() == State.STATUS && info.getProtocolVersion() == -1
                        && (clientSideVersion != -1 || blocked)
                        && connection.getChannel().pipeline().get(ClientConnection.class).getPacketListener()
                        .getClass().getName().startsWith("net.earthcomputer.multiconnect")) { // multiconnect version detector
                    int multiconnectSuggestion = getVersionForMulticonnect(clientSideVersion);
                    if (blocked) multiconnectSuggestion = -1;
                    ViaFabric.JLOGGER.info("Sending " + ProtocolVersion.getProtocol(multiconnectSuggestion) + " for multiconnect version detector");
                    PacketWrapper newAnswer = new PacketWrapper(0x00, null, connection);
                    newAnswer.write(Type.STRING, "{\"version\":{\"name\":\"viafabric integration\",\"protocol\":" + multiconnectSuggestion + "}}");
                    newAnswer.send(info.getPipeline().contains(BaseProtocol1_16.class) ? BaseProtocol1_16.class : BaseProtocol1_7.class);
                    throw CancelException.generate();
                }
                if (clientSideVersion == -1 || blocked) return info.getProtocolVersion();
            }
            return clientSideVersion;
        }
        return super.getServerProtocol(connection);
    }

    private int getVersionForMulticonnect(int clientSideVersion) {
        // https://github.com/ViaVersion/ViaVersion/blob/master/velocity/src/main/java/us/myles/ViaVersion/velocity/providers/VelocityVersionProvider.java
        // multiconnect supports it
        int[] compatibleProtocols = multiconnectSupportedVersions.stream().mapToInt(Integer::intValue).toArray();
        if (Arrays.binarySearch(compatibleProtocols, clientSideVersion) >= 0)
            return clientSideVersion;

        // Older than multiconnect supports, get the lowest version
        if (clientSideVersion < compatibleProtocols[0]) {
            return compatibleProtocols[0];
        }

        // Loop through all protocols to get the closest protocol id that multiconnect supports (and that viaversion does too)

        // TODO: This needs a better fix, i.e checking ProtocolRegistry to see if it would work.
        // This is more of a workaround for snapshot support by multiconnect.
        for (int i = compatibleProtocols.length - 1; i >= 0; i--) {
            int protocol = compatibleProtocols[i];
            if (clientSideVersion > protocol && ProtocolVersion.isRegistered(protocol))
                return protocol;
        }

        ViaFabric.JLOGGER.severe("multiconnect integration: Panic, no protocol id found for " + clientSideVersion);
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
            if (ViaFabric.config.isForcedDisable(query)) {
                ViaFabric.JLOGGER.info(addr + " is force-disabled. (Matches " + query + ")");
                return true;
            } else {
                return false;
            }
        });
    }
}
