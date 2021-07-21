package com.viaversion.fabric.common.util;

import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class RemappingUtil {
    public static int swordId(String id) {
        // https://github.com/ViaVersion/ViaVersion/blob/8de26a0ad33f5b739f5394ed80f69d14197fddc7/common/src/main/java/us/myles/ViaVersion/protocols/protocol1_9to1_8/Protocol1_9To1_8.java#L86
        switch (id) {
            case "minecraft:iron_sword":
                return 267;
            case "minecraft:wooden_sword":
                return 268;
            case "minecraft:golden_sword":
                return 272;
            case "minecraft:diamond_sword":
                return 276;
            case "minecraft:stone_sword":
                return 283;
        }
        return 0;
    }

    public static String legacyToJson(String legacy) {
        return GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }
}
