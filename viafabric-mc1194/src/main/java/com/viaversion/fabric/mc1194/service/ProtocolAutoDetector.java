/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.mc1194.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.mc1194.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Environment(EnvType.CLIENT)
public class ProtocolAutoDetector {
    private static final LoadingCache<InetSocketAddress, CompletableFuture<ProtocolVersion>> SERVER_VER = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(CacheLoader.from((address) -> {
                CompletableFuture<ProtocolVersion> future = new CompletableFuture<>();

                try {
                    final Connection clientConnection = new Connection(PacketFlow.CLIENTBOUND);

                    ChannelFuture ch = new Bootstrap()
                            .group(Connection.NETWORK_WORKER_GROUP.get())
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<Channel>() {
                                protected void initChannel(Channel channel) {
                                    try {
                                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                                        channel.config().setOption(ChannelOption.IP_TOS, 0x18); // Stolen from Velocity, low delay, high reliability
                                    } catch (ChannelException ignored) {
                                    }

                                    channel.pipeline()
                                            .addLast("timeout", new ReadTimeoutHandler(30))
                                            .addLast("splitter", new Varint21FrameDecoder())
                                            .addLast("decoder", new PacketDecoder(PacketFlow.CLIENTBOUND))
                                            .addLast("prepender", new Varint21LengthFieldPrepender())
                                            .addLast("encoder", new PacketEncoder(PacketFlow.SERVERBOUND))
                                            .addLast("packet_handler", clientConnection);
                                }
                            })
                            .connect(address);

                    ch.addListener(future1 -> {
                        if (!future1.isSuccess()) {
                            future.completeExceptionally(future1.cause());
                        } else {
                            ch.channel().eventLoop().execute(() -> { // needs to execute after channel init
                                clientConnection.setListener(new ClientStatusPacketListener() {
                                    @Override
                                    public void handleStatusResponse(ClientboundStatusResponsePacket packet) {
                                        ServerStatus meta = packet.status();
                                        if (meta != null && meta.version().isPresent()) {
                                            ProtocolVersion ver = ProtocolVersion.getProtocol(meta.version().get()
                                                    .protocol());
                                            future.complete(ver);
                                            ViaFabric.JLOGGER.info("Auto-detected " + ver + " for " + address);
                                        } else {
                                            future.completeExceptionally(new IllegalArgumentException("Null version in query response"));
                                        }
                                        clientConnection.disconnect(Component.empty());
                                    }

                                    @Override
                                    public void handlePongResponse(ClientboundPongResponsePacket packet) {
                                        clientConnection.disconnect(Component.literal("Pong not requested!"));
                                    }

                                    @Override
                                    public void onDisconnect(Component reason) {
                                        future.completeExceptionally(new IllegalStateException(reason.getString()));
                                    }

                                    @Override
                                    public boolean isAcceptingMessages() {
                                        return ch.channel().isOpen();
                                    }
                                });

                                clientConnection.send(new ClientIntentionPacket(address.getHostString(),
                                        address.getPort(), ConnectionProtocol.STATUS));
                                clientConnection.send(new ServerboundStatusRequestPacket());
                            });
                        }
                    });
                } catch (Throwable throwable) {
                    future.completeExceptionally(throwable);
                }

                return future;
            }));

    public static CompletableFuture<ProtocolVersion> detectVersion(InetSocketAddress address) {
        try {
            InetSocketAddress real = new InetSocketAddress(InetAddress.getByAddress
                    (new AddressParser().parse(address.getHostString()).serverAddress,
                            address.getAddress().getAddress()), address.getPort());
            return SERVER_VER.get(real);
        } catch (UnknownHostException | ExecutionException e) {
            ViaFabric.JLOGGER.log(Level.WARNING, "Protocol auto detector error: ", e);
            return CompletableFuture.completedFuture(null);
        }
    }
}
