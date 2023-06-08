package com.viaversion.fabric.mc120.service;

import com.viaversion.fabric.common.AddressParser;
import com.viaversion.fabric.mc120.ViaFabric;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
                                    public void onPong(QueryPongS2CPacket packet) {
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

                                clientConnection.send(new HandshakeC2SPacket(address.getHostString(),
                                        address.getPort(), NetworkState.STATUS));
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
