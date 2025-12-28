/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.mc12111.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.mc12111.ViaFabric;
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
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.UnconfiguredPipelineHandler;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ProtocolAutoDetector {
    private static final LoadingCache<InetSocketAddress, CompletableFuture<ProtocolVersion>> SERVER_VER = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build(CacheLoader.from((address) -> {
            CompletableFuture<ProtocolVersion> future = new CompletableFuture<>();

            try {
                final Connection clientConnection = new Connection(PacketFlow.CLIENTBOUND);
                final EventLoopGroupHolder eventLoopGroupHolder = EventLoopGroupHolder.remote(Minecraft.getInstance().options.useNativeTransport());

                ChannelFuture ch = new Bootstrap()
                    .group(eventLoopGroupHolder.eventLoopGroup())
                    .channel(eventLoopGroupHolder.channelCls())
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
                                .addLast("splitter", new Varint21FrameDecoder(null))
                                .addLast("inbound_config", new UnconfiguredPipelineHandler.Inbound())
                                .addLast("prepender", new Varint21LengthFieldPrepender())
                                .addLast("encoder", new PacketEncoder<>(HandshakeProtocols.SERVERBOUND))
                                .addLast("packet_handler", clientConnection);
                        }
                    })
                    .connect(address);

                ch.addListener(future1 -> {
                    if (!future1.isSuccess()) {
                        future.completeExceptionally(future1.cause());
                    } else {
                        ch.channel().eventLoop().submit(() -> { // needs to execute after channel init
                            clientConnection.setupInboundProtocol(StatusProtocols.CLIENTBOUND, new ClientStatusPacketListener() {
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
                                public void onDisconnect(DisconnectionDetails info) {
                                    future.completeExceptionally(new IllegalStateException(info.reason().getString()));
                                }

                                @Override
                                public boolean isAcceptingMessages() {
                                    return ch.channel().isOpen();
                                }
                            });

                            //noinspection deprecation
                            clientConnection.send(new ClientIntentionPacket(
                                SharedConstants.getCurrentVersion().protocolVersion(),
                                address.getHostString(),
                                address.getPort(),
                                ClientIntent.STATUS
                            ));

                            clientConnection.setupOutboundProtocol(StatusProtocols.SERVERBOUND);
                            clientConnection.send(ServerboundStatusRequestPacket.INSTANCE);
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
                (AddressParser.parse(address.getHostString()).serverAddress(),
                    address.getAddress().getAddress()), address.getPort());
            return SERVER_VER.get(real);
        } catch (UnknownHostException | ExecutionException e) {
            ViaFabric.JLOGGER.log(Level.WARNING, "Protocol auto detector error: ", e);
            return CompletableFuture.completedFuture(null);
        }
    }
}
