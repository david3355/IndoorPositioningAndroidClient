package thesis.jager.indoorpositioning.client.eventhandling;

/**
 * Created by Jager on 2016.04.27..
 */
public interface ClientEventCallback
{
       void onError(String message);
       void onEvent(String message);
}
