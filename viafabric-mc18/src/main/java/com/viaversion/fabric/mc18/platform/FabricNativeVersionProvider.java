package com.viaversion.fabric.mc18.platform;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;

public class FabricNativeVersionProvider implements NativeVersionProvider {
    @Override
    public int getNativeServerVersion() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getClientProtocol();
        }

        ServerMetadata.Version version = MinecraftServer.getServer().getServerMetadata().getVersion();
        if (version == null) return ProtocolVersion.v1_8.getVersion();
        return version.getProtocolVersion();
    }

    @Environment(EnvType.CLIENT)
    private int getClientProtocol() {
        try {
            return RealmsSharedConstants.class.getField("NETWORK_PROTOCOL_VERSION").getInt(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ProtocolVersion.v1_8.getVersion(); // fallback
    }
}
