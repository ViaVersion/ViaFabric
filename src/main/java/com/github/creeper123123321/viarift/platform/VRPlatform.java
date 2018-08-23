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

package com.github.creeper123123321.viarift.platform;

import com.github.creeper123123321.viarift.ViaRift;
import com.github.creeper123123321.viarift.util.FutureTaskId;
import com.github.creeper123123321.viarift.util.ThreadTaskId;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.ViaVersionConfig;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.configuration.ConfigurationProvider;
import us.myles.ViaVersion.api.platform.TaskId;
import us.myles.ViaVersion.api.platform.ViaPlatform;
import us.myles.ViaVersion.sponge.VersionInfo;
import us.myles.viaversion.libs.gson.JsonObject;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class VRPlatform implements ViaPlatform {
    private VRViaConfig config = new VRViaConfig(new File("config/ViaRift/config.yml"));

    @Override
    public Logger getLogger() {
        return ViaRift.JLOGGER;
    }

    @Override
    public String getPlatformName() {
        return "ViaRift";
    }

    @Override
    public String getPlatformVersion() {
        return "?"; // TODO
    }

    @Override
    public String getPluginVersion() {
        return "ViaRift-" + VersionInfo.VERSION;
    }

    @Override
    public TaskId runAsync(Runnable runnable) {
        Thread t = new Thread(runnable, "ViaRift Async Task");
        t.start();
        return new ThreadTaskId(t);
    }

    @Override
    public TaskId runSync(Runnable runnable) {
        return new FutureTaskId(ViaRift.EVENT_LOOP.submit(runnable));
    }

    @Override
    public TaskId runSync(Runnable runnable, Long ticks) {
        return new FutureTaskId(ViaRift.EVENT_LOOP.schedule(runnable, ticks * 50, TimeUnit.SECONDS));
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long ticks) {
        return new FutureTaskId(ViaRift.EVENT_LOOP.scheduleAtFixedRate(runnable, 0, ticks * 50, TimeUnit.SECONDS));
    }

    @Override
    public void cancelTask(TaskId taskId) {
        if (taskId instanceof ThreadTaskId) {
            ((ThreadTaskId) taskId).getObject().interrupt();
        } else if (taskId instanceof FutureTaskId) {
            ((FutureTaskId) taskId).getObject().cancel(false);
        }
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        if (uuid.equals(Minecraft.getMinecraft().player.getUniqueID())) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        throw new UnsupportedOperationException();
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
