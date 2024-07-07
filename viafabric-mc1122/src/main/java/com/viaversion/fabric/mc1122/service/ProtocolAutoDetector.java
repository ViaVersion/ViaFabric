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
package com.viaversion.fabric.mc1122.service;

import com.viaversion.fabric.mc1122.ViaFabric;
import com.viaversion.fabric.common.AddressParser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.viaversion.fabric.mc1122.mixin.pipeline.client.MixinHandshakeC2SPacketAccessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.*;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.Via;

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
                    final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);

                    ChannelFuture ch = new Bootstrap()
                            .group(ClientConnection.field_11553.get())
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
                                            .addLast("splitter", new SplitterHandler())
                                            .addLast("decoder", new DecoderHandler(NetworkSide.CLIENTBOUND))
                                            .addLast("prepender", new SizePrepender())
                                            .addLast("encoder", new PacketEncoder(NetworkSide.SERVERBOUND))
                                            .addLast("packet_handler", clientConnection);
                                }
                            })
                            .connect(address);

                    ch.addListener(future1 -> {
                        if (!future1.isSuccess()) {
                            future.completeExceptionally(future1.cause());
                        } else {
                            ch.channel().eventLoop().execute(() -> { // needs to execute after channel init
                                clientConnection.setPacketListener(new ClientQueryPacketListener() {
                                    @Override
                                    public void onResponse(QueryResponseS2CPacket packet) {
                                        ServerMetadata meta = packet.getServerMetadata();
                                        ServerMetadata.Version version;
                                        if (meta != null && (version = meta.getVersion()) != null) {
                                            ProtocolVersion ver = ProtocolVersion.getProtocol(version.getProtocolVersion());
                                            future.complete(ver);
                                            ViaFabric.JLOGGER.info("Auto-detected " + ver + " for " + address);
                                        } else {
                                            future.completeExceptionally(new IllegalArgumentException("Null version in query response"));
                                        }
                                        clientConnection.disconnect(new LiteralText(""));
                                    }

                                    @Override
                                    public void onPong(QueryPongS2CPacket packet) {
                                        clientConnection.disconnect(new LiteralText("Pong not requested!"));
                                    }

                                    @Override
                                    public void onDisconnected(Text reason) {
                                        future.completeExceptionally(new IllegalStateException(reason.asUnformattedString()));
                                    }
                                });

                                HandshakeC2SPacket handshakeC2SPacket = new HandshakeC2SPacket(address.getHostString(),
                                        address.getPort(), NetworkState.STATUS);
                                ((MixinHandshakeC2SPacketAccessor) handshakeC2SPacket).setProtocolVersion(
                                        RealmsSharedConstants.NETWORK_PROTOCOL_VERSION);
                                clientConnection.send(handshakeC2SPacket);
                                clientConnection.send(new QueryRequestC2SPacket());
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
