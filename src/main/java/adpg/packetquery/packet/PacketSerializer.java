package adpg.packetquery.packet;

import java.io.*;
import java.util.Base64;

public class PacketSerializer {

    public static String toString(Packet packet) {
        String serializedPacket;

        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(byteArrayOut)) {
            packet.resetReader();
            objectOut.writeObject(packet);
            serializedPacket = Base64.getEncoder().encodeToString(byteArrayOut.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return serializedPacket;
    }

    public static Packet fromString(String serializedPacket) {
        Packet packet;

        byte [] data = Base64.getDecoder().decode(serializedPacket);
        try (ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(data))) {
            packet = (Packet) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        packet.resetReader();
        return packet;
    }

}
