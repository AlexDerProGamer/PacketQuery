package adpg.packetquery.event;

import adpg.packetquery.packet.Packet;

/**
 * Use this event to listen to client packets<br>
 * {@code client -> server}
 */
public interface ClientPacketMessageEvent {

    /**
     * Use this event to listen to client packets<br>
     * {@code client -> server}
     * @param packet The packet sent to the server
     */
    void onClientMessageReceive(String client, Packet packet);

}
