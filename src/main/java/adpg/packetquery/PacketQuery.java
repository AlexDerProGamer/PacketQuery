package adpg.packetquery;

import adpg.packetquery.event.ClientPacketMessageEvent;
import adpg.packetquery.event.ServerPacketMessageEvent;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.query.client.Client;
import adpg.packetquery.query.server.Server;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PacketQuery {

    public static final Logger LOGGER = LoggerFactory.getLogger(PacketQuery.class);

    private static boolean DEBUG = false;
    private static final ArrayList<ServerPacketMessageEvent> SERVER_LISTENERS = new ArrayList<>();
    private static final ArrayList<ClientPacketMessageEvent> CLIENT_LISTENERS = new ArrayList<>();

    public static boolean isDebugEnabled() {
        return DEBUG;
    }

    public static void enableDebug(boolean enable) {
        DEBUG = enable;
    }

    /**
     * Initializes this instance as the server
     * @apiNote Do NOT use {@link #initServer(int)} and {@link #initClient(String, int)} at the same time!<br>
     * Only call this method ONCE
     */
    public static Server initServer(int port) {
        return new Server(port);
    }

    /**
     * Initializes this instance as the client
     * @apiNote Do NOT use {@link #initServer(int)} and {@link #initClient(String, int)} at the same time!<br>
     * Only call this method ONCE
     */
    public static Client initClient(String name, int port) {
        return new Client(name, port);
    }

    /// Registers your {@link ServerPacketMessageEvent} listener
    public static void addServerMessageListener(ServerPacketMessageEvent listener) {
        SERVER_LISTENERS.add(listener);
    }

    /// Registers your {@link ClientPacketMessageEvent} listener
    public static void addClientMessageListener(ClientPacketMessageEvent listener) {
        CLIENT_LISTENERS.add(listener);
    }

    /// This is made for internal usage but can be used to manipulate events
    @ApiStatus.Internal
    public static void fireServerMessageEvent(Packet packet) {
        SERVER_LISTENERS.forEach(listener -> listener.onServerMessageReceive(packet));
    }

    /// This is made for internal usage but can be used to manipulate events
    @ApiStatus.Internal
    public static void fireClientMessageEvent(String client, Packet packet) {
        CLIENT_LISTENERS.forEach(listener -> listener.onClientMessageReceive(client, packet));
    }

}
