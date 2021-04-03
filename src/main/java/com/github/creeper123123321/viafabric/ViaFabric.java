package com.github.creeper123123321.viafabric;

import com.github.creeper123123321.viafabric.commands.VRCommandHandler;
import com.github.creeper123123321.viafabric.config.VRConfig;
import com.github.creeper123123321.viafabric.platform.VRInjector;
import com.github.creeper123123321.viafabric.platform.VRLoader;
import com.github.creeper123123321.viafabric.platform.VRPlatform;
import com.github.creeper123123321.viafabric.protocol.ViaFabricHostnameProtocol;
import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandSource;
import org.apache.logging.log4j.LogManager;
import us.myles.ViaVersion.ViaManagerImpl;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaFabric implements ModInitializer {
    public static final Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;
    public static CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
    public static VRConfig config;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaFabric-%d").build();
        ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
        EVENT_LOOP = new DefaultEventLoop(factory);
        EVENT_LOOP.submit(INIT_FUTURE::join); // https://github.com/ViaVersion/ViaFabric/issues/53 ugly workaround code but works tm
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer("viafabric")
                .get().getMetadata().getVersion().getFriendlyString();
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
        Via.init(ViaManagerImpl.builder()
                .injector(new VRInjector())
                .loader(new VRLoader())
                .commandHandler(new VRCommandHandler())
                .platform(new VRPlatform()).build());

        FabricLoader.getInstance().getModContainer("viabackwards").ifPresent(mod -> MappingDataLoader.enableMappingsCache());

        ((ViaManagerImpl) Via.getManager()).init();

        Via.getManager().getProtocolManager().registerBaseProtocol(ViaFabricHostnameProtocol.INSTANCE, Range.lessThan(Integer.MIN_VALUE));
        ProtocolVersion.register(-2, "AUTO");

        FabricLoader.getInstance().getEntrypoints("viafabric:via_api_initialized", Runnable.class).forEach(Runnable::run);

        try {
            registerCommandsV1();
        } catch (NoClassDefFoundError ignored) {
            try {
                registerCommandsV0();
                JLOGGER.info("Using Fabric Commands V0");
            } catch (NoClassDefFoundError ignored2) {
                JLOGGER.info("Couldn't register command as Fabric Commands isn't installed");
            }
        }

        config = new VRConfig(FabricLoader.getInstance().getConfigDir().resolve("ViaFabric")
                .resolve("viafabric.yml").toFile());

        INIT_FUTURE.complete(null);
    }

    private void registerCommandsV1() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("viaversion")));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("viaver")));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(command("vvfabric")));
    }

    @SuppressWarnings("deprecation")
    private void registerCommandsV0() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(command("viaversion")));
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(command("viaver")));
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register(command("vvfabric")));
    }
}
