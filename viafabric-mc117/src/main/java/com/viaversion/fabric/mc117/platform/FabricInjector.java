package com.viaversion.fabric.mc117.platform;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.platform.AbstractFabricInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.GsonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

import java.lang.reflect.Method;
import java.util.Arrays;

public class FabricInjector extends AbstractFabricInjector {
    @Override
    public int getServerProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }

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
