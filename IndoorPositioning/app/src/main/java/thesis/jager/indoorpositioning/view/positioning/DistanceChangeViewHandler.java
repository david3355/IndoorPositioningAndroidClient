package thesis.jager.indoorpositioning.view.positioning;

import android.graphics.PointF;
import thesis.jager.indoorpositioning.positioning.locationcalc.util.Precision;

/**
 * Created by Jager on 2016.02.10..
 */
public interface DistanceChangeViewHandler
{
       void distanceFromTagChanged(String mac, float distance);
       void simulatedLocationChanged(PointF location, Precision locationResultPrecision, String username);
}
