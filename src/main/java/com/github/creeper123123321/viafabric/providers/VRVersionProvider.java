package com.github.creeper123123321.viafabric.providers;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.ViaFabricAddress;
import com.github.creeper123123321.viafabric.service.ProtocolAutoDetector;
import com.github.creeper123123321.viafabric.util.ProtocolUtils;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.protocols.base.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.stream.IntStream;

public class VRVersionProvider extends BaseVersionProvider {
    private int[] multiconnectSupportedVersions = null;

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
                Set<Integer> vers = new TreeSet<>();
                for (Object protocol : protocols) {
                    // Do not use versions with beta multiconnect support, which may have stability issues
                    if (isMulticonnectBeta == null || !(Boolean) isMulticonnectBeta.invoke(protocol)) {
                        vers.add((Integer) getValue.invoke(protocol));
                    }
                }
                multiconnectSupportedVersions = vers.stream().mapToInt(Integer::intValue).toArray();
                ViaFabric.JLOGGER.info("ViaFabric will integrate with multiconnect");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | ClassCastException ignored) {
        }
    }

    @Override
    public int getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide()) {
            ProtocolInfo info = Objects.requireNonNull(connection.getProtocolInfo());

            if (!ViaFabric.config.isClientSideEnabled()) {
                return info.getProtocolVersion();
            }

            int serverVer = ViaFabric.config.getClientSideVersion();
            SocketAddress addr = connection.getChannel().remoteAddress();

            if (addr instanceof InetSocketAddress) {
                int addrVersion = new ViaFabricAddress().parse(((InetSocketAddress) addr).getHostName()).protocol;
                if (addrVersion != 0) serverVer = addrVersion;

                try {
                    if (serverVer == -2) {
                        // Hope protocol was autodetected
                        ProtocolVersion autoVer =
                                ProtocolAutoDetector.detectVersion((InetSocketAddress) addr).getNow(null);
                        if (autoVer != null) {
                            serverVer = autoVer.getVersion();
                        }
                    }
                } catch (Exception e) {
                    ViaFabric.JLOGGER.warning("Couldn't auto detect: " + e);
                }
            }

            boolean blocked = checkAddressBlocked(addr);
            boolean supported = ProtocolUtils.isSupported(serverVer, info.getProtocolVersion());

            handleMulticonnectPing(connection, info, blocked, serverVer);

            if (blocked || !supported) serverVer = info.getProtocolVersion();

            return serverVer;
        }
        return super.getClosestServerProtocol(connection);
    }

    private boolean checkAddressBlocked(SocketAddress addr) {
        return addr instanceof InetSocketAddress && (isDisabled(((InetSocketAddress) addr).getHostString())
                || ((((InetSocketAddress) addr).getAddress() != null) &&
                (isDisabled(((InetSocketAddress) addr).getAddress().getHostAddress())
                        || isDisabled(((InetSocketAddress) addr).getAddress().getHostName()))));
    }

    private void handleMulticonnectPing(UserConnection connection, ProtocolInfo info, boolean blocked, int serverVer) throws Exception {
        if (info.getState() == State.STATUS
                && info.getProtocolVersion() == -1
                && connection.getChannel().pipeline().get(ClientConnection.class).getPacketListener()
                .getClass().getName().startsWith("net.earthcomputer.multiconnect")
                && (blocked || ProtocolUtils.isSupported(serverVer, getVersionForMulticonnect(serverVer)))) { // Intercept the connection
            int multiconnectSuggestion = blocked ? -1 : getVersionForMulticonnect(serverVer);
            ViaFabric.JLOGGER.info("Sending " + ProtocolVersion.getProtocol(multiconnectSuggestion) + " for multiconnect version detector");
            PacketWrapper newAnswer = PacketWrapper.create(0x00, null, connection);
            newAnswer.write(Type.STRING, "{\"version\":{\"name\":\"viafabric integration\",\"protocol\":" + multiconnectSuggestion + "}}");
            newAnswer.send(info.getPipeline().contains(BaseProtocol1_16.class) ? BaseProtocol1_16.class : BaseProtocol1_7.class, true, true);
            throw CancelException.generate();
        }
    }

    private int getVersionForMulticonnect(int clientSideVersion) {
        // https://github.com/ViaVersion/ViaVersion/blob/master/velocity/src/main/java/us/myles/ViaVersion/velocity/providers/VelocityVersionProvider.java
        int[] compatibleProtocols = multiconnectSupportedVersions;

        if (Arrays.binarySearch(compatibleProtocols, clientSideVersion) >= 0) {
            return clientSideVersion;
        }

        if (clientSideVersion < compatibleProtocols[0]) {
            return compatibleProtocols[0];
        }

        // TODO: This needs a better fix, i.e checking ProtocolRegistry to see if it would work.
        for (int i = compatibleProtocols.length - 1; i >= 0; i--) {
            int protocol = compatibleProtocols[i];
            if (clientSideVersion > protocol && ProtocolVersion.isRegistered(protocol)) {
                return protocol;
            }
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
