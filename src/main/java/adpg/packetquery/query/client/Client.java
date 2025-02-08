package adpg.packetquery.query.client;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketBuilder;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.ApiStatus;

import static adpg.packetquery.PacketQuery.LOGGER;

public class Client {

    private final Channel CHANNEL;
    private final EventLoopGroup GROUP;

    /// @see PacketQuery#initClient(String, int)
    @ApiStatus.Internal
    public Client(String name, int port) {
        GROUP = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(GROUP)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            LOGGER.info("Starting client \"{}\" on port {}", name, port);
            ChannelFuture future = bootstrap.connect("localhost", port);

            CHANNEL = future.sync().channel();

            String serializedNamePacket = PacketSerializer.toString(new PacketBuilder()
                    .write("packetquery.client.name")
                    .write(name)
                    .build()
            );
            CHANNEL.writeAndFlush(serializedNamePacket + System.lineSeparator());
        } catch (Exception e) {
            throw new RuntimeException("Could not start the client \"" + name + "\"", e);
        }
    }

    /// Sends a packet to the server
    public void sendPacketToServer(Packet packet) {
        if (PacketQuery.isDebugEnabled()) {
            LOGGER.info("Sending packet to server: {}", PacketSerializer.toString(packet));
        }

        CHANNEL.writeAndFlush(PacketSerializer.toString(packet) + System.lineSeparator());
    }

    /// Stops the client and disconnects from the server
    public void stop() {
        LOGGER.info("Stopping client");

        CHANNEL.close();
        GROUP.shutdownGracefully();
    }

}
