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

package com.github.creeper123123321.viafabric.listeners;

import com.github.creeper123123321.viafabric.commands.NMSCommandSender;
import com.github.creeper123123321.viafabric.platform.VRClientSideUserConnection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.update.UpdateUtil;

import java.util.Arrays;
import java.util.UUID;

public class UpdateListener {
    public static void onJoin(UserConnection conn) {
        UUID id = conn.getProtocolInfo().getUuid();
        if (conn instanceof VRClientSideUserConnection) {
            onClientJoin(id);
        } else {
            onServerJoin(id);
        }
    }

    private static void onClientJoin(UUID id) {
        MinecraftClient.getInstance().execute(() -> {
            Entity entity = MinecraftClient.getInstance().targetedEntity;
            if (entity != null) {
                onSenderJoin(new NMSCommandSender(entity.getCommandSource()), id);
            }
        });
    }

    private static void onServerJoin(UUID id) {
        Via.getPlatform().runSync(() -> {
            Arrays.stream(Via.getPlatform().getOnlinePlayers())
                    .filter(it -> it.getUUID().equals(id)).findAny().ifPresent(sender -> {
                onSenderJoin(sender, id);
            });
        });
    }

    private static void onSenderJoin(ViaCommandSender sender, UUID connId) {
        if (sender.hasPermission("viaversion.admin")
                && Via.getConfig().isCheckForUpdates()) {
            UpdateUtil.sendUpdateMessage(connId);
        }
    }
}
