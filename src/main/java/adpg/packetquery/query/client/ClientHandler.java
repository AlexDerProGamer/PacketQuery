package adpg.packetquery.query.client;

import adpg.packetquery.PacketQuery;
import adpg.packetquery.logger.LoggingUtil;
import adpg.packetquery.packet.Packet;
import adpg.packetquery.packet.PacketSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@SuppressWarnings({"RedundantThrows", "CallToPrintStackTrace"})
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        //packet handling
        Packet packet = PacketSerializer.fromString(message);
        if(PacketQuery.isDebugEnabled()){
            LoggingUtil.info("Server sent the Client a packet: \n" + PacketSerializer.toString(packet));
        }

        PacketQuery.fireServerMessageEvent(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        ctx.close();
        LoggingUtil.error("An error occurred, please report it: " + LoggingUtil.link);
        cause.printStackTrace();
    }

}
