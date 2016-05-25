package thesis.jager.indoorpositioning.discovery.communicator;

import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;

/**
 * Created by Jager on 2016.04.15..
 * Ez az objektum szabványos felületet ad a PositioningActivity és a BluetoothDiscoveryService között átmenő adatoknak
 */
public class InterComponentData
{
       public double radius;
       public Precision precision;
       public double cx;
       public double cy;
       public String mac;
       public double estimatedDistance;
       public int rssi;

       public static final String KEY_CX = "coordx";
       public static final String KEY_CY = "coordy";
       public static final String KEY_RADIUS = "radius";
       public static final String KEY_PRECISION = "prec";
       public static final String KEY_MAC = "mac";
       public static final String KEY_ESTIMATED_DISTANCE = "est_distance";
       public static final String KEY_RSSI = "rssi";

       public static final String KEY_MESSAGE = "message";
}
