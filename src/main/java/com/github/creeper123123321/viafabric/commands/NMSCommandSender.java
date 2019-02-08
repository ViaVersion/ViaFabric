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

package com.github.creeper123123321.viafabric.commands;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ChatRewriter;

import java.util.UUID;

public class NMSCommandSender implements ViaCommandSender {
    private CommandSource source;

    public NMSCommandSender(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String s) {
        // https://gaming.stackexchange.com/questions/138602/what-does-op-permission-level-do
        return source.hasPermissionLevel(3);
    }

    @Override
    public void sendMessage(String s) {
        if (source instanceof ServerCommandSource) {
            ((ServerCommandSource) source).sendFeedback(TextComponent.Serializer.fromJsonString(ChatRewriter.legacyTextToJson(s)), false);
        } else if (FabricLoader.INSTANCE.getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            FabricLoader.INSTANCE.getEnvironmentHandler().getClientPlayer()
                    .appendCommandFeedback(TextComponent.Serializer.fromJsonString(ChatRewriter.legacyTextToJson(s)));
        }
    }

    @Override
    public UUID getUUID() {
        if (source instanceof ServerCommandSource) {
            Entity entity = ((ServerCommandSource) source).getEntity();
            if (entity != null) return entity.getUuid();
        } else if (FabricLoader.INSTANCE.getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            return FabricLoader.INSTANCE.getEnvironmentHandler().getClientPlayer().getUuid();
        }
        return UUID.fromString(getName());
    }

    @Override
    public String getName() {
        if (source instanceof ServerCommandSource) {
            return ((ServerCommandSource) source).getName();
        } else if (FabricLoader.INSTANCE.getEnvironmentType() == EnvType.CLIENT && source instanceof ClientCommandSource) {
            return FabricLoader.INSTANCE.getEnvironmentHandler().getClientPlayer().getEntityName();
        }
        return "?";
    }
}
