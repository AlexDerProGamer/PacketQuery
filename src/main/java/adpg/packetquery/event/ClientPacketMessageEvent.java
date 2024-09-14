package adpg.packetquery.event;

import adpg.packetquery.packet.Packet;

/**
 * Use this event to read received packets from clients
 */
public interface ClientPacketMessageEvent {

    /**
     * Use this event to read received packets from clients
     * @param clientName The name of the client that sent the packet
     * @param packet The packet sent to this server
     */
    void onClientMessageReceive(String clientName, Packet packet);

}
