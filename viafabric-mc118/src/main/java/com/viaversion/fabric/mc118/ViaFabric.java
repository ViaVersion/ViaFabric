package com.viaversion.fabric.mc118;

import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.viaversion.fabric.common.config.VFConfig;
import com.viaversion.fabric.common.platform.FabricInjector;
import com.viaversion.fabric.common.protocol.HostnameParserProtocol;
import com.viaversion.fabric.common.util.JLoggerToLog4j;
import com.viaversion.fabric.mc118.commands.VRCommandHandler;
import com.viaversion.fabric.mc118.platform.FabricPlatform;
import com.viaversion.fabric.mc118.platform.VFLoader;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
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
        EVENT_LOOP = new DefaultEventLoop(factory);
        EVENT_LOOP.submit(INIT_FUTURE::join); // https://github.com/ViaVersion/ViaFabric/issues/53 ugly workaround code but works tm
    }

    public static <S extends CommandSource> LiteralArgumentBuilder<S> command(String commandName) {
        return LiteralArgumentBuilder.<S>literal(commandName)
                .then(
                        RequiredArgumentBuilder
                                .<S, String>argument("args", StringArgumentType.greedyString())
                                .executes(((VRCommandHandler) Via.getManager().getCommandHandler())::execute)
                                .suggests(((VRCommandHandler) Via.getManager().getCommandHandler())::suggestion)
                )
                .executes(((VRCommandHandler) Via.getManager().getCommandHandler())::execute);
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
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("viaversion")));
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("viaver")));
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("vvfabric")));
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientCommandManager.DISPATCHER.register(command("viafabricclient"));
            }
        } catch (NoClassDefFoundError ignored) {
            JLOGGER.info("Couldn't register command as Fabric Commands V1 isn't installed");
        }
    }
}
