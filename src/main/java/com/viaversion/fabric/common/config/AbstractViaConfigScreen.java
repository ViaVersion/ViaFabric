package com.viaversion.fabric.common.config;

import com.viaversion.fabric.common.util.ProtocolUtils;

public interface AbstractViaConfigScreen {
    String TITLE_TRANSLATE_ID = "gui.viafabric_config.title";
    String VERSION_TRANSLATE_ID = "gui.protocol_version_field.name";

    default int getProtocolTextColor(int version, boolean parsedValid) {
        if (!parsedValid) return 0xff0000; // Red
        if (version == -1 || version == -2) return 0x5555FF; // Blue
        if (!ProtocolUtils.isSupportedClientSide(version)) return 0xFFA500; // Orange
        return 0xE0E0E0; // Default
    }

    default int calculatePosX(int width, int entryNumber) {
        return width / 2 - 155 + entryNumber % 2 * 160;
    }

    default int calculatePosY(int height, int entryNumber) {
        return height / 6 + 24 * (entryNumber / 2);
    }
}
