package thesis.jager.indoorpositioning.positioning.locationcalc.util;

import android.graphics.PointF;
import java.util.List;

/**
 * Created by Jager on 2016.04.16..
 */
public class CalculatorUtility
{
       public static double Distance(PointF P0, PointF P1)
       {
              return Math.sqrt(Math.pow(P0.x - P1.x, 2) + Math.pow(P0.y - P1.y, 2));
       }

       public static PointF Midpoint(PointF P0, PointF P1)
       {
              return new PointF((P0.x + P1.x) / 2, (P0.y + P1.y) / 2);
       }

       public static PointF PointAverage(List<PointF> Points)
       {
              float sx = 0;
              float sy = 0;
              int n = Points.size();
              for (PointF p : Points)
              {
                     sx += p.x;
                     sy += p.y;
              }
              return new PointF(sx / n, sy / n);
       }
}

