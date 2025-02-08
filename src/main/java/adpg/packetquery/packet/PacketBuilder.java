package adpg.packetquery.packet;

import java.util.ArrayList;

public class PacketBuilder {

    private final ArrayList<String> content = new ArrayList<>();

    /**
     * Write to the next field in the packet
     * <br>
     * There is no field limit
     */
    public PacketBuilder write(String message) {
        content.add(message);
        return this;
    }

    /**
     * Write to a specific field in the packet
     * <br>
     * There is no field limit
     */
    public PacketBuilder writeAt(int index, String message) {
        content.add(index, message);
        return this;
    }

    /// @return the {@link Packet Packet} with the written fields
    public Packet build() {
        return new Packet(content);
    }

}
