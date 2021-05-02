package com.viaversion.fabric.mc117.platform;

import com.viaversion.fabric.mc117.ViaFabric;
import com.viaversion.fabric.mc117.commands.NMSCommandSender;
import com.viaversion.fabric.mc117.commands.UserCommandSender;
import com.viaversion.fabric.mc117.util.FutureTaskId;
import com.viaversion.fabric.mc117.util.JLoggerToLog4j;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.PlatformTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.util.GsonUtil;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VRPlatform implements ViaPlatform<UUID> {
    private final Logger logger = new JLoggerToLog4j(LogManager.getLogger("ViaVersion"));
    private final VRViaConfig config;
    private final File dataFolder;
    private final ViaAPI<UUID> api;

    public VRPlatform() {
        Path configDir = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("ViaFabric");
        config = new VRViaConfig(configDir.resolve("viaversion.yml").toFile());
        dataFolder = configDir.toFile();
        api = new VRViaAPI();
    }

    public static MinecraftServer getServer() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getIntegratedServer();
        }
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Environment(EnvType.CLIENT)
    private static MinecraftServer getIntegratedServer() {
        return MinecraftClient.getInstance().getServer();
    }

    public static String legacyToJson(String legacy) {
        return GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(legacy));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getPlatformName() {
        return "ViaFabric";
    }

    @Override
    public String getPlatformVersion() {
        return ViaFabric.getVersion();
    }

    @Override
    public String getPluginVersion() {
        return FabricLoader.getInstance().getModContainer("viaversion").map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion).map(Version::getFriendlyString).orElse("UNKNOWN");
    }

    @Override
    public FutureTaskId runAsync(Runnable runnable) {
        return new FutureTaskId(CompletableFuture
                .runAsync(runnable, ViaFabric.ASYNC_EXECUTOR)
                .exceptionally(throwable -> {
                    if (!(throwable instanceof CancellationException)) {
                        throwable.printStackTrace();
                    }
                    return null;
                })
        );
    }

    @Override
    public FutureTaskId runSync(Runnable runnable) {
        if (getServer() != null) {
            return runServerSync(runnable);
        } else {
            return runEventLoop(runnable);
        }
    }

    private FutureTaskId runServerSync(Runnable runnable) {
        // Kick task needs to be on main thread, it does already have error logger
        return new FutureTaskId(CompletableFuture.runAsync(runnable, getServer()));
    }

    private FutureTaskId runEventLoop(Runnable runnable) {
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .submit(runnable)
                        .addListener(errorLogger())
        );
    }

    @Override
    public PlatformTask runSync(Runnable runnable, long ticks) {
        // ViaVersion seems to not need to run delayed tasks on main thread
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .schedule(() -> runSync(runnable), ticks * 50, TimeUnit.MILLISECONDS)
                        .addListener(errorLogger())
        );
    }

    @Override
    public PlatformTask runRepeatingSync(Runnable runnable, long ticks) {
        // ViaVersion seems to not need to run repeating tasks on main thread
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .scheduleAtFixedRate(() -> runSync(runnable), 0, ticks * 50, TimeUnit.MILLISECONDS)
                        .addListener(errorLogger())
        );
    }

    private <T extends Future<?>> GenericFutureListener<T> errorLogger() {
        return future -> {
            if (!future.isCancelled() && future.cause() != null) {
                future.cause().printStackTrace();
            }
        };
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        MinecraftServer server = getServer();
        if (server != null && server.isOnThread()) {
            return getServerPlayers();
        }
        return Via.getManager().getConnectionManager().getConnectedClients().values().stream()
                .map(UserCommandSender::new)
                .toArray(ViaCommandSender[]::new);
    }

    private ViaCommandSender[] getServerPlayers() {
        return getServer().getPlayerManager().getPlayerList().stream()
                .map(Entity::getCommandSource)
                .map(NMSCommandSender::new)
                .toArray(ViaCommandSender[]::new);
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        sendMessageServer(uuid, s);
    }

    private void sendMessageServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return;
        runServerSync(() -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) return;
            player.sendMessage(Text.Serializer.fromJson(
                    legacyToJson(s)
            ), false);
        });
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return kickServer(uuid, s);
    }

    private boolean kickServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return false;
        Supplier<Boolean> kickTask = () -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) return false;
            player.networkHandler.disconnect(Text.Serializer.fromJson(legacyToJson(s)));
            return true;
        };
        if (server.isOnThread()) {
            return kickTask.get();
        } else {
            ViaFabric.JLOGGER.log(Level.WARNING, "Weird!? Player kicking was called off-thread", new Throwable());
            runServerSync(kickTask::get);
        }
        return false;  // Can't know if it worked
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ViaAPI<UUID> getApi() {
        return api;
    }

    @Override
    public ViaVersionConfig getConf() {
        return config;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return config;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public void onReload() {
        // Nothing to do
    }

    @Override
    public JsonObject getDump() {
        JsonObject platformSpecific = new JsonObject();
        List<PluginInfo> mods = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            mods.add(new PluginInfo(true,
                    mod.getMetadata().getId() + " (" + mod.getMetadata().getName() + ")",
                    mod.getMetadata().getVersion().getFriendlyString(),
                    null,
                    mod.getMetadata().getAuthors().stream()
                            .map(info -> info.getName()
                                    + (info.getContact().asMap().isEmpty() ? "" : " " + info.getContact().asMap()))
                            .collect(Collectors.toList())
            ));
        }

        platformSpecific.add("mods", GsonUtil.getGson().toJsonTree(mods));
        return platformSpecific;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }
}
