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

package com.github.creeper123123321.viarift;

import com.github.creeper123123321.viarift.platform.*;
import com.github.creeper123123321.viarift.util.JLoggerToLog4j;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.minecraft.util.NamedThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.base.ProtocolInfo;

import java.util.concurrent.ThreadFactory;

public class ViaRift implements InitializationListener {
    public static int fakeServerVersion = -1;
    public static final Logger LOGGER = LogManager.getLogger();
    public static final java.util.logging.Logger JLOGGER = new JLoggerToLog4j(LOGGER);
    public static final ThreadFactory THREAD_FACTORY = new NamedThreadFactory("ViaRift");
    public static final EventLoop EVENT_LOOP = new DefaultEventLoop(THREAD_FACTORY);
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.viarift.main.json");
        Via.init(ViaManager.builder()
                .injector(new VRInjector())
                .loader(new VRLoader())
                .commandHandler(new VRCommandHandler())
                .platform(new VRPlatform()).build());
        Via.getManager().init();
        ProtocolRegistry.getProtocolPath(ProtocolVersion.v1_13.getId(), ProtocolVersion.v1_12_2.getId()) // XGH to intercept /viarift commands
                .get(0).getValue().registerIncoming(State.PLAY, 0x02, 0x02, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String msg = packetWrapper.get(Type.STRING, 0);
                        ProtocolInfo info = packetWrapper.user().get(ProtocolInfo.class);
                        if (msg.startsWith("/viarift")) {
                            Via.getManager().getCommandHandler().onCommand(
                                    new VRCommandSender(info.getUuid(), info.getUsername()),
                                    (msg.length() == 8 ? "" : msg.substring(9)).split(" ", -1)
                            );
                            packetWrapper.cancel();
                        }
                    }
                });
            }
        });
        //Via.getManager().setDebug(true);
    }
}
