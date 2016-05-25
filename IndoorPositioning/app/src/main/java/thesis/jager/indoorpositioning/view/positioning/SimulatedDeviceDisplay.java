package thesis.jager.indoorpositioning.view.positioning;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Jager on 2016.02.10..
 */
public class SimulatedDeviceDisplay extends BasicDisplay
{
       public SimulatedDeviceDisplay(PointF origo, int displayColor, String identifier)
       {
              super(origo);
              this.deviceDisplayColor = displayColor;
              this.identifier = identifier;
              visible = false;
       }

       public SimulatedDeviceDisplay()
       {
              super();
              deviceDisplayColor = OWN_DEVICE_DISPLAY_COLOR;
              visible = false;
       }

       private static final int displayRadius = 10;
       private int deviceDisplayColor;
       private boolean visible;

       public final static int OWN_DEVICE_DISPLAY_COLOR = Color.rgb(255, 0, 0);
       public final static int OTHER_DEVICE_DISPLAY_COLOR = Color.rgb(156, 0, 156);

       public String getIdentifier()
       {
              return identifier;
       }

       public void setIdentifier(String identifier)
       {
              this.identifier = identifier;
       }

       private String identifier;

       @Override
       public void drawItself(Canvas canvas)
       {
              if (origo == null || !visible) return;
              Paint deviceDisplayPaint = new Paint();
              Paint identifierPaint = new Paint();
              deviceDisplayPaint.setStyle(Paint.Style.FILL);
              deviceDisplayPaint.setColor(deviceDisplayColor);
              identifierPaint.setColor(deviceDisplayColor);
              identifierPaint.setTextSize(30);
              identifierPaint.setFakeBoldText(true);
              canvas.drawCircle(getScaledData(origo.x, canvas), getScaledData(origo.y, canvas), displayRadius, deviceDisplayPaint);
              if (identifier != null && !identifier.equals(""))
                     canvas.drawText(identifier, getScaledData(origo.x, canvas) + 20, getScaledData(origo.y, canvas) + 10, identifierPaint);
       }

       public void makeVisible()
       {
              visible = true;
       }

       public void makeInvisible()
       {
              visible = false;
       }


}
