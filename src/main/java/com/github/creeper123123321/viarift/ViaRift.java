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

import com.github.creeper123123321.viarift.platform.VRCommandHandler;
import com.github.creeper123123321.viarift.platform.VRInjector;
import com.github.creeper123123321.viarift.platform.VRLoader;
import com.github.creeper123123321.viarift.platform.VRPlatform;
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
import us.myles.ViaVersion.api.Via;

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
    }
}
