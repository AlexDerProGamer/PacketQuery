import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.PacketBuilder;
import adpg.packetquery.query.server.Server;

import java.util.Scanner;

import static adpg.packetquery.PacketQuery.LOGGER;

public class TestServer {

    public static void main(String[] args) {
        PacketQuery.enableDebug(true);
        PacketQuery.addClientMessageListener((client, packet) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(client + "'s packet:");
            while (packet.hasNext()) {
                builder.append("\n" + packet.read());
            }
            LOGGER.info(builder.toString());
        });
        Server server = PacketQuery.initServer(12345);

        Scanner scanner = new Scanner(System.in);
        boolean RUNNING = true;
        while (RUNNING) {
            String input = scanner.nextLine();
            switch (input) {
                case "stop":
                    server.stop();
                    RUNNING = false;
                    break;
                case "list":
                    LOGGER.info(server.getConnectedClients().toString());
                    break;
                case "disconnect":
                    server.getConnectedClients().forEach(server::disconnectClient);
                    break;
                default:
                    // packet clientName field1;field2;field3
                    if (input.startsWith("packet ")) {
                        input = input.replaceFirst("packet ", "");

                        String client = input.substring(0, input.indexOf(" "));
                        input = input.replaceFirst(client + " ", "");

                        String[] fields = input.split(";");
                        PacketBuilder builder = new PacketBuilder();
                        for (String field : fields) {
                            builder.write(field);
                        }

                        server.sendPacketToClient(client, builder.build());
                    }
                    break;
            }
        }

    }

}
