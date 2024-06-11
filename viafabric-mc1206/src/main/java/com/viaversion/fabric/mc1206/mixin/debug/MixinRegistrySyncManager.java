package com.viaversion.fabric.mc1206.mixin.debug;

import com.viaversion.fabric.mc1206.ViaFabric;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RegistrySyncManager.class, remap = false)
public class MixinRegistrySyncManager {

    @Inject(method = "configureClient", at = @At("HEAD"), cancellable = true)
    private static void ignoreMissingRegistries(ServerConfigurationNetworkHandler handler, MinecraftServer server, CallbackInfo ci) {
        if (ViaFabric.config.isIgnoreRegistrySyncErrors()) {
            ci.cancel();
        }
    }

}
