package adpg.packetquery.packet;

import adpg.packetquery.logger.LoggingUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class PacketSerializer {

    private static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    /**
     * @apiNote Made for internal use, no need to call this method
     */
    public static String toString(Packet packet){
        String serializedPacket = null;

        try {
            serializedPacket = mapper.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            LoggingUtil.error("An error occurred, please report it: " + LoggingUtil.link);
            e.getCause().printStackTrace();
        }

        return serializedPacket;
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     */
    public static Packet fromString(String serializedPacket){
        Packet packet = null;

        try {
            packet = mapper.readValue(serializedPacket, Packet.class);
        } catch (JsonProcessingException e) {
            LoggingUtil.error("An error occurred, please report it: " + LoggingUtil.link);
            e.getCause().printStackTrace();
        }

        return packet;
    }

}
