package adpg.packetquery.query.client;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.logger.LoggingUtil;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketBuilder;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class Client {

    private Channel channel;
    private final EventLoopGroup group;

    /**
     * @apiNote Do NOT instantiate, use {@link PacketQuery#initClient(String, int) PacketQuery.initClient}
     */
    public Client(String name, int port){
        group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            channel = bootstrap.connect("localhost", port).sync().channel();

            String serializedNamePacket = PacketSerializer.toString(new PacketBuilder()
                    .write("socketquery.client.name")
                    .write(name)
                    .build()
            );
            channel.writeAndFlush(serializedNamePacket + "\r\n");

            LoggingUtil.info("Started Client on port " + port + " with name \"" + name + "\"");
        } catch (InterruptedException e) {
            LoggingUtil.error("An error occurred, please report it: " + LoggingUtil.link);
            e.getCause().printStackTrace();
        }
    }

    /**
     * Sends a packet to the Server
     */
    public void sendPacketToServer(Packet packet){
        channel.writeAndFlush(PacketSerializer.toString(packet) + "\r\n");

        if(PacketQuery.isDebugEnabled()){
            LoggingUtil.info("Sent a packet to the Server: \n" + PacketSerializer.toString(packet));
        }
    }

    /**
     * Stops the Client and disconnects from the Server
     */
    public void stop(){
        channel.close();
        group.shutdownGracefully();

        LoggingUtil.info("Stopped Client");
    }

}
