package com.viaversion.fabric.mc120.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PlayerLookTargetProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class VFPlayerLookTargetProvider extends PlayerLookTargetProvider {

    @Override
    public Position getPlayerLookTarget(UserConnection info) {
        if (!info.isClientSide()) return null;

        if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHitResult) {
            final BlockPos pos = blockHitResult.getBlockPos();
            return new Position(pos.getX(), pos.getY(), pos.getZ());
        }
        return null;
    }
}
