package com.viaversion.fabric.common.config;

public interface AbstractViaConfigScreen {
    String TITLE_TRANSLATE_ID = "gui.viafabric_config.title";
    String VERSION_TRANSLATE_ID = "gui.protocol_version_field.name";

    default int getProtocolTextColor(boolean valid, boolean supported) {
        if (!valid) {
            return 0xff0000; // Red
        } else if (!supported) {
            return 0xFFA500; // Orange
        }
        return 0xE0E0E0; // Default
    }
}
