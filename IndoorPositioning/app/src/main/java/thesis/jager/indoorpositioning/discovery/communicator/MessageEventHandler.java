package thesis.jager.indoorpositioning.discovery.communicator;

/**
 * Created by Jager on 2016.04.18..
 */
public interface MessageEventHandler
{
       void infoMessage(String text);
       void errorMessage(String text);
}
