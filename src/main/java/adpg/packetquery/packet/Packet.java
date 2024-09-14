package adpg.packetquery.packet;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Packet {

    private int index = 0;
    private final ArrayList<String> content;

    /**
     * @apiNote Use a {@link PacketBuilder PacketBuilder} to create a {@link Packet Packet}
     */
    public Packet(@JsonProperty("field_name") ArrayList<String> content){
        this.content = content;
    }

    /**
     * @return The next field in the packet, may be {@code null} if its empty
     */
    public String read(){
        String message = null;

        if(index < content.size()){
            message = content.get(index);
        }
        index++;

        return message;
    }

    /**
     * @return The specific field in the packet, may be {@code null} if its empty
     */
    public String readAt(int index){
        String message = null;

        if(index < content.size()){
            message = content.get(index);
        }

        return message;
    }

    /**
     * Resets the reader so the next time a field is read it will start from the beginning
     */
    public void resetReader(){
        index = 0;
    }

}
