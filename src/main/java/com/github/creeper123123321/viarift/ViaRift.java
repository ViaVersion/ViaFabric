package com.github.creeper123123321.viarift;

import com.github.creeper123123321.viarift.platform.VRInjector;
import com.github.creeper123123321.viarift.platform.VRLoader;
import com.github.creeper123123321.viarift.platform.VRPlatform;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;

public class ViaRift implements InitializationListener {
    public static int fakeServerVersion = 340; // TODO
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.viarift.main.json");
        Via.init(ViaManager.builder().injector(new VRInjector()).loader(new VRLoader()).platform(new VRPlatform()).build());
        Via.getManager().init();
        //Via.getManager().setDebug(true);
    }
}
