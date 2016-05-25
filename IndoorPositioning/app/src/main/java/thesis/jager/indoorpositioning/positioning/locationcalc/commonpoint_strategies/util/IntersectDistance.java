package thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.util;

import android.graphics.PointF;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.CalculatorUtility;

/**
 * Created by Jager on 2016.04.17..
 */
public class IntersectDistance
{
       public IntersectDistance(PointF P1, PointF P2)
       {
              this.P1 = P1;
              this.P2 = P2;
              this.Distance = CalculatorUtility.Distance(P1, P2);
       }

       public double Distance;
       public PointF P1;
       public PointF P2;

       @Override
       public String toString()
       {
              return String.format("D: {0} Point 1: [{1};{2}] Point 2: [{3};{4}]", Math.round(Distance), Math.round(P1.x), Math.round(P1.y), Math.round(P2.x), Math.round(P2.y));
       }

       @Override
       public boolean equals(Object obj)
       {
              IntersectDistance idist = (IntersectDistance) obj;
              return (this.P1.equals(idist.P1) && this.P2.equals(idist.P2)) || (this.P1.equals(idist.P2) && this.P2.equals(idist.P1));
       }

}
