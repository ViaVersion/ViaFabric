package com.viaversion.fabric.mc117.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerInfo;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements ViaServerInfo {
    @Unique
    private boolean viaFabric$translating;

    @Unique
    private int viaFabric$serverVer;

    @Override
    public int viaFabric$getServerVer() {
        return viaFabric$serverVer;
    }

    @Override
    public void viaFabric$setServerVer(int ver) {
        this.viaFabric$serverVer = ver;
    }

    @Override
    public boolean viaFabric$translating() {
        return viaFabric$translating;
    }

    @Override
    public void viaFabric$setTranslating(boolean via) {
        this.viaFabric$translating = via;
    }
}
