package thesis.jager.indoorpositioning;

import android.content.Intent;
import android.graphics.PointF;
import android.os.ResultReceiver;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import org.joda.time.DateTime;

import jager.indoornav.wscconvention.model.NewLocation;
import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;
import thesis.jager.indoorpositioning.client.eventhandling.WebsocketServerEventCallback;
import thesis.jager.indoorpositioning.discovery.BluetoothDiscoveryService;
import thesis.jager.indoorpositioning.discovery.communicator.InterComponentCommunicator;
import thesis.jager.indoorpositioning.discovery.communicator.InterComponentData;
import thesis.jager.indoorpositioning.util.ActivityUtil;
import thesis.jager.indoorpositioning.view.positioning.MapView;
import thesis.jager.indoorpositioning.view.positioning.UIOperationHandler;

public class PositioningActivity extends AppCompatActivity implements ClientEventCallback, WebsocketServerEventCallback
{
       private ResultReceiver resultReceiver;
       private MapView mapView;
       private Client client;

       @Override
       protected void onCreate(Bundle savedInstanceState)
       {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.activity_positioning);
              mapView = (MapView) findViewById(R.id.view_map);
              setResultReceiver();
       }

       // TODO: Egyelőre a helymeghatározás leáll, amikor az activity pause-ol, azonban fel lehene kínálni a lehetőséget,  hogy a háttérben fusson a service, hogy amikor újra bekapcsolja, rögtön legyen pozíció
       @Override
       protected void onPause()
       {
              if (client != null)
              {
                     client.unsubscribeWebsocketEventHandler();
              }
              super.onPause();
              stopPositioning();
       }

       @Override
       protected void onResume()
       {
              super.onResume();
              client = ActivityUtil.createClientIfNotExists(client, this, this);
              if (client != null) client.subscribeWebsocketEventHandler(this);
              startPositioning();
       }


       private void startPositioning()
       {
              Intent svci = new Intent(this, BluetoothDiscoveryService.class);
              svci.putExtra(BluetoothDiscoveryService.KEY_RESULTRECEIVER, resultReceiver);
              startService(svci);
              ActivityUtil.popup("Positioning service started!", this);
       }

       private void stopPositioning()
       {
              Intent svci = new Intent(this, BluetoothDiscoveryService.class);
              stopService(svci);
              ActivityUtil.popup("Positioning service stopped!", this);
       }

       private void setResultReceiver()
       {
              resultReceiver = new ResultReceiver(null)
              {
                     @Override
                     protected void onReceiveResult(int resultCode, Bundle resultData)
                     {
                            ActivityUtil.popup("Result from bluetooth", PositioningActivity.this);
                            switch (resultCode)
                            {
                                   case BluetoothDiscoveryService.LOCATION_RESULTCODE:
                                          processReceivedLocation(resultData);
                                          break;
                                   case BluetoothDiscoveryService.MESSAGE_RESULTCODE:
                                          processMessage(resultData);
                                          break;
                            }

                     }
              };
       }

       private void processReceivedLocation(Bundle dataFromSvc)
       {
              final InterComponentData data = InterComponentCommunicator.readBundle(dataFromSvc);
              ActivityUtil.popup(String.format("received: %s, %s, %s", data.rssi, data.estimatedDistance, data.precision), this);
              //if (client != null && client.isLoggedIn())
              //       client.sendLocationData(new NewLocation(data.cx, data.cx, DateTime.now().getMillis(), client.getUsername()));
              UIOperationHandler todo = new UIOperationHandler()
              {
                     @Override
                     public void operation()
                     {
                            mapView.distanceFromTagChanged(data.mac, (float) data.estimatedDistance);
                            mapView.simulatedLocationChanged(new PointF((float) data.cx, (float) data.cy), data.precision, client.getUsername());
                            mapView.invalidate();
                     }
              };
              changeUI(todo);
       }

       private void changeUI(final UIOperationHandler operationcallback)
       {
              runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            operationcallback.operation();
                     }
              });
       }

       private void processMessage(Bundle dataFromSvc)
       {
              String msg = dataFromSvc.getString(InterComponentData.KEY_MESSAGE);
              ActivityUtil.popup(msg, this);
       }

       @Override
       public void onError(String message)
       {
              ActivityUtil.popup("Error: " + message, this);
       }

       @Override
       public void serverMessage(String message)
       {
              ActivityUtil.popup("From server: " + message, this);
       }

       @Override
       public void onEvent(String message)
       {
              ActivityUtil.popup("Event: " + message, this);
       }
}
