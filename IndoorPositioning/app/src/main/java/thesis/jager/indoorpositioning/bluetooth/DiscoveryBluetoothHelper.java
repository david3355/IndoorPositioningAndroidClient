package thesis.jager.indoorpositioning.bluetooth;

import android.bluetooth.BluetoothDevice;

import thesis.jager.indoorpositioning.discovery.DeviceFoundEventHandler;
import thesis.jager.indoorpositioning.discovery.DiscoveredBTDevice;

/**
 * Created by Jager on 2016.04.18..
 */
public class DiscoveryBluetoothHelper extends BluetoothLowEnergyHelper
{
       public DiscoveryBluetoothHelper(DeviceFoundEventHandler deviceFoundEventHandler) throws IllegalAccessException
       {
              super();
              this.deviceFoundEventHandler = deviceFoundEventHandler;
       }

       private DeviceFoundEventHandler deviceFoundEventHandler;

       @Override
       protected void onDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord)
       {
              DiscoveredBTDevice disconveredDevice = new DiscoveredBTDevice(device, rssi);
              deviceFoundEventHandler.deviceFound(disconveredDevice);
       }
}
