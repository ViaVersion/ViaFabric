package com.viaversion.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ViaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getAllMods()
                .stream()
                .noneMatch(it -> it.getMetadata().getId().startsWith("viafabric-mc"))) {
            System.out.println("ViaFabric didn't load correctly... Are dependencies installed?");
        }
    }
}
