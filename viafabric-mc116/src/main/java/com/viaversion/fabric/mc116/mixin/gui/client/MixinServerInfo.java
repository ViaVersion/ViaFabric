package com.viaversion.fabric.mc116.mixin.gui.client;


import com.viaversion.fabric.common.gui.ViaServerInfo;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements ViaServerInfo {
    private boolean viaTranslating;
    private int viaServerVer;

    public int getViaServerVer() {
        return viaServerVer;
    }

    public void setViaServerVer(int viaServerVer) {
        this.viaServerVer = viaServerVer;
    }

    @Override
    public boolean isViaTranslating() {
        return viaTranslating;
    }

    @Override
    public void setViaTranslating(boolean via) {
        this.viaTranslating = via;
    }
}
