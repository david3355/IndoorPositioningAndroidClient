package thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thesis.jager.indoorpositioning.positioning.locationcalc.util.CalculatorUtility;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Intersection;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.LocationResult;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.TagDistanceRepresentation;

/**
 * Created by Jager on 2016.02.09..
 */
public abstract class CalculatorStrategy
{
       private final double BIAS = 0.1;

       public LocationResult CalculateLocation(List<TagDistanceRepresentation> nearbyDistances, LocationResult LastLocation)
       {
              List<TagDistanceRepresentation> Distances = new ArrayList<TagDistanceRepresentation>(nearbyDistances);   // Az eredeti másolata, hogy az eredeti ne módosuljon
              removeZeroAverages(Distances);
              if (Distances.size() == 0) return LastLocation;
              if (Distances.size() == 1)
                     return new LocationResult(Distances.get(0).getOrigo(), Precision.OneTag, Distances.get(0).getAvgPredictedDistance()); // 1 vagy semennyi kör esetén nem tudunk pozíciót meghatározni

              if (Distances.size() == 2) // 2 kör esetén a metszet két pontja közti felezőpont kell
              {
                     ForceAllIntersections(Distances);
                     Intersection points = Intersection.CalculateIntersection(Distances.get(0).getOrigo(), Distances.get(0).getAvgPredictedDistance(), Distances.get(1).getOrigo(), Distances.get(1).getAvgPredictedDistance());
                     return new LocationResult(CalculatorUtility.Midpoint(points.Points().get(0), points.Points().get(1)), Precision.TwoTag, CalculatorUtility.Distance(points.Points().get(0), points.Points().get(1)) / 2);
              }
              return CalculateCommonPoint(Distances, LastLocation);
       }

       protected abstract LocationResult CalculateCommonPoint(List<TagDistanceRepresentation> Distances, LocationResult LastLocation);

       private void removeZeroAverages(List<TagDistanceRepresentation> distances)
       {
              Iterator<TagDistanceRepresentation> iter = distances.iterator();
              while (iter.hasNext())
              {
                     TagDistanceRepresentation tag = iter.next();
                     if (tag.getAvgRSSI() == 0.0) iter.remove();
              }
       }


       /**
        * Kiszámolja a megadott távolságoknak megfelelő körök metszéspontjait
        * @param Distances  A közeli bluetooth tag-ek eszköztől mért távolságait tartalmazzák, melyek köröknek felelnek meg
        * @return    A visszaadott Intersection lista minden eleme 2 kör metszéspontait tartalmazza
        */
       protected List<Intersection> GetIntersections(List<TagDistanceRepresentation> Distances)
       {
              List<Intersection> intersectionPoints = new ArrayList<Intersection>();
              for (int i = 0; i < Distances.size(); i++)
              {
                     for (int j = i + 1; j < Distances.size(); j++)
                     {
                            if (i != j)
                                   intersectionPoints.add(Intersection.CalculateIntersection(Distances.get(i).getOrigo(), Distances.get(i).getAvgPredictedDistance(), Distances.get(j).getOrigo(), Distances.get(j).getAvgPredictedDistance()));
                     }
              }
              return intersectionPoints;
       }

       /**
        * Ha egyszer is módosítani kellett valahol, akkor újra kell ellenőrizni az egészet, hogy mindenhol van-e metszéspont továbbra is
        */
       protected boolean IntersectionCheck(List<TagDistanceRepresentation> LeastDistances)
       {
              boolean requiredModification = false;
              for (int i = 0; i < LeastDistances.size(); i++)
              {
                     for (int j = 0; j < LeastDistances.size(); j++)
                     {
                            if (i != j)
                            {
                                   requiredModification |= IntersectionCheck(LeastDistances.get(i), LeastDistances.get(j));
                            }
                     }
              }
              return requiredModification;
       }

       /**
        *     Ez a metódus kikényszeríti, hogy az összes távolság által reprezentált körnek legyen metszéspontja
        */
       protected void ForceAllIntersections(List<TagDistanceRepresentation> LeastDistances)
       {
              int checkNum = 0;
              final int MAXITERATIONS = 100;

              boolean requiredModification = false;
              do
              {
                     requiredModification = IntersectionCheck(LeastDistances);
                     checkNum++;
              }
              while (requiredModification && checkNum < MAXITERATIONS);
       }

