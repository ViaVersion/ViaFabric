package com.github.creeper123123321.viarift;

import com.github.creeper123123321.viarift.platform.VRInjector;
import com.github.creeper123123321.viarift.platform.VRLoader;
import com.github.creeper123123321.viarift.platform.VRPlatform;
import com.github.creeper123123321.viarift.util.JLoggerToLog4j;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;

public class ViaRift implements InitializationListener {
    public static int fakeServerVersion = 393;
    public static final Logger LOGGER = LogManager.getLogger();
    public static final java.util.logging.Logger JLOGGER = new JLoggerToLog4j(LOGGER);
    public static final EventLoop EVENT_LOOP = new DefaultEventLoop();
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.viarift.main.json");
        Via.init(ViaManager.builder().injector(new VRInjector()).loader(new VRLoader()).platform(new VRPlatform()).build());
        Via.getManager().init();
        //Via.getManager().setDebug(true);
    }
}
