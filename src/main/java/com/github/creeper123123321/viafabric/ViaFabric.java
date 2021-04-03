/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
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

import com.github.creeper123123321.viafabric.commands.NMSCommandImpl;
import com.github.creeper123123321.viafabric.commands.VRCommandHandler;
import com.github.creeper123123321.viafabric.config.VRConfig;
import com.github.creeper123123321.viafabric.platform.VRInjector;
import com.github.creeper123123321.viafabric.platform.VRLoader;
import com.github.creeper123123321.viafabric.platform.VRPlatform;
import com.github.creeper123123321.viafabric.protocol.ViaFabricHostnameProtocol;
import com.github.creeper123123321.viafabric.util.JLoggerToLog4j;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import us.myles.ViaVersion.ViaManagerImpl;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.MappingDataLoader;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import net.legacyfabric.fabric.api.registry.CommandRegistry;

public class ViaFabric implements ModInitializer {
    public static final Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaFabric"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;
    public static CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
    public static VRConfig config;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaFabric-%d").build();
        ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next(); // ugly code
        EVENT_LOOP.submit(INIT_FUTURE::join); // https://github.com/ViaVersion/ViaFabric/issues/53 ugly workaround code but works tm
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer("viafabric")
                .get().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public void onInitialize() {
        Via.init(ViaManagerImpl.builder()
                .injector(new VRInjector())
                .loader(new VRLoader())
                .commandHandler(new VRCommandHandler())
                .platform(new VRPlatform()).build());

        FabricLoader.getInstance().getModContainer("viabackwards").ifPresent(mod -> MappingDataLoader.enableMappingsCache());

        ((ViaManagerImpl) Via.getManager()).init();

        Via.getManager().getProtocolManager().registerBaseProtocol(ViaFabricHostnameProtocol.INSTANCE, Range.lessThan(Integer.MIN_VALUE));
        ProtocolVersion.register(-2, "AUTO");

        FabricLoader.getInstance().getEntrypoints("viafabric:via_api_initialized", Runnable.class).forEach(Runnable::run);

        try {
            registerCommandsV0();
        } catch (NoClassDefFoundError ignored2) {
            JLOGGER.info("Couldn't register command as Fabric Commands isn't installed");
        }

        config = new VRConfig(FabricLoader.getInstance().getConfigDir().resolve("ViaFabric")
                .resolve("viafabric.yml").toFile());

        INIT_FUTURE.complete(null);
    }

    @SuppressWarnings("deprecation")
    private void registerCommandsV0() {
        CommandRegistry.INSTANCE.register(new NMSCommandImpl(Via.getManager().getCommandHandler()));
    }
}
