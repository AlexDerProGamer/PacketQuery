package adpg.packetquery.query.client;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.logger.QueryLogger;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketBuilder;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.ConnectException;

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

            ChannelFuture future = bootstrap.connect("localhost", port);

            channel = future.sync().channel();

            String serializedNamePacket = PacketSerializer.toString(new PacketBuilder()
                    .write("socketquery.client.name")
                    .write(name)
                    .build()
            );
            channel.writeAndFlush(serializedNamePacket + "\r\n");

            QueryLogger.info("Started Client on port " + port + " with name \"" + name + "\"");
        } catch (Exception e) {
            //noinspection ConstantValue
            if(e instanceof ConnectException){
                QueryLogger.error("Can't connect to Server: No Server running on the port " + port);
            }else{
                QueryLogger.error("Can't connect to Server: No Server running on the port " + port);
                e.getCause().printStackTrace();
            }
        }
    }

    /**
     * Sends a packet to the Server
     */
    public void sendPacketToServer(Packet packet){
        channel.writeAndFlush(PacketSerializer.toString(packet) + "\r\n");

        if(PacketQuery.isDebugEnabled()){
            QueryLogger.info("Sent a packet to the Server: \n" + PacketSerializer.toString(packet));
        }
    }

    /**
     * Stops the Client and disconnects from the Server
     */
    public void stop(){
        channel.close();
        group.shutdownGracefully();

        QueryLogger.info("Stopped Client");
    }

}
