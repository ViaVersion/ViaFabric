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

package com.github.creeper123123321.viafabric;

import com.github.creeper123123321.viafabric.commands.VRCommandHandler;
import com.github.creeper123123321.viafabric.platform.*;
import com.github.creeper123123321.viafabric.protocol.protocol1_7_6_10to1_7_1_5.Protocol1_7_6_10To1_7_1_5;
import com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.Protocol1_8To1_7_6_10;
import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandSource;
import org.apache.logging.log4j.LogManager;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaFabric implements ModInitializer {
    public static final Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("ViaFabric-%d").build();
        ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
        EVENT_LOOP = new DefaultEventLoop(factory);
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer("viafabric")
                .get().getMetadata().getVersion().getFriendlyString();
    }

    public static <S extends CommandSource> LiteralArgumentBuilder<S> command(String commandName) {
        return LiteralArgumentBuilder.<S>literal(commandName)
                .then(
                        RequiredArgumentBuilder
                                .<S, String>argument("args", StringArgumentType.greedyString())
                                .executes(((VRCommandHandler) Via.getManager().getCommandHandler())::execute)
                                .suggests(((VRCommandHandler) Via.getManager().getCommandHandler())::suggestion)
                )
                .executes(((VRCommandHandler) Via.getManager().getCommandHandler())::execute);
    }

    @Override
    public void onInitialize() {
        Via.init(ViaManager.builder()
                .injector(new VRInjector())
                .loader(new VRLoader())
                .commandHandler(new VRCommandHandler())
                .platform(new VRPlatform()).build());
        Via.getManager().init();
        ProtocolRegistry.registerProtocol(new Protocol1_7_6_10To1_7_1_5(), Collections.singletonList(ProtocolVersion.v1_7_6.getId()), ProtocolVersion.v1_7_1.getId());
        ProtocolRegistry.registerProtocol(new Protocol1_8To1_7_6_10(), Collections.singletonList(ProtocolVersion.v1_8.getId()), ProtocolVersion.v1_7_6.getId());
        new VRRewindPlatform().init();
        new VRBackwardsPlatform().init();

        CommandRegistry.INSTANCE.register(false, c -> c.register(command("viaversion")));
        CommandRegistry.INSTANCE.register(false, c -> c.register(command("viaver")));
        CommandRegistry.INSTANCE.register(false, c -> c.register(command("vvfabric")));
    }
}
