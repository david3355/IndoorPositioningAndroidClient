package thesis.jager.indoorpositioning.client;

import java.util.UUID;

import jager.indoornav.wscconvention.Communicator;
import jager.indoornav.wscconvention.model.ClientIdentifier;
import jager.indoornav.wscconvention.model.Message;
import jager.indoornav.wscconvention.model.NewLocation;
import jager.indoornav.wscconvention.model.Task;
import jager.indoornav.wscconvention.model.Tasks;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.HTTPResponseEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;

/**
 * Created by Jager on 2016.04.21..
 */
public class Client
{
       private Client()
       {
              restClient = new RESTClient();
              wsClient = null;
              uniqueClientID = generateClientID();
              communicator = Communicator.getInstance();
              logged_username = null;
       }

       private static Client self;

       public static Client getInstance() throws Exception
       {
              if (self == null) self = new Client();
              return self;
       }

       private BasicWebsocketClientInterface wsClient;
       private RESTClient restClient;
       private Communicator communicator;
       private String uniqueClientID;
       private String logged_username;

       private String generateClientID()
       {
              return UUID.randomUUID().toString();
       }

       public boolean isLoggedIn()
       {
              return logged_username != null;
       }

       public String getUsername()
       {
              return logged_username;
       }

       public void setUserName(String username)
       {
              this.logged_username = username;
       }

       public boolean isConnected()
       {
              return wsClient != null && wsClient.isConnected();
       }

       public void connectToWebsocketServer(ClientEventCallback clientEventCallback)
       {
              try
              {
                     if (ServerData.HOST == null)
                            throw new RuntimeException("You must set ServerData HOST first!");
                     if (wsClient == null || !wsClient.isConnected())
                            wsClient = new AndroidWebsocketClient(ServerData.HOST, ServerData.PORT_WS);
                     if (!wsClient.isConnected())
                     {
                            wsClient.connectToServer();
                            sendClientIdentifier();
                            if (wsClient.isConnected())
                                   clientEventCallback.onEvent("Connected to WebSocket server!");
                     }
              } catch (Exception e)
              {
                     clientEventCallback.onError("Error while connecting to server. Reason:" + e.getMessage());
              }
       }

       public void sendClientIdentifier()
       {
              int i = 0;
              final int MAXTRIES = 10;
              try
              {
                     while (!wsClient.isConnected() && i < MAXTRIES)
                     {
                            Thread.sleep(100);
                            i++;
                     }
              } catch (InterruptedException e)
              {
              }
              Task task = new ClientIdentifier(uniqueClientID);
              Message msg = new Message(Tasks.TASK_IDENTIFY_CLIENT, task);
              String json = communicator.getJSON(msg);
              wsClient.sendMessage(json);
       }

       public void disconnectFromWebsocketServer(ClientEventCallback clientEventCallback)
       {
              try
              {
                     wsClient.disconnectFromServer();
              } catch (Exception e)
              {
                     clientEventCallback.onError(e.getMessage());
              }
       }

       public void subscribeWebsocketEventHandler(WebsocketServerEventCallback eventHandler)
       {
              wsClient.subscribeWebsocketEventHandler(eventHandler);
       }

       public void unsubscribeWebsocketEventHandler()
       {
              wsClient.unsubscribeWebsocketEventHandler();
       }

       public void login(String username, String password, HTTPResponseEventCallback httpResponseEventCallback)
       {
              restClient.login(username, password, uniqueClientID, httpResponseEventCallback);
       }

       public void logout(HTTPResponseEventCallback httpResponseEventCallback)
       {
              restClient.logout(uniqueClientID, httpResponseEventCallback);
       }

       public void registration(final String username, final String password, final String email, final String firstname, final String lastname, final HTTPResponseEventCallback httpResponseEventCallback)
       {
              restClient.registration(username, password, email, firstname, lastname, uniqueClientID, httpResponseEventCallback);
       }

       public void sendLocationData(NewLocation location)
       {
              Message msg = new Message(Tasks.TASK_NEWLOC, location);
              String json = communicator.getJSON(msg);
              wsClient.sendMessage(json);
       }

}
