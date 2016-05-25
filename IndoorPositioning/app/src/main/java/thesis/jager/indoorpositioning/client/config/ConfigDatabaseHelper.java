package thesis.jager.indoorpositioning.client.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import thesis.jager.indoorpositioning.util.ExtStorageHelper;

/**
 * Created by Jager on 2016.05.04..
 */
public class ConfigDatabaseHelper extends SQLiteOpenHelper
{
       public ConfigDatabaseHelper(Context context, String name)
       {
              super(context, name, null, DBConstants.DATABASE_VERSION);
       }

       @Override
       public void onCreate(SQLiteDatabase db)
       {
              db.execSQL(DBConstants.DATABASE_CREATE_ALL);
       }

       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
       {
              db.execSQL(DBConstants.DATABASE_DROP_ALL);
              db.execSQL(DBConstants.DATABASE_CREATE_ALL);
       }
}

class DBConstants
{
       public static String getDatabasePath(Context context)
       {
              if (ExtStorageHelper.isStorageAccessible())
              {
                     if (DATABASE_PATH == null)
                            DATABASE_PATH = ExtStorageHelper.getPublicDatabasePath(context, DATABASE_NAME);
                     return DATABASE_PATH;
              }
              return DATABASE_NAME;
       }

       public static final String DATABASE_NAME = ClientConfigDatabaseDef.DATABASE_NAME;
       public static String DATABASE_PATH = null;
       public static final int DATABASE_VERSION = 1;

       public static String DATABASE_CREATE_ALL = ClientConfigDatabaseDef.DATABASE_CREATE;
       public static String DATABASE_DROP_ALL = ClientConfigDatabaseDef.DATABASE_DROP;
}

class ConfigDatabaseManager
{
       private ConfigDatabaseManager(Context AppContext)
       {
              appContext = AppContext;
       }

       private static ConfigDatabaseManager self;

       public static ConfigDatabaseManager getInstance(Context AppContext)
       {
              if (self == null)
                     self = new ConfigDatabaseManager(AppContext);
              return self;
       }

       private Context appContext;
       private ConfigDatabaseHelper dbHelper;
       private SQLiteDatabase mDB;

       private boolean fileExist(String FileName)
       {
              return new File(FileName).exists();
       }

       private void open()
       {
              dbHelper = new ConfigDatabaseHelper(appContext, DBConstants.getDatabasePath(appContext));
              mDB = dbHelper.getWritableDatabase();
              if (mDB != null && !fileExist(getAbsuluteDBPath()))
                     dbHelper.onCreate(mDB);
       }

       private void close()
       {
              dbHelper.close();
       }

       public String getAbsuluteDBPath()
       {
              return mDB.getPath();
       }

       public long insertData(ClientConfigDatabaseDef data)
       {
              open();
              ContentValues values = new ContentValues();
              values.put(ClientConfigDatabaseDef.COLUMN_KEY, data.getValue_key());
              values.put(ClientConfigDatabaseDef.COLUMN_VALUE, data.getValue_value());
              long rowid = mDB.insert(ClientConfigDatabaseDef.DB_TABLE, null, values);
              close();
              return rowid;
       }

       public int deleteData(String DatabaseField, String KeyValue)
       {
              open();
              int deletedRows = mDB.delete(ClientConfigDatabaseDef.DB_TABLE,
                      String.format("%s='%s'", DatabaseField, KeyValue), null);
              close();
              return deletedRows;
       }

       public void clearAllData()
       {
              open();
              mDB.execSQL(String.format("DELETE FROM %s", ClientConfigDatabaseDef.DB_TABLE));
              close();
       }

       public int updateValue(String DatabaseField, String KeyValue, String ColumnToModify, Object newValue)
       {
              open();
              ContentValues values = new ContentValues();
              values.put(ColumnToModify, newValue.toString());
              int updatedRows = mDB.update(ClientConfigDatabaseDef.DB_TABLE, values,
                      String.format("%s='%s'", DatabaseField, KeyValue), null);
              close();
              return updatedRows;
       }

       public List<ClientConfigDatabaseDef> getAllRecords()
       {
              List<ClientConfigDatabaseDef> records = new ArrayList<ClientConfigDatabaseDef>();
              Cursor c = selectAll();
              if (!c.moveToFirst())
                     return records;
              do
              {
                     records.add(getDataByCursor(c));
              } while (c.moveToNext());
              return records;
       }

       public Cursor selectAll()
       {
              open();
              String query = String.format("SELECT * FROM %s", ClientConfigDatabaseDef.DB_TABLE);
              Cursor c = mDB.rawQuery(query, null);
              c.moveToFirst(); // Mielőtt lezárjuk a kapcsolatot, az első elemre kell lépni, különben nem lesz adat!
              close();
              return c;
       }

       public ClientConfigDatabaseDef selectData(String ColumnName, String KeyValue)
       {
              open();
              String query = String.format("SELECT * FROM %s WHERE %s='%s'", ClientConfigDatabaseDef.DB_TABLE, ColumnName, KeyValue);
              Cursor c = mDB.rawQuery(query, null);
              boolean hasData = c.moveToFirst();
              close();
              if (hasData)
                     return getDataByCursor(c);
              return null;
       }

       public List<ClientConfigDatabaseDef> fetchAll()
       {
              open();
              String[] fields =
                      {ClientConfigDatabaseDef.COLUMN_KEY, ClientConfigDatabaseDef.COLUMN_VALUE};
              Cursor c = mDB.query(ClientConfigDatabaseDef.DB_TABLE, fields, null, null, null, null, null);
              List<ClientConfigDatabaseDef> data = new ArrayList<>();
              boolean hasData = c.moveToFirst();
              if (hasData) do
              {
                     ClientConfigDatabaseDef item = getDataByCursor(c);
                     data.add(item);
                     hasData = c.moveToNext();
              } while (hasData);
              close();
              return data;
       }

       public ClientConfigDatabaseDef fetchData(String DatabaseField, String KeyValue)
       {
              open();
              String[] fields =
                      {ClientConfigDatabaseDef.COLUMN_KEY, ClientConfigDatabaseDef.COLUMN_VALUE};
              Cursor c = mDB.query(ClientConfigDatabaseDef.DB_TABLE, fields,
                      String.format("%s='%s'", DatabaseField, KeyValue), null, null, null, null);
              boolean hasData = c.moveToFirst();
              close();
              if (hasData)
                     return getDataByCursor(c);
              return null;
       }

       private ClientConfigDatabaseDef getDataByCursor(Cursor c)
       {
              return new ClientConfigDatabaseDef(c.getString(c.getColumnIndex(ClientConfigDatabaseDef.COLUMN_KEY)),
                      (c.getString(c.getColumnIndex(ClientConfigDatabaseDef.COLUMN_VALUE))));
       }
}