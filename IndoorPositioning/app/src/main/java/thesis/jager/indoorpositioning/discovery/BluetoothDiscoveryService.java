package thesis.jager.indoorpositioning.discovery;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import thesis.jager.indoorpositioning.bluetooth.BluetoothLowEnergyHelper;
import thesis.jager.indoorpositioning.bluetooth.DiscoveryBluetoothHelper;
import thesis.jager.indoorpositioning.discovery.communicator.InterComponentCommunicator;
import thesis.jager.indoorpositioning.discovery.communicator.InterComponentData;
import thesis.jager.indoorpositioning.discovery.communicator.MessageEventHandler;
import thesis.jager.indoorpositioning.positioning.Positioning;
import thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies.CalculatorStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies.AverageClosestDistanceLocationCalculator;
import thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.ClosestPointsStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.CommonPointStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.LocationResult;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;

/**
 * Created by Jager on 2016.04.10..
 */
public class BluetoothDiscoveryService extends Service implements BluetoothEventHandler, DeviceFoundEventHandler, MessageEventHandler
{
       public BluetoothDiscoveryService()
       {
              super();
              discoveryRunning = false;
       }

       private boolean discoveryRunning;
       private int SCANNING_PERIOD = 3000;
       private ResultReceiver replier;
       private Positioning positioning;

       public final static String KEY_RESULTRECEIVER = "pos_svc_resultreceiver";
       public final static int LOCATION_RESULTCODE = 100;
       public final static int MESSAGE_RESULTCODE = 101;

       /**
        * A btdeviceMgr kezeli a Bluetooth Low Energy műveleteket, többek közt az eszközök szkennelését.
        */
       private BluetoothLowEnergyHelper btdeviceMgr;

       @Override
       public int onStartCommand(Intent intent, int flags, int startId)
       {
              try
              {
                     btdeviceMgr = new DiscoveryBluetoothHelper(this);
                     CommonPointStrategy commonPointStrategy = new ClosestPointsStrategy();
                     CalculatorStrategy calculatorStrategy = new AverageClosestDistanceLocationCalculator(commonPointStrategy);
                     positioning = new Positioning(getApplicationContext(), calculatorStrategy, this);
                     Log.d("BluetoothDiscoverySVC", "Getting replier object...");
                     replier = intent.getParcelableExtra(KEY_RESULTRECEIVER);
                     Log.d("BluetoothDiscoverySVC", "Replier object aquired...");
                     startDiscovery();
              } catch (IllegalAccessException e)
              {
                     errorMessage(e.getMessage());
              }
              return super.onStartCommand(intent, flags, startId);
       }


       private void startDiscovery()
       {
              // TODO: hibakezelés, ha pl a felhasználó kinyomja a bluetooth-t közben: service leáll
              Log.d("BluetoothDiscoverySVC", "Discovering started!");
              discoveryRunning = true;
              btdeviceMgr.scanLeDevice(true, this, SCANNING_PERIOD);
       }

       private void stopDiscovery()
       {
              discoveryRunning = false;
              if (btdeviceMgr != null) btdeviceMgr.stopScanning(this);
              Log.d("BluetoothDiscoverySVC", "Discovery stopped!");
       }

       /**
        * Feldolgozzuk azon eszköz adatait, amelytől jel érkezett a felderítés során.
        * Az adatokból összeállítunk egy InterComponentData objektumot, ebből az InterComponentCommunicator Bundle objektumot készít,
        * amit a replier ResultReceiver objektum által visszaküldünk az Activitynek
        *
        * @param device
        */
       public void processDiscoveredDevice(DiscoveredBTDevice device)
       {
              InterComponentData devicedata = new InterComponentData();
              devicedata.rssi = device.getRssi();
              devicedata.estimatedDistance = positioning.getDistanceCalculator().getDistanceByRSSI(device.getRssi());
              Log.d("BluetoothDiscoverySVC", "Calculating location. New device: " + device.getMAC());
              LocationResult loc = positioning.getNewPosition(device);
              Log.d("BluetoothDiscoverySVC", "Location calculated: " + String.format("%s, %s", loc.simulatedLocation.x, loc.simulatedLocation.y));
              PointF pos = loc.simulatedLocation;

              devicedata.cx = pos.x;
              devicedata.cy = pos.y;
              devicedata.precision = loc.precision;
              devicedata.radius = loc.radius;
              devicedata.mac = device.getMAC();

              Bundle bundle = InterComponentCommunicator.getBundle(devicedata);
              replier.send(LOCATION_RESULTCODE, bundle);         // Send back to activity
              Log.d("BluetoothDiscoverySVC", "Reply sent to acitivity!");
       }

       @Override
       public void deviceFound(DiscoveredBTDevice device)
       {
              processDiscoveredDevice(device);
       }

       @Override
       public void scanningStarted()
       {

       }

       /**
        * A keresés periodikus, a SCANNING_PERIOD konstans által meghatározott ideig tart.
        * Ahogy egy periódus lefutott, rögtön indul a következő.
        */
       @Override
       public void scanningEnded()
       {
              Log.d("BluetoothDiscoverySVC", "Scanning ended...Start an other scanning...");
              if (discoveryRunning) btdeviceMgr.scanLeDevice(true, this, SCANNING_PERIOD);
              Log.d("BluetoothDiscoverySVC", "Scanning started...");
       }

       @Override
       public void onDestroy()
       {
              stopDiscovery();
              super.onDestroy();
       }

       @Override
       public IBinder onBind(Intent intent)
       {
              return null;
       }

       @Override
       public void infoMessage(String text)
       {
              Bundle data = new Bundle();
              data.putString(InterComponentData.KEY_MESSAGE, text);
              replier.send(MESSAGE_RESULTCODE, data);
       }

       @Override
       public void errorMessage(String text)
       {
              Bundle data = new Bundle();
              data.putString(InterComponentData.KEY_MESSAGE, text);
              replier.send(MESSAGE_RESULTCODE, data);
       }
}

