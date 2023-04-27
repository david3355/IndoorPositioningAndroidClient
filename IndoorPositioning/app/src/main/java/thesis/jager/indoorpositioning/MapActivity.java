package thesis.jager.indoorpositioning;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import jager.indoornav.wscconvention.Communicator;
import jager.indoornav.wscconvention.model.Message;
import jager.indoornav.wscconvention.model.NewLocation;
import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;
import thesis.jager.indoorpositioning.util.ActivityUtil;
import thesis.jager.indoorpositioning.view.positioning.MapView;

public class MapActivity extends AppCompatActivity implements ClientEventCallback, WebsocketServerEventCallback
{
       private MapView mapView;
       private Client client;
       private Communicator communicator;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_map);
              mapView = (MapView) findViewById(R.id.view_map);
              communicator = Communicator.getInstance();
              mapView.displayTags(false);
              //TEST:
              /*
              NewLocation nl1 = new NewLocation(250, 300, 123124121, "jager");
              NewLocation nl2 = new NewLocation(60, 264, 123124121, "test");
              NewLocation nl3 = new NewLocation(180, 406, 123124121, "gabe");
              NewLocation nl4 = new NewLocation(150, 264, 123124121, "peter");
              handleNewLocation(nl1);
              handleNewLocation(nl2);
              handleNewLocation(nl3);
              handleNewLocation(nl4);
              */
       }

       @Override
       protected void onResume()
       {
              super.onResume();
              client = ActivityUtil.createClientIfNotExists(client, this, this);
              if (client != null) client.subscribeWebsocketEventHandler(this);
       }

       @Override
       protected void onPause()
       {
              if(client!=null)
              {
                     client.unsubscribeWebsocketEventHandler();
              }
              super.onPause();
       }


       @Override
       public void onError(String message)
       {
              ActivityUtil.popup("Error: " + message, this);
       }

       @Override
       public void serverMessage(String message)
       {
              Message msg = communicator.getMessage(message);
              switch (msg.getTaskObject().getTask())
              {
                     case TASK_NEWLOC:
                            NewLocation nloc = (NewLocation) msg.getTaskObject();
                            handleNewLocation(nloc);
                            break;
              }
       }

       private void handleNewLocation(NewLocation newlocation)
       {
              mapView.handleNewLocationFromOtherDevice(newlocation);
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            mapView.invalidate();
                     }
              });
       }

       @Override
       public void onEvent(String message)
       {
              ActivityUtil.popup("Event: " + message, this);
       }
}
