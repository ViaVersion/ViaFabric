package com.github.creeper123123321.viafabric.platform;

import com.github.creeper123123321.viafabric.handler.CommonTransformer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import us.myles.ViaVersion.api.platform.ViaInjector;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonObject;

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
    public int getServerProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
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
