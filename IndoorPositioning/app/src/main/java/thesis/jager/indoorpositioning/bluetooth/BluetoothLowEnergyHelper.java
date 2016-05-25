package thesis.jager.indoorpositioning.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import thesis.jager.indoorpositioning.discovery.BluetoothEventHandler;

/**
 * Created by Jager on 2016.04.15..
 */
public abstract class BluetoothLowEnergyHelper
{
       public BluetoothLowEnergyHelper() throws  IllegalAccessException
       {
              mHandler = new Handler();
              btHelper = BluetoothHelper.getInstance();
       }

       protected Context context;
       private boolean mScanning;
       private Handler mHandler;
       private BluetoothHelper btHelper;

       public boolean isBluetoothEnabled()
       {
              return btHelper.bluetoothAdapter.isEnabled();
       }

       public void scanLeDevice(final boolean enable, final BluetoothEventHandler scanevent,  int scan_period)
       {
              if (enable)
              {
                     // a scan_period által megadott idő után lefuttatja a scan leállítását.
                     mHandler.postDelayed(new Runnable()
                     {
                            @Override
                            public void run()
                            {
                                   if(mScanning)
                                   {
                                          stopScanning(scanevent);
                                   }
                            }
                     }, scan_period);

                     startScanning(scanevent);
              } else
              {
                     stopScanning(scanevent);
              }

       }

       public boolean isScanning()
       {
              return mScanning;
       }

       protected void startScanning(BluetoothEventHandler scanevent)
       {
              btHelper.bluetoothAdapter.startLeScan(mLeScanCallback);
              mScanning = true;
              if(scanevent != null) scanevent.scanningStarted();
       }

       public void stopScanning(BluetoothEventHandler scanevent)
       {
              Log.d(this.getClass().toString(), "------------------- SCANNING STOPPED ------------------");
              btHelper.bluetoothAdapter.stopLeScan(mLeScanCallback);
              mScanning = false;
              if(scanevent != null) scanevent.scanningEnded();
       }

       private BluetoothAdapter.LeScanCallback mLeScanCallback =
               new BluetoothAdapter.LeScanCallback()
               {
                      @Override
                      public void onLeScan(final BluetoothDevice device, final int rssi,
                                           byte[] scanRecord)
                      {
                             onDeviceFound(device, rssi, scanRecord);
                      }
               };

       protected abstract void onDeviceFound(final BluetoothDevice device, final int rssi,
                                             byte[] scanRecord);


       protected BluetoothGatt connectToDevice(BluetoothDevice device, BluetoothGattCallback gattCallback)
       {
              return device.connectGatt(context, false, gattCallback);       // to autoconnect, set 2nd parameter to true
       }

}
