package thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies;

import java.util.ArrayList;
import java.util.List;

import thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.CommonPointStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Intersection;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.LocationResult;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.TagDistanceRepresentation;

/**
 * Created by Jager on 2016.04.17..
 */
public class ClosestDistanceLocationCalculator extends CalculatorStrategy
{
       public ClosestDistanceLocationCalculator(CommonPointStrategy CommonPointStrategy)
       {
              commonPointStrategy = CommonPointStrategy;
       }

       protected CommonPointStrategy commonPointStrategy;

       /**
        * A megadott bluetooth tag-ek távolságaiból visszaadja a k legkisebb távolságú tag-et, vagyis azok tag-eket, amelyek legközelebb vannak az eszközhöz
        * @param Distances A közeli bluetooth tag-ek, melyek tárolják az eszköztől számított közelítő távoságot
        * @param kLeastDistances Az a k érték amennyi bluetooth tag-et vissza akarunk kapni, melyek a legközelebb vannak az eszköztől
        * @return A k legkisebb távolságú tag, amelyek legközelebb vannak az eszközhöz
        */
       protected List<TagDistanceRepresentation> CalculateClosestDistances(List<TagDistanceRepresentation> Distances, int kLeastDistances)
       {
              // Válasszuk ki a k legkisebb távolságot:
              List<TagDistanceRepresentation> distancesCopy = new ArrayList<TagDistanceRepresentation>(Distances);
              List<TagDistanceRepresentation> leastDistances = new ArrayList<TagDistanceRepresentation>();
              int mini;
              for (int j = 0; j < kLeastDistances; j++)
              {
                     mini = 0;
                     for (int i = 1; i < distancesCopy.size(); i++)
                     {
                            if (distancesCopy.get(i).getAvgPredictedDistance() < distancesCopy.get(mini).getAvgPredictedDistance())
                                   mini = i;
                     }
                     leastDistances.add(distancesCopy.get(mini));
                     distancesCopy.remove(mini);
              }
              return leastDistances;
       }

       @Override
       protected LocationResult CalculateCommonPoint(List<TagDistanceRepresentation> Distances, LocationResult LastLocation)
       {
              List<TagDistanceRepresentation> leastDistances = CalculateClosestDistances(Distances, 3);

              List<Intersection> intersectionPoints = GetIntersections(leastDistances); // ebben lesznek két-két kör metszéspontjai

              return new LocationResult(commonPointStrategy.CommonPointOfIntersections(intersectionPoints), Precision.ThreeOrMoreTag);
       }
}
