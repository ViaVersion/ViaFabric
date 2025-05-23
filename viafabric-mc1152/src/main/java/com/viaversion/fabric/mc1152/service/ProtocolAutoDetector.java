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
package com.viaversion.fabric.mc1152.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.mc1152.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

@Environment(EnvType.CLIENT)
public class ProtocolAutoDetector {
    private static final LoadingCache<InetSocketAddress, CompletableFuture<ProtocolVersion>> SERVER_VER = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build(CacheLoader.from((address) -> {
            CompletableFuture<ProtocolVersion> future = new CompletableFuture<>();

            try {
                final Connection connection = new Connection(PacketFlow.CLIENTBOUND);

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
                                .addLast("packet_handler", connection);
                        }
                    })
                    .connect(address);

                ch.addListener(future1 -> {
                    if (!future1.isSuccess()) {
                        future.completeExceptionally(future1.cause());
                    } else {
                        ch.channel().eventLoop().execute(() -> { // needs to execute after channel init
                            connection.setListener(new ClientStatusPacketListener() {
                                @Override
                                public void handleStatusResponse(ClientboundStatusResponsePacket clientboundStatusResponsePacket) {
                                    ServerStatus status = clientboundStatusResponsePacket.getStatus();
                                    ServerStatus.Version version;
                                    if (status != null && (version = status.getVersion()) != null) {
                                        ProtocolVersion ver = ProtocolVersion.getProtocol(version.getProtocol());
                                        future.complete(ver);
                                        ViaFabric.JLOGGER.info("Auto-detected " + ver + " for " + address);
                                    } else {
                                        future.completeExceptionally(new IllegalArgumentException("Null version in query response"));
                                    }
                                    connection.disconnect(new TextComponent(""));
                                }

                                @Override
                                public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {
                                    connection.disconnect(new TextComponent("Pong not requested!"));
                                }

                                @Override
                                public void onDisconnect(Component reason) {
                                    future.completeExceptionally(new IllegalStateException(reason.getContents()));
                                }

                                @Override
                                public Connection getConnection() {
                                    return connection;
                                }
                            });

                            connection.send(new ClientIntentionPacket(address.getHostString(),
                                address.getPort(), ConnectionProtocol.STATUS));
                            connection.send(new ServerboundStatusRequestPacket());
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
