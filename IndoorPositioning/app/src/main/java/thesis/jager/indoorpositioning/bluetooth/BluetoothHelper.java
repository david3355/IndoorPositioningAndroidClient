package thesis.jager.indoorpositioning.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Jager on 2016.04.11..
 */
public class BluetoothHelper
{
       private BluetoothHelper(Context context)
       {
              this.context = context;
              bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
              bluetoothAdapter = bluetoothManager.getAdapter();
       }

       private static BluetoothHelper self;

       public static BluetoothHelper getInstance(Activity context)
       {
              if (self == null) self = new BluetoothHelper(context);
              return self;
       }

       public static BluetoothHelper getInstance() throws IllegalAccessException
       {
              if(self==null) throw new IllegalAccessException("getInstance(Context) must be called in some Activity before calling getInstance() method!");
              return self;
       }

       public Context context;
       public final BluetoothManager bluetoothManager;
       public final BluetoothAdapter bluetoothAdapter;

       public boolean isBluetoothEnabled()
       {
              if (bluetoothAdapter != null)
                     return bluetoothAdapter.isEnabled();
              return false;
       }

       public boolean deviceSupportsBluetoothLE()
       {
              return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
       }

       public void enableBluetooth()
       {
              if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
              {
                     Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                     int REQUEST_ENABLE_BT = 0;
                     Activity a_context = (Activity) context;
                     a_context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
              }
       }


}
