package com.viaversion.fabric.mc18.platform;

import com.viaversion.fabric.mc18.handler.CommonTransformer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.server.MinecraftServer;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.util.GsonUtil;
import com.viaversion.viaversion.libs.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.Arrays;

public class VRInjector implements ViaInjector {
    @Override
    public void inject() {
        // *looks at Mixins*
    }

    @Override
    public void uninject() {
        // not possible *plays sad violin*
    }

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

    @Override
    public String getEncoderName() {
        return CommonTransformer.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return CommonTransformer.HANDLER_DECODER_NAME;
    }

    @Override
    public JsonObject getDump() {
        JsonObject obj = new JsonObject();
        try {
            obj.add("serverNetworkIOChInit", GsonUtil.getGson().toJsonTree(
                    Arrays.stream(Class.forName("net.minecraft.class_3242$1").getDeclaredMethods())
                            .map(Method::toString)
                            .toArray(String[]::new)));
        } catch (ClassNotFoundException ignored) {
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                obj.add("clientConnectionChInit", GsonUtil.getGson().toJsonTree(
                        Arrays.stream(Class.forName("net.minecraft.class_2535$1").getDeclaredMethods())
                                .map(Method::toString)
                                .toArray(String[]::new)));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return obj;
    }
}
