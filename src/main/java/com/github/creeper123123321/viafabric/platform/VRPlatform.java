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
import com.github.creeper123123321.viafabric.providers.VRVersionProvider;
import com.github.creeper123123321.viafabric.util.FutureTaskId;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.network.OffThreadException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.dump.PluginInfo;
import us.myles.ViaVersion.protocols.base.VersionProvider;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.sponge.VersionInfo;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VRPlatform implements ViaPlatform {
    private VRViaConfig config = new VRViaConfig(FabricLoader.getInstance().getConfigDirectory().toPath().resolve("ViaFabric").resolve("viaversion.yml").toFile());

    @Nullable
    public static MinecraftServer getServer() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getIntegratedServer();
        }
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    private static MinecraftServer getIntegratedServer() {
        return MinecraftClient.getInstance().getServer();
    }

    @Override
    public Logger getLogger() {
        return ViaFabric.JLOGGER;
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
                    throwable.printStackTrace();
                    return null;
                })
        );
    }

    @Override
    public TaskId runSync(Runnable runnable) {
        // Kick task needs to be on main thread
        Executor executor = ViaFabric.EVENT_LOOP;
        boolean alreadyLogged;
        MinecraftServer server = getServer();
        if (server != null) {
            alreadyLogged = true;
            executor = server;
        } else {
            alreadyLogged = false;
        }
        return new FutureTaskId(
                CompletableFuture.runAsync(runnable, executor)
                        .exceptionally(throwable -> {
                            if (!alreadyLogged) {
                                throwable.printStackTrace();
                            }
                            return null;
                        })
        );
    }

    @Override
    public TaskId runSync(Runnable runnable, Long ticks) {
        // ViaVersion seems to not need to run delayed tasks on main thread
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .schedule(runnable, ticks * 50, TimeUnit.MILLISECONDS)
                        .addListener(future -> {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                            }
                        })
        );
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
        // ViaVersion seems to not need to run repeating tasks on main thread
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .scheduleAtFixedRate(runnable, 0, ticks * 50, TimeUnit.MILLISECONDS)
                        .addListener(future -> {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                            }
                        })
        );
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
            // Not thread safe
            return server.getPlayerManager().getPlayerList().stream()
                    .map(Entity::getCommandSource)
                    .map(NMSCommandSender::new)
                    .toArray(ViaCommandSender[]::new);
        }
        return Via.getManager().getPortedPlayers().values().stream()
                .map(UserCommandSender::new)
                .toArray(ViaCommandSender[]::new);
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        UserConnection user = Via.getManager().getPortedPlayers().get(uuid);
        if (user instanceof VRClientSideUserConnection) {
            sendMessageClient(s);
        } else {
            runSync(() -> {
                MinecraftServer server = getServer();
                if (server == null) return;
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player == null) return;
                player.sendChatMessage(Text.Serializer.fromJson(ChatRewriter.legacyTextToJson(s)), MessageType.SYSTEM);
            });
        }
    }

    @Environment(EnvType.CLIENT)
    private void sendMessageClient(String s) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler != null) {
            try {
                handler.onChatMessage(new ChatMessageS2CPacket(
                        Text.Serializer.fromJson(ChatRewriter.legacyTextToJson(s))
                ));
            } catch (OffThreadException ignored) {
            }
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        UserConnection user = Via.getManager().getPortedPlayers().get(uuid);
        if (user instanceof VRClientSideUserConnection) {
            return kickClient(s);
        } else {
            MinecraftServer server = getServer();
            if (server != null && server.isOnThread()) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player == null) return false;
                player.networkHandler.disconnect(Text.Serializer.fromJson(ChatRewriter.legacyTextToJson(s)));
            }
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    private boolean kickClient(String msg) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler != null) {
            try {
                handler.onDisconnect(new DisconnectS2CPacket(
                        Text.Serializer.fromJson(ChatRewriter.legacyTextToJson(msg))
                ));
            } catch (OffThreadException ignored) {
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ViaAPI getApi() {
        return new VRViaAPI();
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
    public void onReload() {
        // Nothing to do
    }

    @Override
    public JsonObject getDump() {
        JsonObject platformSpecific = new JsonObject();
        List<PluginInfo> mods = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            mods.add(new PluginInfo(true,
                    mod.getMetadata().getName(),
                    mod.getMetadata().getVersion().getFriendlyString(),
                    null,
                    mod.getMetadata().getAuthors().stream()
                            .map(info -> info.getName() + "(" + info.getContact().asMap() + ")")
                            .collect(Collectors.toList())
            ));
        }

        platformSpecific.add("mods", GsonUtil.getGson().toJsonTree(mods));
        platformSpecific.addProperty("client-sided-version", ((VRVersionProvider) Via.getManager().getProviders().get(VersionProvider.class)).clientSideModeVersion);
        return platformSpecific;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }
}
