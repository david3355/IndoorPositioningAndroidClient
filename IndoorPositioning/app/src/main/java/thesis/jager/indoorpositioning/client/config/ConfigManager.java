package thesis.jager.indoorpositioning.client.config;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by Jager on 2016.05.04..
 */
public class ConfigManager
{
       private ConfigManager(Context context)
       {
              database = ConfigDatabaseManager.getInstance(context);
              initDatabase();
       }

       private final static String CONFIGKEY_HOST = "host_address";
       private final static String CONFIGKEY_SAVEDUSER = "saved_username";
       private final static String CONFIGKEY_SAVEDPWD = "saved_pwd";
       private final static String CONFIGKEY_SAVECHECKED = "save_checked";

       private final static String DEFAULT_HOST = "192.168.1.113";

       private ConfigDatabaseManager database;
       private static ConfigManager self;

       public static ConfigManager getInstance(Context context)
       {
              if (self == null) self = new ConfigManager(context);
              return self;
       }

       public static ConfigManager getInstance()
       {
              if (self == null)
                     throw new RuntimeException("You must call getInstance(Context) before calling this!");
              return self;
       }

       public void initDatabase()
       {
              insertConfigKeyIfNotExists(CONFIGKEY_HOST, DEFAULT_HOST);
              insertConfigKeyIfNotExists(CONFIGKEY_SAVEDUSER, "");
              insertConfigKeyIfNotExists(CONFIGKEY_SAVEDPWD, "");
              insertConfigKeyIfNotExists(CONFIGKEY_SAVECHECKED, "0");
       }

       private void insertConfigKeyIfNotExists(String configkey, String configvalue)
       {
              ClientConfigDatabaseDef data = database.fetchData(ClientConfigDatabaseDef.COLUMN_KEY, configkey);
              if (data == null)
              {
                     ClientConfigDatabaseDef config = new ClientConfigDatabaseDef(configkey, configvalue);
                     database.insertData(config);
              }
       }

       private String getConfigValue(String configKey)
       {
              ClientConfigDatabaseDef configdata = database.fetchData(ClientConfigDatabaseDef.COLUMN_KEY, configKey);
              if (configdata == null)
                     throw new Resources.NotFoundException("No configuration found with key: " + configKey);
              return configdata.getValue_value();
       }

       public String getHostAddress()
       {
              String host = getConfigValue(CONFIGKEY_HOST);
              return host;
       }

       public String getSavedUsername()
       {
              String saved_username = getConfigValue(CONFIGKEY_SAVEDUSER);
              return saved_username;
       }

       public int saveUsername(String username)
       {
              int updatedrows = database.updateValue(ClientConfigDatabaseDef.COLUMN_KEY, CONFIGKEY_SAVEDUSER, ClientConfigDatabaseDef.COLUMN_VALUE, username);
              return updatedrows;
       }

       public String getSavedPassword()
       {
              String saved_password = getConfigValue(CONFIGKEY_SAVEDPWD);
              return saved_password;
       }

       public int savePassword(String password)
       {
              int updatedrows = database.updateValue(ClientConfigDatabaseDef.COLUMN_KEY, CONFIGKEY_SAVEDPWD, ClientConfigDatabaseDef.COLUMN_VALUE, password);
              return updatedrows;
       }

       public boolean getSaveChecked()
       {
              String saved_password = getConfigValue(CONFIGKEY_SAVECHECKED);
              return saved_password.equals("0") ? false : true;
       }

       public int saveSaveCheck(boolean saveCheck)
       {
              int updatedrows = database.updateValue(ClientConfigDatabaseDef.COLUMN_KEY, CONFIGKEY_SAVECHECKED, ClientConfigDatabaseDef.COLUMN_VALUE, saveCheck ? "1" : "0");
              return updatedrows;
       }

       public List<ClientConfigDatabaseDef> getAllConfig()
       {
              return database.fetchAll();
       }
}
