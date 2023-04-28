package thesis.jager.indoorpositioning.positioning;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import thesis.jager.indoorpositioning.discovery.Beacon;
import thesis.jager.indoorpositioning.discovery.DiscoveredBTDevice;
import thesis.jager.indoorpositioning.discovery.communicator.MessageEventHandler;
import thesis.jager.indoorpositioning.positioning.distancecalc.DistanceCalculator;
import thesis.jager.indoorpositioning.positioning.distancecalc.PolyRegressionModelDistanceCalculator;
import thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies.CalculatorStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.LocationResult;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.TagDistanceRepresentation;
import thesis.jager.indoorpositioning.positioning.tagmanager.TagPositionManager;
import thesis.jager.indoorpositioning.util.ActivityUtil;
import thesis.jager.indoorpositioning.view.positioning.BluetoothTagDisplay;

/**
 * Created by Jager on 2016.04.16..
 */
public class Positioning
{
       public Positioning(Context context, CalculatorStrategy calculatorStrategy, MessageEventHandler messageEventHandler)
       {
              this.context = context;
              this.devices = new ArrayList<TagDistanceRepresentation>();
              this.tagPositionManager = TagPositionManager.getInstance(context);
              this.calc_strat = calculatorStrategy;
              this.messageEventHandler = messageEventHandler;
              this.lastLocation = new LocationResult(new PointF(-1, -1), Precision.NoTag);
       }

       private Context context;
       private List<TagDistanceRepresentation> devices;
       private TagPositionManager tagPositionManager;
       private CalculatorStrategy calc_strat;
       private final DistanceCalculator distanceCalculator = PolyRegressionModelDistanceCalculator.getInstance();
       private LocationResult lastLocation;
       private MessageEventHandler messageEventHandler;

       public DistanceCalculator getDistanceCalculator()
       {
              return distanceCalculator;
       }

       /**
        * Ez a metódus hívódik minden alkalommal, amikor a felderítő jelet vesz egy Tag-től
        * Ha az eszköz még nem szerepel a listában, azaz most derítettük fel először, akkor hozzáadjuk, egyébként frissítjük az újonnan vett RSSI értékkel
        * Aztán kiszámoljuk a pozíciót a listában szereplő távolságok alapján
        *
        * @param lastDiscoveredDevice Az a Bluetooth Tag, amitől jelet vett a felderítő szolgáltatás
        * @return Az új tagtől vett adatok és az eddigi adatok alapján közelítőleg visszaadja a felhasználó pozícióját
        */
       public LocationResult getNewPosition(DiscoveredBTDevice lastDiscoveredDevice)
       {
              messageEventHandler.infoMessage("new device");
              TagDistanceRepresentation deviceFound = containsDevice(lastDiscoveredDevice.getMAC());
              if (deviceFound == null)
              {
                     addDevice(lastDiscoveredDevice);
                     //Toast.makeText(context, String.format("new device: %s", lastDiscoveredDevice.getMAC()), Toast.LENGTH_SHORT).show();
                     Log.d("Positioning", String.format("New Device added to list: %s | RSSI: %s", lastDiscoveredDevice.getMAC(), lastDiscoveredDevice.getRssi()));
              } else
              {
                     refreshDevice(deviceFound, lastDiscoveredDevice.getRssi());
                     messageEventHandler.infoMessage(String.format("rssi: %s", lastDiscoveredDevice.getRssi()));
                     //Toast.makeText(context, String.format("rssi: %s", lastDiscoveredDevice.getRssi()), Toast.LENGTH_SHORT).show();
                     Log.d("Positioning", String.format("Device refreshed: %s | RSSI: %s", lastDiscoveredDevice.getMAC(), lastDiscoveredDevice.getRssi()));
              }
              return calculatePosition();
       }

       /**
        * Megnézi, hogy az eszközlista tartalmaz-e a megadott MAC című eszközt. Ha igen, visszaadja a szóban forgó eszközt, egyébként null-t.
        * @param mac A keresni kívánt eszköz MAC címe
        * @return Az eszköz objektum, ha létezik a listában, egyébként null
        */
       private TagDistanceRepresentation containsDevice(String mac)
       {
              for(TagDistanceRepresentation tdr : devices)
              {
                     if(tdr.getMac().equals(mac)) return tdr;
              }
              return null;
       }

       /**
        * Új eszközt talált a felderítő, amely MAC címe még nem szerepel a listávan, így új elemet adunk hozzá
        */
       private void addDevice(DiscoveredBTDevice device)
       {
              try
              {
                     PointF pos = getPositionForDevice(device.getMAC());
                     messageEventHandler.infoMessage(String.format("Device: %s", device.getMAC()));
                     TagDistanceRepresentation tdr = new TagDistanceRepresentation(device.getMAC(), pos, distanceCalculator);
                     devices.add(tdr);
              }
              catch(Resources.NotFoundException e)
              {
                     messageEventHandler.errorMessage(e.getMessage());
              }
       }

       /**
        * Amikor új, eddig ismeretlen eszközt adunk a listához, le kell kérnünk az eszközhöz tartozó előre definiált pozíciót a tag adatbázisból
        *
        * @param macAddress Az új eszköz MAC címe
        * @return Az eszközhöz tartozó előre meghatározott pozíció
        */
       private PointF getPositionForDevice(String macAddress)
       {
              if (macAddress.equals("E0:FF:F1:11:8F:41")) return new PointF(0, 255);
              if (macAddress.equals("C4:BE:84:49:FA:B3")) return new PointF(270, 35);
              if (macAddress.equals("C4:BE:84:49:F8:B9")) return new PointF(515, 255);
              return new PointF(0,0);
              //return tagPositionManager.getTagPosition(macAddress);
       }

       /**
        * A felderítő olyan eszköztől vett jelet, amelyet korábban már eltároltunk, így kikeressük a listából ezt az eszközt, és frissítjük az új RSSI értékkel
        */
       private void refreshDevice(TagDistanceRepresentation device, int rssi)
       {
              Beacon b = new Beacon(rssi, DateTime.now());
              device.addBeacon(b);
       }

       /**
        * Az eltárolt eszközök adatai (távolságai) alapján kiszámoljuk a felhasználó közelítő pozícióját, a megfelelő calculator strategy segítségével
        *
        * @return A meghatározott pozícióobjektum, mely tartalmazza a pozíción kívül a pontosságot is.
        */
       private LocationResult calculatePosition()
       {
              try
              {
                     lastLocation = calc_strat.CalculateLocation(devices, lastLocation);
                     Toast.makeText(context, String.format("pos: %s", lastLocation.simulatedLocation.toString()), Toast.LENGTH_SHORT).show();
              }
              catch(Exception e)
              {
                     Toast.makeText(context, String.format("error: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
              }
              return lastLocation;
       }


}

