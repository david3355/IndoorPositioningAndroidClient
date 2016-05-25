package thesis.jager.indoorpositioning.discovery;

import org.joda.time.DateTime;

/**
 * Created by Jager on 2016.04.15..
 */
public class Beacon
{
       public Beacon(int rssi, DateTime timestamp)
       {
              this.rssi = rssi;
              this.timestamp = timestamp;
       }

       public int rssi;
       public DateTime timestamp;

       @Override
       public String toString()
       {
              return String.format("(%s)", rssi);
       }
}
