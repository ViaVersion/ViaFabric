/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
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
package com.viaversion.fabric.mc1206.service;

import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.mc1206.ViaFabric;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.*;
import net.minecraft.network.handler.*;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.network.state.HandshakeStates;
import net.minecraft.network.state.QueryStates;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ProtocolAutoDetector {
    private static final LoadingCache<InetSocketAddress, CompletableFuture<ProtocolVersion>> SERVER_VER = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(CacheLoader.from((address) -> {
                CompletableFuture<ProtocolVersion> future = new CompletableFuture<>();

                try {
                    final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);

                    ChannelFuture ch = new Bootstrap()
                            .group(ClientConnection.CLIENT_IO_GROUP.get())
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(@NotNull Channel channel) {
                                    try {
                                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                                        channel.config().setOption(ChannelOption.IP_TOS, 0x18); // Stolen from Velocity, low delay, high reliability
                                    } catch (ChannelException ignored) {
                                    }

                                    channel.pipeline()
                                            .addLast("timeout", new ReadTimeoutHandler(30))
                                            .addLast("splitter", new SplitterHandler(null))
                                            .addLast("inbound_config", new NetworkStateTransitions.InboundConfigurer())
                                            .addLast("prepender", new SizePrepender())
                                            .addLast("encoder", new EncoderHandler<>(HandshakeStates.C2S))
                                            .addLast("packet_handler", clientConnection);
                                }
                            })
                            .connect(address);

                    ch.addListener(future1 -> {
                        if (!future1.isSuccess()) {
                            future.completeExceptionally(future1.cause());
                        } else {
                            ch.channel().eventLoop().submit(() -> { // needs to execute after channel init
                                clientConnection.transitionInbound(QueryStates.S2C, new ClientQueryPacketListener() {
                                    @Override
                                    public void onResponse(QueryResponseS2CPacket packet) {
                                        ServerMetadata meta = packet.metadata();
                                        if (meta != null && meta.version().isPresent()) {
                                            ProtocolVersion ver = ProtocolVersion.getProtocol(meta.version().get()
                                                    .protocolVersion());
                                            future.complete(ver);
                                            ViaFabric.JLOGGER.info("Auto-detected " + ver + " for " + address);
                                        } else {
                                            future.completeExceptionally(new IllegalArgumentException("Null version in query response"));
                                        }
                                        clientConnection.disconnect(Text.empty());
                                    }

                                    @Override
                                    public void onPingResult(PingResultS2CPacket packet) {
                                        clientConnection.disconnect(Text.literal("Pong not requested!"));
                                    }

                                    @Override
                                    public void onDisconnected(Text reason) {
                                        future.completeExceptionally(new IllegalStateException(reason.getString()));
                                    }

                                    @Override
                                    public boolean isConnectionOpen() {
                                        return ch.channel().isOpen();
                                    }
                                });

                                //noinspection deprecation
                                clientConnection.send(new HandshakeC2SPacket(
                                        SharedConstants.getGameVersion().getProtocolVersion(),
                                        address.getHostString(),
                                        address.getPort(),
                                        ConnectionIntent.STATUS
                                ));

                                clientConnection.transitionOutbound(QueryStates.C2S);
                                clientConnection.send(QueryRequestC2SPacket.INSTANCE);
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
