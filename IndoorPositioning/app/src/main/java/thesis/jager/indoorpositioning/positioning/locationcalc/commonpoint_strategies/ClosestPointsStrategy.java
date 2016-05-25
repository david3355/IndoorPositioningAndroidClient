package thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import thesis.jager.indoorpositioning.positioning.locationcalc.util.CalculatorUtility;
import thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.util.IntersectDistance;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Intersection;

/**
 * Created by Jager on 2016.02.09..
 */
public class ClosestPointsStrategy extends CommonPointStrategy
{
       /**
        * Ez működik általános esetre, amikor nincs pontos metszéspont, így közelítő pontot ad vissza
        * A legközelebbi 3 metszéspont átlagpontját adjuk vissza
        * Kiválasztja a metszéspontok távolságai közül a legrövidebb hármat, ez három pontot eredményez, melyek átlagpontját adjuk vissza
        *
        * @param intersections Metszetek listája
        * @return A metszéspontok közös pontja a legközelebbi metszéspontok átlaga, ezzel tér vissza
        */
       @Override
       public PointF CalculateCommonPoint(List<Intersection> intersections)
       {
              List<IntersectDistance> idistances = new ArrayList<IntersectDistance>();     // A metszéspontok távolságai
              IntersectDistance idist;
              for (int i = 0; i < intersections.size(); i++)
              {
                     for (int j = 0; j < intersections.size(); j++)
                     {
                            if (i != j)
                            {
                                   // Egy metszet két metszéspontjának távolságát nem is vesszük figyelembe
                                   idist = new IntersectDistance(intersections.get(i).Points().get(0), intersections.get(j).Points().get(1));
                                   if (!idistances.contains(idist)) idistances.add(idist);
                                   idist = new IntersectDistance(intersections.get(i).Points().get(0), intersections.get(j).Points().get(0));
                                   if (!idistances.contains(idist)) idistances.add(idist);
                                   idist = new IntersectDistance(intersections.get(i).Points().get(1), intersections.get(j).Points().get(1));
                                   if (!idistances.contains(idist)) idistances.add(idist);
                            }
                     }
              }

              // Az összes metszéspontot ebben a listában tároljuk
              List<PointF> intersectPoints = new ArrayList<PointF>();

              //Ebben a listában tároljuk el a k legközelebbi metszéspontokat
              List<PointF> closestPoints = new ArrayList<PointF>();

              IntersectDistance firstDistance = getMinimumDistance(idistances);
              closestPoints.add(firstDistance.P1);
              closestPoints.add(firstDistance.P2);

              for (Intersection i : intersections)
              {
                     for (PointF p : i.Points())
                     {
                            if (!p.equals(firstDistance.P1) && !p.equals(firstDistance.P2))
                            {
                                   intersectPoints.add(p);
                            }
                     }
              }

              PointF candidate1 = getClosestPoint(firstDistance.P1, intersectPoints);
              PointF candidate2 = getClosestPoint(firstDistance.P2, intersectPoints);

              closestPoints.add(getThirdPoint(candidate1, candidate2, firstDistance));

              return CalculatorUtility.PointAverage(closestPoints);
       }

       /**
        * Kikeressük a legkisebb metszet távolságot, vagyis a két, egymáshoz legközelebbi metszéspont távolságát.
        */
       private IntersectDistance getMinimumDistance(List<IntersectDistance> idistances)
       {
              int mini = 0;
              for (int i = 1; i < idistances.size(); i++)
              {
                     if (idistances.get(i).Distance < idistances.get(mini).Distance) mini = i;
              }
              return idistances.get(mini);
       }

       /**
        * Kikeressük azt a metszet távolságot, ami a legkisebb távolság egyik pontjától a legkisebb távolság
        */
       private PointF getClosestPoint(PointF P, List<PointF> IntersectionPoints)
       {
              int mini = 0;
              PointF intersectionPoint;
              double mindist = CalculatorUtility.Distance(P, IntersectionPoints.get(0));
              double dist;
              for (int i = 1; i < IntersectionPoints.size(); i++)
              {
                     intersectionPoint = IntersectionPoints.get(i);
                     dist = CalculatorUtility.Distance(P, intersectionPoint);
                     if (dist < mindist)
                     {
                            mini = i;
                            mindist = dist;
                     }
              }
              return IntersectionPoints.get(mini);
       }

       private PointF getThirdPoint(PointF candidate1, PointF candidate2, IntersectDistance minimumDistance)
       {
              double dist1 = getDistance(candidate1, minimumDistance.P1, minimumDistance.P2);
              double dist2 = getDistance(candidate2, minimumDistance.P1, minimumDistance.P2);
              return dist1 < dist2 ? candidate1 : candidate2;
       }

       private double getDistance(PointF p1, PointF p2, PointF p3)
       {
              return CalculatorUtility.Distance(p1, p2) + CalculatorUtility.Distance(p2, p3) + CalculatorUtility.Distance(p1, p3);
       }
}