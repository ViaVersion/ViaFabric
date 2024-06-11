package com.viaversion.fabric.mc1206.mixin.debug.client;

import com.viaversion.fabric.mc1206.ViaFabric;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistrySyncManager.class)
public class MixinRegistrySyncManager {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "checkRemoteRemap", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    private static void ignoreMissingRegistries(Map<Identifier, Object2IntMap<Identifier>> map, CallbackInfo ci) {
        if (ViaFabric.config.isIgnoreRegistrySyncErrors()) {
            LOGGER.warn("Ignoring missing registries");
            ci.cancel();
        }
    }

}