       /**
        * Leellenőrzi azokat az eseteket, amikor nincs metszéspont, és beállítja a távolságokat úgy, hogy legyen
        * @param t0 Az első bluetooth tag távolsága által reprezentált kör
        * @param t1  A második bluetooth tag távolsága által reprezentált kör
        * @return true, ha módosítani kellett, mert nem volt metszéspont, false, ha volt metszéspont
        */
       protected boolean IntersectionCheck(TagDistanceRepresentation t0, TagDistanceRepresentation t1)
       {
              // Első eset: a két távolság által jelzett kör messze van egymástól:
              // Megoldás: mindkettőt arányosan növeljük
              boolean requiredModification = CheckFarDistances(t0, t1);

              // Második eset: az egyik kör a másikon belül helyezkedik el:
              // Megoldás: a nagyobb kört arányosan csökkentjük, a kisebb kört pedig arányosan növeljük
              if (!requiredModification) requiredModification = CheckInclude(t0, t1);
              return requiredModification;
       }

       /**
        * Első eset: a két távolság által jelzett kör messze van egymástól:
        * Megoldás: mindkettőt arányosan növeljük
        */
       protected boolean CheckFarDistances(TagDistanceRepresentation tag1, TagDistanceRepresentation tag2)
       {
              if (CalculatorUtility.Distance(tag1.getOrigo(), tag2.getOrigo()) > tag1.getAvgPredictedDistance() + tag2.getAvgPredictedDistance())
              {
                     double deviance = CalculatorUtility.Distance(tag1.getOrigo(), tag2.getOrigo()) - (tag1.getAvgPredictedDistance() + tag2.getAvgPredictedDistance());
                     deviance += BIAS;  // Az eltérés torzítása, így biztosan lesz metszéspont
                     double max = tag1.getAvgPredictedDistance() + tag2.getAvgPredictedDistance();
                     double rate1 = tag1.getAvgPredictedDistance() / max;
                     double rate2 = tag2.getAvgPredictedDistance() / max;
                     tag1.setAvgPredictedDistance((float) (tag1.getAvgPredictedDistance() + deviance * rate1));
                     tag2.setAvgPredictedDistance((float) (tag2.getAvgPredictedDistance() + deviance * rate2));


                     //TEST
                     double dist = CalculatorUtility.Distance(tag1.getOrigo(), tag2.getOrigo()) - (tag1.getAvgPredictedDistance() + tag2.getAvgPredictedDistance());
                     if (dist > 0.0)
                            dist = 0; /* throw new Exception("Calculation error: distance must be 0.0"); */
                     //TEST

                     return true;
              } else return false;
       }

       /**
        * Második eset: az egyik kör a másikon belül helyezkedik el:
        * Megoldás: a nagyobb kört arányosan csökkentjük, a kisebb kört pedig arányosan növeljük
        */
       protected boolean CheckInclude(TagDistanceRepresentation tag1, TagDistanceRepresentation tag2)
       {
              TagDistanceRepresentation bigger, smaller;
              if (tag1.Includes(tag2))
              {
                     bigger = tag1;
                     smaller = tag2;
              } else if (tag2.Includes(tag1))
              {
                     bigger = tag2;
                     smaller = tag1;
              } else return false;

              double deviance = bigger.getAvgPredictedDistance() - (CalculatorUtility.Distance(bigger.getOrigo(), smaller.getOrigo()) + smaller.getAvgPredictedDistance());
              deviance += BIAS;   // Az eltérés torzítása, így biztosan lesz metszéspont
              double max = bigger.getAvgPredictedDistance() + smaller.getAvgPredictedDistance();
              double brate = bigger.getAvgPredictedDistance() / max;
              double srate = smaller.getAvgPredictedDistance() / max;
              bigger.setAvgPredictedDistance((float) (bigger.getAvgPredictedDistance() - deviance * brate));
              smaller.setAvgPredictedDistance((float) (smaller.getAvgPredictedDistance() + deviance * srate));


              //TEST
              double dist = bigger.getAvgPredictedDistance() - (CalculatorUtility.Distance(bigger.getOrigo(), smaller.getOrigo()) + smaller.getAvgPredictedDistance());
              if (dist > 0.0)
                     dist = 0; /* throw new Exception("Calculation error: distance must be 0.0"); */
              //TEST

              return true;
       }

}
