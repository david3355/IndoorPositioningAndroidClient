package thesis.jager.indoorpositioning.positioning.distancecalc;

/**
 * Created by Jager on 2016.04.15..
 */
public class PolyRegressionModelDistanceCalculator implements DistanceCalculator
{
       private PolyRegressionModelDistanceCalculator()
       {
       }

       private static PolyRegressionModelDistanceCalculator self;

       public static PolyRegressionModelDistanceCalculator getInstance()
       {
              if (self == null) self = new PolyRegressionModelDistanceCalculator();
              return self;
       }

       // The two constant values of this equation is acquired from the polynomial regression model based on the rssi measures taken from known distances
       public double getDistanceByRSSI(int RSSI)
       {
              return -0.00000000002457955958 * Math.pow(RSSI, 7.0) + 0.17043942467948633000;
       }

}
