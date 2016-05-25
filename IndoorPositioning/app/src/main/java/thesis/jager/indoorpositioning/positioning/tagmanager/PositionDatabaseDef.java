package thesis.jager.indoorpositioning.positioning.tagmanager;

public class PositionDatabaseDef
{
       public static String DATABASE_NAME = "positions.data";

       public static final String DB_TABLE = "positions";
       public static final String COLUMN_ID = "id";
       public static final String COLUMN_MAC = "mac";
       public static final String COLUMN_CX = "cx";
       public static final String COLUMN_CY = "cy";

       public static final String DATABASE_CREATE = String
               .format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s REAL)",
                       DB_TABLE, COLUMN_ID, COLUMN_MAC, COLUMN_CX, COLUMN_CY);
       public static final String DATABASE_DROP = String.format("drop table if exists %s", DB_TABLE);

       public PositionDatabaseDef(String mac, double coordinate_x, double coordinate_y)
       {
              value_mac = mac;
              value_cx = coordinate_x;
              value_cy = coordinate_y;
       }

       private String value_mac;
       private double value_cx;
       private double value_cy;

       public double getValue_cx()
       {
              return value_cx;
       }

       public void setValue_cx(double value_cx)
       {
              this.value_cx = value_cx;
       }

       public double getValue_cy()
       {
              return value_cy;
       }

       public void setValue_cy(double value_cy)
       {
              this.value_cy = value_cy;
       }


       public String getMac()
       {
              return value_mac;
       }

       public void setMac(String value_mac)
       {
              this.value_mac = value_mac;
       }



}
