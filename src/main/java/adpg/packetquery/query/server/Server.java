package adpg.packetquery.query.server;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;

import java.util.ArrayList;

import static adpg.packetquery.PacketQuery.LOGGER;

public class Server {

    private final EventLoopGroup BOSS_GROUP;
    private final EventLoopGroup WORKER_GROUP;
    private final ServerInitializer SERVER_INITIALIZER;


    /// @see PacketQuery#initServer(int)
    @ApiStatus.Internal
    public Server(int port) {
        BOSS_GROUP = new NioEventLoopGroup();
        WORKER_GROUP = new NioEventLoopGroup();
        SERVER_INITIALIZER = new ServerInitializer();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(BOSS_GROUP, WORKER_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(SERVER_INITIALIZER);

            bootstrap.bind(port).sync();
            LOGGER.info("Started server on port {}", port);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /// @return a {@link ArrayList<String>} with all client names
    public ArrayList<String> getConnectedClients() {
        return new ArrayList<>(SERVER_INITIALIZER.SERVER_HANDLER.CONNECTED_CLIENTS.keySet());
    }

    /**
     * Sends a {@link Packet} to a client
     * @param client The name of the client (case-sensitive)
     * @return if the packet was sent
     */
    public boolean sendPacketToClient(String client, Packet packet) {
        if (!SERVER_INITIALIZER.SERVER_HANDLER.CONNECTED_CLIENTS.containsKey(client)) {
            LOGGER.error("Failed to send packet: No client named \"{}\" is connected", client);
            return false;
        }

        return SERVER_INITIALIZER.SERVER_HANDLER.sendPacketToClient(client, packet);
    }

    /**
     * Disconnects a specific client
     * @param client The name of the client (case-sensitive)
     */
    public void disconnectClient(String client) {
        SERVER_INITIALIZER.SERVER_HANDLER.disconnectClient(client);
    }

    /// Stops the server and waits until all clients are disconnected
    @Blocking
    public void stop() {
        LOGGER.info("Stopping server");

        SERVER_INITIALIZER.SERVER_HANDLER.disconnectAllClientsOnStop();
        BOSS_GROUP.shutdownGracefully();
        WORKER_GROUP.shutdownGracefully();
    }

}
