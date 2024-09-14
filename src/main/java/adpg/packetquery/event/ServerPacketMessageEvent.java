package adpg.packetquery.event;

import adpg.packetquery.packet.Packet;

/**
 * Use this event to read received packets from the server
 */
public interface ServerPacketMessageEvent {

    /**
     * Use this event to read received packets from the server
     * @param packet The packet sent to this client
     */
    void onServerMessageReceive(Packet packet);

}
