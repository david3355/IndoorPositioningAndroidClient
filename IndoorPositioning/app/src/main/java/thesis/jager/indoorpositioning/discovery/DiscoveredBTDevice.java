package thesis.jager.indoorpositioning.discovery;

import android.bluetooth.BluetoothDevice;
import android.graphics.PointF;

import thesis.jager.indoorpositioning.positioning.distancecalc.DistanceCalculator;
import thesis.jager.indoorpositioning.positioning.distancecalc.PolyRegressionModelDistanceCalculator;

public class DiscoveredBTDevice
{
       public DiscoveredBTDevice(BluetoothDevice device, int rssi)
       {
              this.device = device;
              this.rssi = rssi;
       }

       private BluetoothDevice device;
       private int rssi;

       public int getRssi()
       {
              return rssi;
       }

       public BluetoothDevice getDevice()
       {
              return device;
       }

       public String getMAC()
       {
              return device.getAddress();
       }

       @Override
       public boolean equals(Object o)
       {
              DiscoveredBTDevice device = (DiscoveredBTDevice) o;
              return this.getMAC().equals(device.getMAC());
       }
}
