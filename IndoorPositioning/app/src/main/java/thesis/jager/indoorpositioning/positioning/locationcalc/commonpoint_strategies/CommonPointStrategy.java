package thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies;

import android.graphics.PointF;
import java.util.Iterator;
import java.util.List;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Intersection;

/**
 * Created by Jager on 2016.02.09..
 */
public abstract class CommonPointStrategy
{
       /**
        * A paraméterként megadott metszéspontok közös pontjait számolja ki, vagy annak közelítő értékét
        * @param intersections    Metszetek listája
        * @return Azzal a ponttal tér vissza, amely a metszetek közös pontja, vagy annak közelítője
        */
       public PointF CommonPointOfIntersections(List<Intersection> intersections) throws IllegalArgumentException
       {
              if (intersections.size() < 2) throw new IllegalArgumentException("Argument list must contain 2 or more intersections!");
              for(Intersection isection : intersections)
              {
                     if (isection.Points().size() != 2) throw new IllegalArgumentException("Every valid intersection must contain 2 points!");
              }

              return CalculateCommonPoint(intersections);
       }

       public abstract PointF CalculateCommonPoint(List<Intersection> intersections);

}
