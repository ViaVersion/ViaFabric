/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.platform;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.commands.NMSCommandSender;
import com.github.creeper123123321.viafabric.commands.UserCommandSender;
import com.github.creeper123123321.viafabric.util.FutureTaskId;
import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaConnectionManager;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.dump.PluginInfo;
import us.myles.ViaVersion.sponge.VersionInfo;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.bungeecordchat.api.chat.TextComponent;
import us.myles.viaversion.libs.bungeecordchat.chat.ComponentSerializer;
import us.myles.viaversion.libs.gson.JsonObject;

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
    private final ViaConnectionManager connectionManager;
    private final ViaAPI<UUID> api;

    public VRPlatform() {
        Path configDir = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("ViaFabric");
        config = new VRViaConfig(configDir.resolve("viaversion.yml").toFile());
        dataFolder = configDir.toFile();
        connectionManager = new VRConnectionManager();
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
        try {
            return VersionInfo.class.getField("VERSION").get(null).toString();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "?";
    }

    @Override
    public TaskId runAsync(Runnable runnable) {
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
    public TaskId runSync(Runnable runnable) {
        if (getServer() != null) {
            return runServerSync(runnable);
        } else {
            return runEventLoop(runnable);
        }
    }

    private TaskId runServerSync(Runnable runnable) {
        // Kick task needs to be on main thread, it does already have error logger
        return new FutureTaskId(CompletableFuture.runAsync(runnable, getServer()));
    }

    private TaskId runEventLoop(Runnable runnable) {
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .submit(runnable)
                        .addListener(errorLogger())
        );
    }

    @Override
    public TaskId runSync(Runnable runnable, Long ticks) {
        // ViaVersion seems to not need to run delayed tasks on main thread
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .schedule(() -> runSync(runnable), ticks * 50, TimeUnit.MILLISECONDS)
                        .addListener(errorLogger())
        );
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
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
    public void cancelTask(TaskId taskId) {
        if (taskId instanceof FutureTaskId) {
            ((FutureTaskId) taskId).getObject().cancel(false);
        }
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        MinecraftServer server = getServer();
        if (server != null && server.isOnThread()) {
            return getServerPlayers();
        }
        return Via.getManager().getConnectedClients().values().stream()
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
            player.sendChatMessage(Text.Serializer.fromJson(legacyToJson(s)), MessageType.SYSTEM);
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

    @Override
    public ViaConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private String legacyToJson(String legacy) {
        return ComponentSerializer.toString(TextComponent.fromLegacyText(legacy));
    }
}
