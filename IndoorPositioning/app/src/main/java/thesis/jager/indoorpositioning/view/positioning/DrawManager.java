package thesis.jager.indoorpositioning.view.positioning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;
import thesis.jager.indoorpositioning.positioning.tagmanager.PositionDatabaseDef;
import thesis.jager.indoorpositioning.positioning.tagmanager.TagPositionManager;

/**
 * Created by Jager on 2016.02.10..
 */
public class DrawManager
{
       public DrawManager(Context context)
       {
              tagPositionManager = TagPositionManager.getInstance(context);
              tagDisplays = new ArrayList<>();
              simDevice = new SimulatedDeviceDisplay();
              otherDevices = new ArrayList<>();
              setAllBluetoothTags();
              drawTags = true;
       }

       private TagPositionManager tagPositionManager;
       private List<BluetoothTagDisplay> tagDisplays;
       private SimulatedDeviceDisplay simDevice;
       private List<SimulatedDeviceDisplay> otherDevices;
       private boolean drawTags;

       public void setDrawTags(boolean enabled)
       {
              this.drawTags = enabled;
       }

       private void setAllBluetoothTags()
       {
              List<PositionDatabaseDef> positions = tagPositionManager.getAllTagPosition();
              for (PositionDatabaseDef pos : positions)
              {
                     BluetoothTagDisplay btDisplay = new BluetoothTagDisplay(new PointF((float) pos.getValue_cx(), (float) pos.getValue_cy()), pos.getMac());
                     tagDisplays.add(btDisplay);
              }
       }

       public void draw(Canvas canvas)
       {
              if(drawTags) drawTagdisplays(canvas);
              drawSimulatedDevice(canvas);
              drawOtherDevices(canvas);
       }

       private void drawTagdisplays(Canvas canvas)
       {
              for (BluetoothTagDisplay btDisplay : tagDisplays)
              {
                     btDisplay.drawItself(canvas);
              }
       }

       private void drawSimulatedDevice(Canvas canvas)
       {
              simDevice.drawItself(canvas);
       }

       private void drawOtherDevices(Canvas canvas)
       {
              for (SimulatedDeviceDisplay device : otherDevices)
              {
                     device.drawItself(canvas);
              }
       }

       public void changeDistanceFromTag(String mac, float distance)
       {
              for (BluetoothTagDisplay btDisplay : tagDisplays)
              {
                     if (btDisplay.getMac().equals(mac))
                     {
                            btDisplay.setDistanceFromDevice(distance);
                            break;
                     }
              }
       }

       public void changeSimulatedLocation(PointF location, Precision locationResultPrecision , String username)
       {
              simDevice.setIdentifier(username);
              if (locationResultPrecision == Precision.NoTag)
              {
                     simDevice.makeInvisible();
              } else
              {
                     simDevice.makeVisible();
                     simDevice.setOrigo(location);
              }
       }

       public void changeOtherDeviceLocation(PointF location, String identifier)
       {
              SimulatedDeviceDisplay device = getDevice(identifier);
              if (device != null) device.setOrigo(location);
       }

       public boolean hasDevice(String identifier)
       {
              return getDevice(identifier) != null;
       }

       public void addOtherDevice(SimulatedDeviceDisplay device)
       {
              otherDevices.add(device);
       }

       private SimulatedDeviceDisplay getDevice(String identifier)
       {
              for (SimulatedDeviceDisplay device : otherDevices)
              {
                     if (device.getIdentifier().equals(identifier))
                     {
                            return device;
                     }
              }
              return null;
       }
}
