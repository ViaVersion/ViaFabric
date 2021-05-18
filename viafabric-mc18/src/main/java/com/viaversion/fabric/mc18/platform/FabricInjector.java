package com.viaversion.fabric.mc18.platform;

import com.viaversion.fabric.common.platform.AbstractFabricInjector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.server.MinecraftServer;

public class FabricInjector extends AbstractFabricInjector {
    @Override
    public int getServerProtocolVersion() throws NoSuchFieldException, IllegalAccessException {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getClientProtocol();
        }
        return MinecraftServer.getServer().getServerMetadata().getVersion().getProtocolVersion();
    }

    @Environment(EnvType.CLIENT)
    private int getClientProtocol() throws NoSuchFieldException, IllegalAccessException {
        return RealmsSharedConstants.class.getField("NETWORK_PROTOCOL_VERSION").getInt(null);
    }
}
