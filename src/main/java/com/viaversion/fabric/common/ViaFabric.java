package com.viaversion.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ViaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getAllMods()
                .stream()
                .noneMatch(it -> it.getMetadata().getId().startsWith("viafabric-mc"))) {
            throw new IllegalStateException("ViaFabric sub-mod didn't load correctly. Check if required dependencies are installed");
        }
    }
}
