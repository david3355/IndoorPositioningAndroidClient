package thesis.jager.indoorpositioning.positioning.tagmanager;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by Jager on 2016.02.10..
 */
public class TagPositionManager
{
       private TagPositionManager(Context context)
       {
              database = PositionDatabaseManager.getInstance(context);
       }

       private PositionDatabaseManager database;
       private static TagPositionManager self;

       public static TagPositionManager getInstance(Context context)
       {
              if(self == null) self = new TagPositionManager(context);
              return self;
       }

       public PointF getTagPosition(String mac)
       {
              PositionDatabaseDef devdata = database.fetchData(PositionDatabaseDef.COLUMN_MAC, mac);
              if(devdata == null) throw new Resources.NotFoundException("No tag found with MAC address: " + mac);

              return new PointF((float)devdata.getValue_cx(), (float)devdata.getValue_cy());
       }

       public List<PositionDatabaseDef> getAllTagPosition()
       {
             return database.fetchAll();
       }
}
