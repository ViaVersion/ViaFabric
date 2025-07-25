/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.mc1218.platform;

import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.fabric.common.provider.AbstractFabricPlatform;
import com.viaversion.fabric.common.util.FutureTaskId;
import com.viaversion.fabric.mc1218.ViaFabric;
import com.viaversion.fabric.mc1218.commands.NMSCommandSender;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.EventLoop;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.logging.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FabricPlatform extends AbstractFabricPlatform {
    public static MinecraftServer getServer() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getIntegratedServer();
        }
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Environment(EnvType.CLIENT)
    private static MinecraftServer getIntegratedServer() {
        return Minecraft.getInstance().getSingleplayerServer();
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

    @Override
    public void sendMessage(UserConnection connection, String s) {
        UUID uuid = connection.getProtocolInfo().getUuid();
        sendMessageServer(uuid, s);
    }

    private void sendMessageServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return;
        runServerSync(() -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) return;
            player.displayClientMessage(NMSCommandSender.fromLegacy(s), false);
        });
    }

    @Override
    public boolean kickPlayer(UserConnection connection, String s) {
        UUID uuid = connection.getProtocolInfo().getUuid();
        return kickServer(uuid, s);
    }

    private boolean kickServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return false;
        Supplier<Boolean> kickTask = () -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) return false;
            player.connection.disconnect(NMSCommandSender.fromLegacy(s));
            return true;
        };
        if (server.isSameThread()) {
            return kickTask.get();
        } else {
            ViaFabric.JLOGGER.log(Level.WARNING, "Weird!? Player kicking was called off-thread", new Throwable());
            runServerSync(kickTask::get);
        }
        return false;  // Can't know if it worked
    }

    @Override
    protected void installNativeVersionProvider() {
        Via.getManager().getProviders().use(NativeVersionProvider.class, new FabricNativeVersionProvider());
    }

    @Override
    protected ExecutorService asyncService() {
        return ViaFabric.ASYNC_EXECUTOR;
    }

    @Override
    protected EventLoop eventLoop() {
        return ViaFabric.EVENT_LOOP;
    }
}
