package adpg.packetquery.query.client;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static adpg.packetquery.PacketQuery.LOGGER;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        LOGGER.warn("Lost connection to the server");
        context.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        LOGGER.info("Successfully connected to the server");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        LOGGER.info("Disconnected from the server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) {
        Packet packet = PacketSerializer.fromString(message);

        if (PacketQuery.isDebugEnabled()) {
            LOGGER.info("Received a packet from the server: {}", message);
        }

        PacketQuery.fireServerMessageEvent(packet);
    }

}
