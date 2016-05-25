package thesis.jager.indoorpositioning.view.positioning;

import android.graphics.Canvas;
import android.graphics.PointF;

/**
 * Created by Jager on 2016.02.11..
 */
public abstract class BasicDisplay
{
       public BasicDisplay(PointF origo)
       {
              this.origo = origo;
       }

       public BasicDisplay()
       {
              this.origo = null;
       }

       protected PointF origo;

       public PointF getOrigo()
       {
              return origo;
       }

       public void setOrigo(PointF origo)
       {
              this.origo = origo;
       }



       public abstract void drawItself(Canvas canvas);

       protected float getScaledData(float data, Canvas canvas)
       {
              int width = canvas.getWidth();
              int height = canvas.getHeight();
              int realWidth = 380;
              float scale = (float) width / realWidth;
              return data * scale;
       }
}
