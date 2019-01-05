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
import com.github.creeper123123321.viafabric.platform.VRInjector;
import com.github.creeper123123321.viafabric.platform.VRLoader;
import com.github.creeper123123321.viafabric.platform.VRPlatform;
import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import us.myles.ViaVersion.ViaManager;
import us.myles.ViaVersion.api.Via;

public class ViaFabric implements ModInitializer {
    public static final java.util.logging.Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final EventLoop EVENT_LOOP = new DefaultEventLoop(new ThreadFactoryBuilder()
            .setNameFormat("ViaFabric").build());

    @Override
    public void onInitialize() {
        Via.init(ViaManager.builder()
                .injector(new VRInjector())
                .loader(new VRLoader())
                .commandHandler(new VRCommandHandler())
                .platform(new VRPlatform()).build());
        Via.getManager().init();
    }
}
