package thesis.jager.indoorpositioning.positioning.locationcalc.calculating_strategies;

import java.util.List;

import thesis.jager.indoorpositioning.positioning.locationcalc.commonpoint_strategies.CommonPointStrategy;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Intersection;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.LocationResult;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.TagDistanceRepresentation;

/**
 * Created by Jager on 2016.02.09..
 */
public class AverageClosestDistanceLocationCalculator extends ClosestDistanceLocationCalculator
{
       public AverageClosestDistanceLocationCalculator(CommonPointStrategy CommonPointStrategy)
       {
              super(CommonPointStrategy);
       }

       @Override
       protected LocationResult CalculateCommonPoint(List<TagDistanceRepresentation> Distances, LocationResult LastLocation)
       {
              List<TagDistanceRepresentation> leastDistances = CalculateClosestDistances(Distances, 3);

              //Kikényszerítjük, hogy minden esetben legyen metszéspont:
              ForceAllIntersections(leastDistances);

              List<Intersection> intersectionPoints = GetIntersections(leastDistances); // ebben lesznek két-két kör metszéspontjai
              Intersection.removeInvalidIntersections(intersectionPoints);
              return new LocationResult(commonPointStrategy.CommonPointOfIntersections(intersectionPoints), Precision.ThreeOrMoreTag);
       }
}
