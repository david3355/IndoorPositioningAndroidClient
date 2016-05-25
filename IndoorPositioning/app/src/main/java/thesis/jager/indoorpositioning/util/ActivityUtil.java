package thesis.jager.indoorpositioning.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;
import android.widget.Toast;

import thesis.jager.indoorpositioning.client.Client;
import thesis.jager.indoorpositioning.client.ServerData;
import thesis.jager.indoorpositioning.client.eventhandling.ClientEventCallback;

/**
 * Created by Jager on 2016.04.10..
 */
public class ActivityUtil
{
       public static void clearFields(EditText... fields)
       {
              for (EditText et : fields)
              {
                     clearField(et);
              }
       }

       public static void clearField(EditText edittext)
       {
              edittext.setText("");
       }

       public static void popup(final String text, final Activity context)
       {
              context.runOnUiThread(new Runnable()
              {
                     @Override
                     public void run()
                     {
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                     }
              });
       }

       public static String getStringRes(Context context, int id)
       {
              return context.getResources().getString(id);
       }

       public static Client createClientIfNotExists(Client client, ClientEventCallback clientEventCallback, Activity context)
       {
              Client c = null;
              try
              {
                     if (client == null)
                     {
                            c = Client.getInstance();
                     } else c = client;
                     if(!c.isConnected())
                     {
                            popup("Connecting to WebSocket server: " + ServerData.HOST, context);
                            c.connectToWebsocketServer(clientEventCallback);
                     }
                     return c;
              } catch (Exception e)
              {
                     clientEventCallback.onError("Failed to create client. Reason: " + e.getMessage());
                     return null;
              }
       }
}
