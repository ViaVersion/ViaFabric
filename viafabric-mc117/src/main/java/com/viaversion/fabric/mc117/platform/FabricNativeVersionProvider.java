package com.viaversion.fabric.mc117.platform;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import net.minecraft.SharedConstants;

public class FabricNativeVersionProvider implements NativeVersionProvider {
    @Override
    public int getServerProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }
}
