package adpg.packetquery.event;

import adpg.packetquery.packet.Packet;

/**
 * Use this event to listen to server packets<br>
 * {@code server -> client}
 */
public interface ServerPacketMessageEvent {

    /**
     * Use this event to listen to server packets<br>
     * {@code server -> client}
     * @param packet The packet that was sent to this client
     */
    void onServerMessageReceive(Packet packet);

}
