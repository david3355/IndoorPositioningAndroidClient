package thesis.jager.indoorpositioning.positioning.locationcalc.util;

import android.graphics.PointF;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thesis.jager.indoorpositioning.discovery.Beacon;
import thesis.jager.indoorpositioning.positioning.distancecalc.DistanceCalculator;

/**
 * Created by Jager on 2016.04.17..
 */
public class TagDistanceRepresentation
{
       public TagDistanceRepresentation(String mac, PointF origo, DistanceCalculator distanceCalculator)
       {
              this.mac = mac;
              this.origo = origo;
              this.distanceCalculator = distanceCalculator;
              avgRSSI = 0;
              avgPredictedDistance = 0.0f;
              this.beacons = new ArrayList<Beacon>();
       }

       static
       {
              averaging_interval = new Period(0, 0, 0, 20000);
       }

       private String mac;
       private PointF origo;
       private List<Beacon> beacons;
       private int avgRSSI;
       private float avgPredictedDistance;
       private DistanceCalculator distanceCalculator;

       private static Period averaging_interval;

       public String getMac()
       {
              return mac;
       }

       public PointF getOrigo()
       {
              return origo;
       }

       public int getAvgRSSI()
       {
              return avgRSSI;
       }

       public float getAvgPredictedDistance()
       {
              return avgPredictedDistance;
       }

       public void setAvgPredictedDistance(float avgPredictedDistance)
       {
              this.avgPredictedDistance = avgPredictedDistance;
       }

       public static void setAveragingInterval(Period period)
       {
              averaging_interval = period;
       }

       public void addBeacon(Beacon beacon)
       {
              beacons.add(beacon);
              refreshAverageDistance();
       }

       public boolean Includes(TagDistanceRepresentation tag)
       {
              if (this.avgPredictedDistance > CalculatorUtility.Distance(this.origo, tag.origo) + tag.avgPredictedDistance)
                     return true;
              return false;
       }

       @Override
       public boolean equals(Object obj)
       {
              TagDistanceRepresentation dist = (TagDistanceRepresentation) obj;
              return this.origo.x == dist.origo.x && this.origo.x == dist.origo.x && this.mac.equals(dist.mac);
       }

       private void refreshAverageDistance()
       {
              synchronized (beacons)
              {
                     removeOldBeacons();
                     calcAverageRSSI();
                     avgPredictedDistance = (float) distanceCalculator.getDistanceByRSSI(avgRSSI);
              }
       }

       private int calcAverageRSSI()
       {
              double sum = 0.0;
              for (Beacon beacon : beacons)
              {
                     sum += beacon.rssi;
              }
              if (beacons.size() == 0) avgRSSI = 0;
              else avgRSSI = (int) Math.round(sum / beacons.size());
              return avgRSSI;
       }

       private void removeOldBeacons()
       {
              DateTime now = DateTime.now();
              Iterator<Beacon> iter = beacons.iterator();
              while (iter.hasNext())
              {
                     Beacon beacon = iter.next();
                     long beacon_age = now.getMillis() - beacon.timestamp.getMillis();
                     int delay = averaging_interval.getMillis();
                     if (beacon_age > delay)
                     {
                            iter.remove();
                     }
              }
       }

       @Override
       public String toString()
       {
              StringBuilder stb = new StringBuilder();
              for (Beacon b : beacons)
              {
                     stb.append(b.toString() + " ");
              }
              return String.format("%s, %s - %s (O: %s;%s) - [%s]",avgRSSI, avgPredictedDistance, this.mac, origo.x, origo.y, stb.toString() );
       }
}
