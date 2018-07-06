package com.github.creeper123123321.viarift.platform;

import com.github.creeper123123321.viarift.util.DelayedRunnable;
import com.github.creeper123123321.viarift.util.LoopRunnable;
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

import java.util.UUID;
import java.util.logging.Logger;

public class VRPlatform implements ViaPlatform {
    @Override
    public Logger getLogger() {
        return Logger.getLogger("ViaRift");
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
        return VersionInfo.VERSION;
    }

    @Override
    public TaskId runAsync(Runnable runnable) {
        Thread t = new Thread(runnable, "ViaRift Async Task");
        t.start();
        return new VRTaskId(t);
    }

    @Override
    public TaskId runSync(Runnable runnable) {
        Thread t = new Thread(runnable, "ViaRift Sync Task");
        t.start();
        return new VRTaskId(t);
    }

    @Override
    public TaskId runSync(Runnable runnable, Long aLong) {
        Thread t = new Thread(new DelayedRunnable(runnable, aLong * 50), "ViaRift Sync Delayed Task");
        t.start();
        return new VRTaskId(t);
    }

    @Override
    public TaskId runRepeatingSync(Runnable runnable, Long aLong) {
        Thread t = new Thread(new LoopRunnable(runnable, aLong * 50), "ViaRift Sync Repeating Task");
        t.start();
        return new VRTaskId(t);
    }

    @Override
    public void cancelTask(TaskId taskId) {
        if (taskId instanceof VRTaskId) {
            ((VRTaskId) taskId).getObject().interrupt();
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
        return new VRViaConfig();
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onReload() {

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
