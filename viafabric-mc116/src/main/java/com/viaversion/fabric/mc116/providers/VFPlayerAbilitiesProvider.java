package com.viaversion.fabric.mc116.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import net.minecraft.client.MinecraftClient;

public class VFPlayerAbilitiesProvider extends PlayerAbilitiesProvider {

    @Override
    public float getFlyingSpeed(UserConnection connection) {
        if (!connection.isClientSide()) return super.getFlyingSpeed(connection);

        return MinecraftClient.getInstance().player.abilities.getFlySpeed();
    }

    @Override
    public float getWalkingSpeed(UserConnection connection) {
        if (!connection.isClientSide()) return super.getWalkingSpeed(connection);

        return MinecraftClient.getInstance().player.abilities.getWalkSpeed();
    }
}
