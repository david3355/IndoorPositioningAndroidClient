package thesis.jager.indoorpositioning.client;

import android.support.annotation.Nullable;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;

/**
 * Created by Jager on 2016.03.24..
 */
public class AndroidWebsocketClient extends WebSocketClient implements BasicWebsocketClientInterface
{
       private String host;
       private int port;

       private WebsocketServerEventCallback websocketServerEvent;

       public AndroidWebsocketClient(String host, int port)
       {
              super(getServerAccessPath(host, port), new Draft_17());
              this.host = host;
              this.port = port;
              websocketServerEvent = null;
       }

       @Nullable
       public static URI getServerAccessPath(String host, int port)
       {
              try
              {
                     return new URI(ServerData.getWebsocketServerConnectionString(host, port));
              }
              catch (Exception e)
              {
                     return null;
              }
       }

       public void subscribeWebsocketEventHandler(WebsocketServerEventCallback eventHandler)
       {
              this.websocketServerEvent = eventHandler;
       }

       public void unsubscribeWebsocketEventHandler()
       {
              this.websocketServerEvent = null;
       }

       public boolean isConnected()
       {
              return this.getConnection().isOpen();
       }

       @Override
       public int getRemotePort()
       {
              return this.port;
       }

       @Override
       public String getRemoteAddress()
       {
              return this.host;
       }

       @Override
       public void connectToServer()
       {
              this.connect();
       }

       @Override
       public void disconnectFromServer()
       {
              this.close();
       }

       @Override
       public void sendMessage(String message)
       {
              this.send(message);
       }

       @Override
       public void onOpen(ServerHandshake handshakedata)
       {
              //websocketServerEvent.onEvent("Connected to server!");        ebben a pillanatban az eventhandler még null!
       }

       @Override
       public void onMessage(String message)
       {
              if (websocketServerEvent != null)
                     websocketServerEvent.serverMessage(message);
       }

       @Override
       public void onClose(int code, String reason, boolean remote)
       {
              if (websocketServerEvent != null)
                     websocketServerEvent.onEvent("Disconnected from server!");
       }

       @Override
       public void onError(Exception ex)
       {
              if (websocketServerEvent != null)
                     websocketServerEvent.onEvent("ERROR: " + ex.getMessage());
       }


}
