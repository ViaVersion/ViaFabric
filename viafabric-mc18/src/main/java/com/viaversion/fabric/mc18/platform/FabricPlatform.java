package com.viaversion.fabric.mc18.platform;

import com.viaversion.fabric.common.commands.UserCommandSender;
import com.viaversion.fabric.common.platform.NativeVersionProvider;
import com.viaversion.fabric.common.provider.AbstractFabricPlatform;
import com.viaversion.fabric.common.util.FutureTaskId;
import com.viaversion.fabric.common.util.RemappingUtil;
import com.viaversion.fabric.mc18.ViaFabric;
import com.viaversion.fabric.mc18.commands.NMSCommandSender;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import io.netty.channel.EventLoop;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.logging.Level;

public class FabricPlatform extends AbstractFabricPlatform {
    public static MinecraftServer getServer() {
        // In 1.8.9 integrated server instance exists even if it's not running
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                && !MinecraftClient.getInstance().isIntegratedServerRunning()) return null;
        return MinecraftServer.getServer();
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
        return new FutureTaskId(CompletableFuture.runAsync(runnable, it -> getServer().method_10815((Callable<Void>) () -> {
            it.run();
            return null;
        })));
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
        return getServer().getPlayerManager().getPlayers().stream()
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
            player.sendMessage(Text.Serializer.deserialize(RemappingUtil.legacyToJson(s)));
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
            player.networkHandler.disconnect(s);
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
}
