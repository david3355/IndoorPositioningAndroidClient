package thesis.jager.indoorpositioning.view.positioning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import jager.indoornav.wscconvention.model.NewLocation;
import thesis.jager.indoorpositioning.R;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;

/**
 * Created by Jager on 2016.02.10..
 */
public class MapView extends View implements  DistanceChangeViewHandler
{
       private DrawManager drawManager;

       public MapView(Context context)
       {
              super(context);
              init(context);
       }

       public MapView(Context context, AttributeSet attrs)
       {
              super(context, attrs);
              init(context);
       }

       public MapView(Context context, AttributeSet attrs, int defStyleAttr)
       {
              super(context, attrs, defStyleAttr);
              init(context);
       }

       private void init(Context context)
       {
              drawManager = new DrawManager(context);
       }

       public void displayTags(boolean enabled)
       {
              drawManager.setDrawTags(enabled);
       }

       public void handleNewLocationFromOtherDevice(NewLocation location)
       {
              PointF pos = new PointF((float)location.getLatitude(), (float)location.getLongitude());
              if(drawManager.hasDevice(location.getUsername()))
              {
                     drawManager.changeOtherDeviceLocation(pos, location.getUsername());
              }
              else
              {
                     SimulatedDeviceDisplay otherDevice = new SimulatedDeviceDisplay(pos, SimulatedDeviceDisplay.OTHER_DEVICE_DISPLAY_COLOR, location.getUsername());
                     otherDevice.makeVisible(); // TEST
                     drawManager.addOtherDevice(otherDevice);
              }
       }

       @Override
       protected void onDraw(Canvas canvas)
       {
              canvas.drawColor(Color.WHITE);
              loadMap(canvas);
              drawManager.draw(canvas);
              super.onDraw(canvas);
}

       private void loadMap(Canvas canvas)
       {
              Drawable d = getResources().getDrawable(R.drawable.room);
              d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
              d.draw(canvas);
       }

       @Override
       public void distanceFromTagChanged(String mac, float distance)
       {
              drawManager.changeDistanceFromTag(mac, distance);
       }

       @Override
       public void simulatedLocationChanged(PointF location, Precision locationResultPrecision, String username)
       {
              drawManager.changeSimulatedLocation(location, locationResultPrecision, username);
       }
}
