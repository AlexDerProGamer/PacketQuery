package adpg.packetquery.logger;

import org.tinylog.Logger;

@SuppressWarnings("unused")
public class QueryLogger {

    public static final String link = "https://github.com/AlexDerProGamer/PacketQuery/issues";

    public static void info(String message){
        Logger.info("[PacketQuery] " + message);
    }

    public static void warn(String message){
        Logger.warn("[PacketQuery] " + message);
    }

    public static void error(String message){
        Logger.error("[PacketQuery] " + message);
    }

    public static void debug(String message){
        Logger.debug("[PacketQuery] " + message);
    }

}
