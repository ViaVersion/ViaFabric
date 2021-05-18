package com.viaversion.fabric.mc115.platform;

import com.viaversion.fabric.common.platform.AbstractFabricInjector;
import net.minecraft.SharedConstants;

public class FabricInjector extends AbstractFabricInjector {
    @Override
    public int getServerProtocolVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }
}
