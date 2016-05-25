package thesis.jager.indoorpositioning.positioning.locationcalc.util;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jager on 2016.02.09..
 */
public class Intersection
{
       public Intersection(PointF P0, float r0, PointF P1, float r1)
       {
              points = CalculateIntersection(P0, r0, P1, r1).Points();
       }

       public Intersection(List<PointF> IntersectionPoints)
       {
              points = IntersectionPoints;
       }

       private List<PointF> points;

       public List<PointF> Points()
       {
              return points;
       }

       public static Intersection CalculateIntersection(PointF P0, float r0, PointF P1, float r1)
       {
              float d, a, h;
              PointF P2, isPoint1, isPoint2;
              d = (float) CalculatorUtility.Distance(P0, P1);

              if (d > r0 + r1)
                     return new Intersection(new ArrayList<PointF>());    // Két külön körről van szó
              if (d < Math.abs(r0 - r1))
                     return new Intersection(new ArrayList<PointF>());   // Az egyik kör a másikon belül van
              if (d == 0 && r0 == r1)
                     return new Intersection(null);      // A két kör éppen egybeesik

              a = (float) (Math.pow(r0, 2) - Math.pow(r1, 2) + Math.pow(d, 2)) / (2 * d);
              h = (float) Math.sqrt(Math.pow(r0, 2) - Math.pow(a, 2));
              P2 = new PointF(P0.x + a * (P1.x - P0.x) / d, P0.y + a * (P1.y - P0.y) / d);

              isPoint1 = new PointF(P2.x + h * (P1.y - P0.y) / d, P2.y - h * (P1.x - P0.x) / d);
              isPoint2 = new PointF(P2.x - h * (P1.y - P0.y) / d, P2.y + h * (P1.x - P0.x) / d);

              // Ha a két kör éppen érinti egymást egy ponton (d = r1 + r2), akkor is két pont lesz a metszet eredménye, de ezek egybeesnek
              List<PointF> solutions = new ArrayList<PointF>();
              solutions.add(isPoint1);
              solutions.add(isPoint2);
              return new Intersection(solutions);
       }

       @Override
       public String toString()
       {
              if (points == null) return "Infinite intersection points";
              if (points.size() == 0) return "No intersection point";
              StringBuilder stb = new StringBuilder();
              for (int i = 0; i < points.size(); i++)
              {
                     stb.append(String.format("[%s;%s]", points.get(i).x, points.get(i).y));
                     if (i == 0) stb.append(" - ");
              }
              return stb.toString();
       }

       public static void removeInvalidIntersections(List<Intersection> intersections)
       {
              Iterator<Intersection> iter = intersections.iterator();
              while (iter.hasNext()) {
                     Intersection next = iter.next();
                     if (next == null || next.Points()== null || next.Points().size() == 0) {
                            iter.remove();
                     }
              }
       }
}