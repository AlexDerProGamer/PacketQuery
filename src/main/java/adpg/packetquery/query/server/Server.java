package adpg.packetquery.query.server;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.logger.QueryLogger;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class Server {

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    /**
     * @apiNote Do NOT instantiate, use {@link PacketQuery#initServer(int) PacketQuery.initServer}
     */
    public Server(int port){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());

            bootstrap.bind(port).sync();
            QueryLogger.info("Started Server on port " + port);
        } catch (InterruptedException e) {
            QueryLogger.error("An error occurred, please report it: " + QueryLogger.link);
            e.getCause().printStackTrace();
        }
    }

    /**
     * @return All names of the Clients connected to the Server
     */
    public ArrayList<String> getConnectedClients(){
        return new ArrayList<>(ServerHandler.clients.keySet());
    }

    /**
     * Sends a packet to a Client
     * @param clientName The name of the Client (case-sensitive)
     */
    public void sendPacketToClient(String clientName, Packet packet){
        if(!ServerHandler.clients.containsKey(clientName)){
            QueryLogger.warn("There is no client named \"" + clientName + "\" connected, not sending packet:\n" + PacketSerializer.toString(packet));
            return;
        }

        ServerHandler.sendPacketToClient(clientName, packet);

        if(PacketQuery.isDebugEnabled()){
            QueryLogger.info("Sent a packet to the Client named \"" + clientName + "\" [" + ServerHandler.clients.get(clientName).remoteAddress() + "]: \n" + PacketSerializer.toString(packet));
        }
    }

    /**
     * Disconnects a specific Client
     * @param clientName The name of the Client (case-sensitive)
     */
    public void disconnectClient(String clientName){
        ServerHandler.disconnectClient(clientName);
    }

    /**
     * Stops the Server and disconnects all Clients
     */
    public void stop(){
        ServerHandler.disconnectAllClientsOnStop();

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        QueryLogger.info("Stopping Server...");
    }

}
