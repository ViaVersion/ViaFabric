package com.viaversion.fabric.mc18;

import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.platform.FabricInjector;
import com.viaversion.fabric.common.protocol.HostnameParserProtocol;
import com.viaversion.fabric.common.util.JLoggerToLog4j;
import com.viaversion.fabric.mc18.commands.NMSCommandImpl;
import com.viaversion.fabric.mc18.commands.VRCommandHandler;
import com.viaversion.fabric.mc18.platform.FabricPlatform;
import com.viaversion.fabric.mc18.platform.VFLoader;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.legacyfabric.fabric.api.registry.CommandRegistry;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaFabric implements ModInitializer {
    public static final Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;
    public static final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
    public static VFConfig config;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaFabric-%d").build();
        ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next(); // ugly code
        EVENT_LOOP.submit(INIT_FUTURE::join); // https://github.com/ViaVersion/ViaFabric/issues/53 ugly workaround code but works tm
    }

    @Override
    public void onInitialize() {
        FabricPlatform platform = new FabricPlatform();

        Via.init(ViaManagerImpl.builder()
                .injector(new FabricInjector())
                .loader(new VFLoader())
                .commandHandler(new VRCommandHandler())
                .platform(platform).build());

        platform.init();

        ViaManagerImpl manager = (ViaManagerImpl) Via.getManager();
        manager.init();

        Via.getManager().getProtocolManager().registerBaseProtocol(HostnameParserProtocol.INSTANCE, Range.lessThan(Integer.MIN_VALUE));
        ProtocolVersion.register(-2, "AUTO");

        FabricLoader.getInstance().getEntrypoints("viafabric:via_api_initialized", Runnable.class).forEach(Runnable::run);

        registerCommandsV1();

        config = new VFConfig(FabricLoader.getInstance().getConfigDir().resolve("ViaFabric")
                .resolve("viafabric.yml").toFile());

        manager.onServerLoaded();

        INIT_FUTURE.complete(null);
    }

    private void registerCommandsV1() {
        try {
            CommandRegistry.INSTANCE.register(new NMSCommandImpl(Via.getManager().getCommandHandler()));
        } catch (NoClassDefFoundError ignored2) {
            JLOGGER.info("Couldn't register command as Fabric Commands isn't installed");
        }
    }
}
