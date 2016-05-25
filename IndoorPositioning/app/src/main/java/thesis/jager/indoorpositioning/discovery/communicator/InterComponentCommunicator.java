package thesis.jager.indoorpositioning.discovery.communicator;

import android.os.Bundle;

import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;

/**
 * Created by Jager on 2016.04.15..
 * Ez az osztály oldja meg a szabványos kommunikációt a PositioningActivity és a BluetoothDiscoveryService között.
 */
public class InterComponentCommunicator
{
       /**
        * A megfelelő adatokból összeállított InterComponentData objektumból elkészíti a Bundle objektumot, amelyet az egyik komponens küld a másiknak.
        */
       public static Bundle getBundle(InterComponentData data)
       {
              Bundle bundle = new Bundle();
              bundle.putDouble(InterComponentData.KEY_CX, data.cx);
              bundle.putDouble(InterComponentData.KEY_CY, data.cy);
              bundle.putDouble(InterComponentData.KEY_RADIUS, data.radius);
              bundle.putInt(InterComponentData.KEY_PRECISION, data.precision.num());
              bundle.putString(InterComponentData.KEY_MAC, data.mac);
              bundle.putDouble(InterComponentData.KEY_ESTIMATED_DISTANCE, data.estimatedDistance);
              bundle.putInt(InterComponentData.KEY_RSSI, data.rssi);
              return bundle;
       }

       /**
        *   A Bundle objektumtól lekéri a megfelelő adatokat, és elkészíti belőle az InterComponentData objektumot.
        */
       public static InterComponentData readBundle(Bundle data)
       {
              InterComponentData icd = new InterComponentData();
              icd.cx = data.getDouble(InterComponentData.KEY_CX);
              icd.cy = data.getDouble(InterComponentData.KEY_CY);
              icd.radius = data.getDouble(InterComponentData.KEY_RADIUS);
              icd.precision = Precision.getEnum(data.getInt(InterComponentData.KEY_PRECISION));
              icd.mac = data.getString(InterComponentData.KEY_MAC);
              icd.estimatedDistance = data.getDouble(InterComponentData.KEY_ESTIMATED_DISTANCE);
              icd.rssi = data.getInt(InterComponentData.KEY_RSSI);
              return icd;
       }
}
