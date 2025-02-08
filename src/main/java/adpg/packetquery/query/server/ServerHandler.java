package adpg.packetquery.query.server;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.channel.*;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import static adpg.packetquery.PacketQuery.LOGGER;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public Boolean RUNNING = true;
    public final HashMap<Channel, SocketAddress> QUEUED_CLIENTS = new HashMap<>();
    public final HashMap<String, Channel> CONNECTED_CLIENTS = new HashMap<>();

    @Nullable
    private String getClientNameByChannel(Channel channel) {
        HashMap<String, Channel> clients = CONNECTED_CLIENTS;

        for (String name : clients.keySet()) {
            if (clients.get(name).equals(channel)) {
                return name;
            }
        }

        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        Channel client = context.channel();
        if (CONNECTED_CLIENTS.containsValue(client)) {
            //a client lost connection without properly disconnecting
            String name = getClientNameByChannel(client);
            LOGGER.warn("Client \"{}\" ({}) lost connection and did not disconnect properly", name, client.remoteAddress());
            //we do not need to remove the client from the list because that will happen in the handlerRemoved method
            context.close();
        } else {
            super.exceptionCaught(context, cause);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        Channel client = context.channel();
        QUEUED_CLIENTS.put(client, client.remoteAddress());

        if (PacketQuery.isDebugEnabled()) {
            LOGGER.info("New Client ({}) connected, waiting for name packet...", client.remoteAddress());
        }

        QUEUED_CLIENTS.put(client, client.remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        if (RUNNING) {
            Channel client = context.channel();
            String name = getClientNameByChannel(client);

            LOGGER.info("Client \"{}\" ({}) disconnected", name, client.remoteAddress());

            client.close();
            CONNECTED_CLIENTS.remove(name);
        }
    }

    private void processNamePacket(Channel client, Packet packet) {
        String field = packet.read();
        String name = packet.read();
        SocketAddress remoteAddress = client.remoteAddress();

        if (field != null && field.equals("packetquery.client.name")) {
            if (name != null) {
                if (!CONNECTED_CLIENTS.containsKey(name)) {
                    //everything is correct, register client
                    if (PacketQuery.isDebugEnabled()) {
                        LOGGER.info("Received name for client \"{}\" ({})", name, remoteAddress);
                    }

                    LOGGER.info("New client \"{}\" ({}) connected to the server", name, remoteAddress);
                    QUEUED_CLIENTS.remove(client);
                    CONNECTED_CLIENTS.put(name, client);
                } else {
                    QUEUED_CLIENTS.remove(client);
                    client.close();
                    LOGGER.error("Client ({}) provided the same name as \"{}\" ({}), disconnecting", remoteAddress, name, CONNECTED_CLIENTS.get(name).remoteAddress());
                }
            } else {
                QUEUED_CLIENTS.remove(client);
                client.close();
                LOGGER.error("Client ({}) did not specify a name in the packet, disconnecting", remoteAddress);
            }
        } else {
            QUEUED_CLIENTS.remove(client);
            client.close();
            LOGGER.error("Client ({}) did not send a name packet, disconnecting", remoteAddress);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) {
        Channel client = context.channel();
        Packet packet = PacketSerializer.fromString(message);

        if (QUEUED_CLIENTS.containsKey(client)) {
            //register client & receive name packet
            processNamePacket(client, packet);
        } else if (CONNECTED_CLIENTS.containsValue(client)) {
            //process packet
            String name = getClientNameByChannel(client);

            if (PacketQuery.isDebugEnabled()) {
                LOGGER.info("Client \"{}\" ({}) sent a packet: {}", name, client.remoteAddress(), message);
            }

            PacketQuery.fireClientMessageEvent(name, packet);
        }
    }

    public boolean sendPacketToClient(String name, Packet packet) {
        Channel client = CONNECTED_CLIENTS.get(name);
        if (client == null) {
            throw new NullPointerException("Channel of client \"" + name + "\" is null");
        }

        if (client.isOpen() && client.isWritable()) {
            if (PacketQuery.isDebugEnabled()) {
                LOGGER.info("Sending packet to client \"{}\" ({}): {}", name, client.remoteAddress(), PacketSerializer.toString(packet));
            }
            client.writeAndFlush(PacketSerializer.toString(packet) + System.lineSeparator());
            return true;
        } else {
            CONNECTED_CLIENTS.remove(name);
            client.close();
        }

        return false;
    }

    public void disconnectClient(String clientName) {
        if (CONNECTED_CLIENTS.containsKey(clientName)) {
            CONNECTED_CLIENTS.get(clientName).close();
            //we do not need to remove the client from the list because that will happen in the handlerRemoved method
        }
    }

    public void disconnectAllClientsOnStop() {
        RUNNING = false;

        ArrayList<ChannelFuture> closeFutures = new ArrayList<>();
        QUEUED_CLIENTS.forEach((queuedClient, address) -> closeFutures.add(queuedClient.close()));
        CONNECTED_CLIENTS.forEach((name, client) -> closeFutures.add(client.close()));

        closeFutures.forEach(future -> {
            try {
                future.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        QUEUED_CLIENTS.clear();
        CONNECTED_CLIENTS.clear();

        LOGGER.info("All clients were disconnected");
    }

}
