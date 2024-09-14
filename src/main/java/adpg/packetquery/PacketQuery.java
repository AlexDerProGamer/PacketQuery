package adpg.packetquery;

import adpg.packetquery.event.ClientPacketMessageEvent;
import adpg.packetquery.event.ServerPacketMessageEvent;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.query.client.Client;
import adpg.packetquery.query.server.Server;

import java.util.ArrayList;

@SuppressWarnings({"unused", "JavadocDeclaration"})
public class PacketQuery {

    private static boolean debug = false;
    private static final ArrayList<ServerPacketMessageEvent> serverListeners = new ArrayList<>();
    private static final ArrayList<ClientPacketMessageEvent> clientListeners = new ArrayList<>();

    public static void enableDebugMessages(boolean enable){
        debug = enable;
    }

    public static boolean isDebugEnabled(){
        return debug;
    }

    /**
     * Use this method, to initialize this instance as the Server
     * @apiNote Do NOT use {@link #initServer(int) initServer} and {@link #initClient(String, int) initClient} at the same time!<br>
     * Only call this method ONCE
     */
    public static Server initServer(int port){
        return new Server(port);
    }

    /**
     * Use this method, to initialize this instance as the Client
     * @apiNote Do NOT use {@link #initServer(int) initServer} and {@link #initClient(String, int) initClient} at the same time!<br>
     * Only call this method ONCE
     */
    public static Client initClient(String name, int port){
        return new Client(name, port);
    }

    /**
     * Register your {@link ServerPacketMessageEvent ServerPacketMessageEvent} listener
     */
    public static void addServerMessageListener(ServerPacketMessageEvent listener){
        serverListeners.add(listener);
    }

    /**
     * Register your {@link ClientPacketMessageEvent ClientPacketMessageEvent} listener
     */
    public static void addClientMessageListener(ClientPacketMessageEvent listener){
        clientListeners.add(listener);
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     */
    public static void fireServerMessageEvent(Packet packet){
        for(ServerPacketMessageEvent listener : serverListeners){
            listener.onServerMessageReceive(packet);
        }
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     */
    public static void fireClientMessageEvent(String clientName, Packet packet){
        for(ClientPacketMessageEvent listener : clientListeners){
            listener.onClientMessageReceive(clientName, packet);
        }
    }

}
