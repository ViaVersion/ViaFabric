package com.viaversion.fabric.mc118.platform;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import net.minecraft.SharedConstants;

public class FabricNativeVersionProvider implements NativeVersionProvider {
    @Override
    public int getNativeServerVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }
}
