package adpg.packetquery.packet;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class PacketBuilder {

    private final ArrayList<String> content = new ArrayList<>();

    /**
     * Add a field in the packet that can be read using {@link Packet#read() Packet.read}
     * <br>
     * You can add as many fields as you want
     */
    public PacketBuilder write(String message){
        content.add(message);
        return this;
    }

    /**
     * Add a specific field in the packet that can be read using {@link Packet#read() Packet.read}
     * <br>
     * You can add as many fields as you want
     */
    public PacketBuilder writeAt(String message, int index){
        content.add(index, message);
        return this;
    }

    /**
     * @return The {@link Packet Packet} with the written fields
     */
    public Packet build(){
        return new Packet(content);
    }
}
