import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketBuilder;
import adpg.packetquery.packet.PacketSerializer;
import adpg.packetquery.query.client.Client;

import java.util.Scanner;

import static adpg.packetquery.PacketQuery.LOGGER;

public class TestClient {

    public static void main(String[] args) {
        Packet originalPacket = new PacketBuilder()
                .write("this is a test packet 😄")
                .write("does this work? 🤔❓")
                .build();
        while (originalPacket.hasNext()) {
            LOGGER.info(originalPacket.read());
        }

        String serializedPacket = PacketSerializer.toString(originalPacket);
        System.out.println(serializedPacket);

        Packet stringPacket = PacketSerializer.fromString(serializedPacket);
        while (stringPacket.hasNext()) {
            LOGGER.info(stringPacket.read());
        }

        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        PacketQuery.enableDebug(true);
        PacketQuery.addServerMessageListener((packet) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("packet from server:");
            while (packet.hasNext()) {
                builder.append("\n" + packet.read());
            }
            LOGGER.info(builder.toString());
        });
        Client client = PacketQuery.initClient(name, 12345);

        boolean RUNNING = true;
        while (RUNNING) {
            String input = scanner.nextLine();
            if (input.equals("stop")) {
                client.stop();
                RUNNING = false;
            } else if (input.startsWith("packet ")) {
                String[] fields = input.substring(7).split(";");
                PacketBuilder builder = new PacketBuilder();
                for (String field : fields) {
                    builder.write(field);
                }

                client.sendPacketToServer(builder.build());
            }
        }
    }

}
