package com.viaversion.fabric.mc115.gui;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

import java.util.Map;

public class ModMenuConfig implements ModMenuApi {
    @Override
    public String getModId() {
        return "viafabric";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ViaConfigScreen::new;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of("viafabric", getModConfigScreenFactory());
    }
}