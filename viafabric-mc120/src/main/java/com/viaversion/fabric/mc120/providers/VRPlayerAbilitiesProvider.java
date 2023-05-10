package com.viaversion.fabric.mc120.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import net.minecraft.client.MinecraftClient;

public class VRPlayerAbilitiesProvider extends PlayerAbilitiesProvider {

    @Override
    public float getFlyingSpeed(UserConnection connection) {
        return MinecraftClient.getInstance().player.getAbilities().getFlySpeed();
    }

    @Override
    public float getWalkingSpeed(UserConnection connection) {
        return MinecraftClient.getInstance().player.getAbilities().getWalkSpeed();
    }
}
