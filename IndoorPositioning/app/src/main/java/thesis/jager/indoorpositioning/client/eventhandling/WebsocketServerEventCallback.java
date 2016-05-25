package thesis.jager.indoorpositioning.client.eventhandling;

/**
 * Created by Jager on 2016.03.14..
 */
public interface WebsocketServerEventCallback
{
       void serverMessage(String message);
       void onEvent(String eventMessage);
}
