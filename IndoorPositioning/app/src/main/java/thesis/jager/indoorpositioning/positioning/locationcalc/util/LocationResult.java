package thesis.jager.indoorpositioning.positioning.locationcalc.util;

import android.graphics.PointF;

/**
 * Created by Jager on 2016.02.09..
 */
public class LocationResult
{
       public LocationResult(PointF SimulatedLocation, Precision Precision, double Radius)
       {
              this.simulatedLocation = SimulatedLocation;
              this.precision = Precision;
              this.radius = Radius;
       }

       public LocationResult(PointF SimulatedLocation, Precision Precision)
       {
              this(SimulatedLocation, Precision, 0);
       }

       public LocationResult() { }

       public PointF simulatedLocation;
       public Precision precision;
       public double radius;
}
