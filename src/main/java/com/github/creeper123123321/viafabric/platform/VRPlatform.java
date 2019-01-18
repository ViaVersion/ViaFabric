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
import com.github.creeper123123321.viafabric.protocol.Interceptor;
import com.github.creeper123123321.viafabric.util.FutureTaskId;
import net.fabricmc.loader.FabricLoader;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.exception.CancelException;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;
import us.myles.ViaVersion.sponge.VersionInfo;
import us.myles.viaversion.libs.gson.JsonObject;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class VRPlatform implements ViaPlatform {
    private VRViaConfig config = new VRViaConfig(new File("config/ViaFabric"));

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
        return FabricLoader.INSTANCE.getModContainers()
                .stream()
                .filter(container -> container.getInfo().getId().equals("viafabric"))
                .findFirst()
                .get().getInfo().getVersionString(); // TODO
    }

    @Override
    public String getPluginVersion() {
        return VersionInfo.VERSION + "-ViaFabric";
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
        return new FutureTaskId(ViaFabric.EVENT_LOOP
                .submit(runnable)
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                    }
                })
        );
    }

    @Override
    public TaskId runSync(Runnable runnable, Long ticks) {
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .schedule(runnable, ticks * 50, TimeUnit.SECONDS)
                        .addListener(future -> {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                            }
                        })
        );
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
        return new FutureTaskId(
                ViaFabric.EVENT_LOOP
                        .scheduleAtFixedRate(runnable, 0, ticks * 50, TimeUnit.SECONDS)
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
        return Via.getManager().getPortedPlayers().values().stream().map(it -> {
            ProtocolInfo info = it.get(ProtocolInfo.class);
            return new VRCommandSender(info.getUuid(), info.getUsername());
        }).toArray(ViaCommandSender[]::new);
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        UserConnection user = Via.getManager().getPortedPlayers().get(uuid);
        PacketWrapper chat = new PacketWrapper(0x0E, null, user);
        chat.write(Type.STRING, ChatRewriter.legacyTextToJson(s));
        chat.write(Type.BYTE, (byte) 0); // Position chat box
        try {
            chat.send(Interceptor.class);
        } catch (CancelException e) {
            // Ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        UserConnection user = Via.getManager().getPortedPlayers().get(uuid);
        PacketWrapper chat = new PacketWrapper(0x1B, null, user);
        chat.write(Type.STRING, ChatRewriter.legacyTextToJson(s));
        try {
            chat.sendFuture(Interceptor.class).addListener(future -> user.getChannel().close());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
        return new JsonObject();
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }

}
