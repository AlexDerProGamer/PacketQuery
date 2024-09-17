package adpg.packetquery.query.server;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.logger.QueryLogger;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "RedundantThrows", "CallToPrintStackTrace"})
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public static final HashMap<Channel, String> waiting = new HashMap<>();
    public static final HashMap<String, Channel> clients = new HashMap<>();

    private String getClientNameByChannel(Channel channel){
        for(String name : clients.keySet()){
            //no need for a null check
            if(clients.get(name) == channel){
                return name;
            }
        }

        return "N/A";
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        Channel client = context.channel();
        waiting.put(client, client.remoteAddress().toString());
        if(PacketQuery.isDebugEnabled()){
            QueryLogger.info("New Client with Address " + client.remoteAddress() + " connected to the Server, waiting for name...");
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        Channel client = context.channel();
        String name = getClientNameByChannel(client);

        QueryLogger.info("Client named \"" + name + "\" [" + client.remoteAddress() + "] disconnected");

        client.close();
        clients.remove(getClientNameByChannel(client));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        Channel client = context.channel();
        Packet packet = PacketSerializer.fromString(message);

        //name packet handling/client registration
        if(waiting.containsKey(client)){
            if(packet.read().equals("socketquery.client.name")){
                String name = packet.read();

                if(clients.containsKey(name)){
                    client.close();
                    QueryLogger.error("New Client with Address " + client.remoteAddress() + " provided the same name as Client with Address " + clients.get(name).remoteAddress() + " (\"" + name + "\"), disconnecting");
                }else if(clients.containsValue(client)){
                    client.close();
                    QueryLogger.error("New Client with Address " + client.remoteAddress() + " tried to connect but is already connected, disconnecting!");
                }else{
                    if(PacketQuery.isDebugEnabled()){
                        QueryLogger.info("Received name for Client with Address " + client.remoteAddress() + ": \"" + name + "\"");
                    }

                    QueryLogger.info("New Client named \"" + name + "\" [" + client.remoteAddress() + "] connected to the Server");
                    clients.put(name, client);
                }

                waiting.remove(client);
            }else{
                QueryLogger.error("New Client with Address " + client.remoteAddress() + " didn't send a name packet, disconnecting!");
            }
        }else if(clients.containsValue(client)){
            //packet handling
            String clientName = getClientNameByChannel(client);

            if(PacketQuery.isDebugEnabled()){
                QueryLogger.info("Client named \"" + clientName + "\" [" + client.remoteAddress() + "] sent the Server a packet: \n" + PacketSerializer.toString(packet));
            }

            PacketQuery.fireClientMessageEvent(clientName, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        ctx.close();
        QueryLogger.error("An error occurred, please report it: " + QueryLogger.link);
        cause.printStackTrace();
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     * @see Server#sendPacketToClient(String, Packet) Server.sendPacketToClient()
     */
    public static void sendPacketToClient(String clientName, Packet packet){
        Channel client = clients.get(clientName);
        if(client != null){
            if(client.isOpen() && client.isWritable()){
                client.writeAndFlush(PacketSerializer.toString(packet) + "\r\n");

                if(PacketQuery.isDebugEnabled()){
                    QueryLogger.info("Server sent the Client named \"" + clientName + "\" [" + client.remoteAddress() + "] a packet: \n" + PacketSerializer.toString(packet));
                }
            }else{
                client.close();
                clients.remove(clientName);
            }
        }
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     * @see Server#disconnectClient(String)  Server.disconnectClient()
     */
    public static void disconnectClient(String clientName){
        if(clients.containsKey(clientName)){
            clients.get(clientName).close();
            clients.remove(clientName);
        }
    }

    /**
     * @apiNote Made for internal use, no need to call this method
     */
    public static void disconnectAllClientsOnStop(){
        waiting.forEach((client, address) -> client.close());
        clients.forEach((name, client) -> client.close());

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            executor.execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    QueryLogger.error("An error occurred, please report it: " + QueryLogger.link);
                    e.getCause().printStackTrace();
                }

                waiting.clear();
                //no need to run clients.clear() because all clients get removed from the list when they disconnect
            });
        }
    }

}
