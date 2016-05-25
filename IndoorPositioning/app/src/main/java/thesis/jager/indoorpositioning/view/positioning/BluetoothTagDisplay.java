package thesis.jager.indoorpositioning.view.positioning;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Jager on 2016.02.10..
 */
public class BluetoothTagDisplay extends BasicDisplay
{
       public BluetoothTagDisplay(PointF origo, String mac)
       {
              super(origo);
              this.mac = mac;
       }

       private String mac;
       private float distanceFromDevice;

       private static final int tagDisplayRadius = 10;
       private static final int tagDisplayColor = Color.rgb(0, 0, 255);
       private static final int deviceDistanceDisplayColor = Color.rgb(0, 255, 0);

       public String getMac()
       {
              return mac;
       }

       public PointF getOrigo()
       {
              return origo;
       }

       public void setOrigo(PointF origo)
       {
              this.origo = origo;
       }

       public float getDistanceFromDevice()
       {
              return distanceFromDevice;
       }

       public void setDistanceFromDevice(float distanceFromDevice)
       {
              this.distanceFromDevice = distanceFromDevice;
       }

       @Override
       public void drawItself(Canvas canvas)
       {
              Paint tagDisplayPaint = new Paint();
              tagDisplayPaint.setStyle(Paint.Style.FILL);
              tagDisplayPaint.setColor(tagDisplayColor);
              canvas.drawCircle(getScaledData(origo.x, canvas), getScaledData(origo.y, canvas), tagDisplayRadius, tagDisplayPaint);

              Paint deviceDistanceDisplayPaint = new Paint();
              deviceDistanceDisplayPaint.setStyle(Paint.Style.STROKE);
              deviceDistanceDisplayPaint.setStrokeWidth(3);
              deviceDistanceDisplayPaint.setColor(deviceDistanceDisplayColor);
              canvas.drawCircle(getScaledData(origo.x, canvas), getScaledData(origo.y, canvas), getScaledData(distanceFromDevice, canvas), deviceDistanceDisplayPaint);
       }


}
