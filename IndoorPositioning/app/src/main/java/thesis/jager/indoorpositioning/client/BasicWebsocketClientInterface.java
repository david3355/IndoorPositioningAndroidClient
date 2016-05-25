package thesis.jager.indoorpositioning.client;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;

/**
 * Created by Jager on 2016.03.24..
 */
public interface BasicWebsocketClientInterface
{
       void connectToServer() throws URISyntaxException, IOException, DeploymentException;
       void disconnectFromServer();
       void sendMessage(String message);
       void subscribeWebsocketEventHandler(WebsocketServerEventCallback eventHandler);
       void unsubscribeWebsocketEventHandler();
       boolean isConnected();
       int getRemotePort();
       String getRemoteAddress();
}
