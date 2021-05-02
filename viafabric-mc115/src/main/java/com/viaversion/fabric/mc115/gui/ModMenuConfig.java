package com.viaversion.fabric.mc115.gui;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuConfig implements ModMenuApi {
    @Override
    public String getModId() {
        return "viafabric";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ViaConfigScreen::new;
    }
}