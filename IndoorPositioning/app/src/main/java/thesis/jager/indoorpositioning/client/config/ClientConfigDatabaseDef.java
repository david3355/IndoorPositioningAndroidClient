package thesis.jager.indoorpositioning.client.config;

public class ClientConfigDatabaseDef
{
       public static String DATABASE_NAME = "config.data";

       public static final String DB_TABLE = "config";
       public static final String COLUMN_KEY = "key";
       public static final String COLUMN_VALUE = "value";

       public static final String DATABASE_CREATE = String
               .format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT)",
                       DB_TABLE, COLUMN_KEY, COLUMN_VALUE);
       public static final String DATABASE_DROP = String.format("DROP TABLE IF EXISTS %s", DB_TABLE);

       public ClientConfigDatabaseDef(String value_key, String value_value)
       {
              this.value_key = value_key;
              this.value_value = value_value;
       }

       private String value_key;
       private String value_value;

       public String getValue_key()
       {
              return value_key;
       }

       public void setValue_key(String value_key)
       {
              this.value_key = value_key;
       }

       public String getValue_value()
       {
              return value_value;
       }

       public void setValue_value(String value_value)
       {
              this.value_value = value_value;
       }


}
