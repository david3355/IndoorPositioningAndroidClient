package thesis.jager.indoorpositioning.client;

import thesis.jager.indoorpositioning.client.config.ConfigManager;

/**
 * Created by Jager on 2016.04.27..
 */
public class ServerData
{
       public static  String HOST = null;
       public final static int PORT_WS = 8081;
       public final static int PORT_REST = 2551;

       private final static String WSSERVER_CONNSTRING_TEMPLATE = "ws://%s:%s/WebSocketServer/websocketServerEndpoint";

       public static String getWebsocketServerConnectionString(String host, int port)
       {
              return String.format(WSSERVER_CONNSTRING_TEMPLATE, host, port);
       }
}
