package thesis.jager.indoorpositioning.positioning.tagmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import thesis.jager.indoorpositioning.util.ExtStorageHelper;

/**
 * Created by Jager on 2016.02.08..
 */
public class PositionDatabaseHelper extends SQLiteOpenHelper
{

       public PositionDatabaseHelper(Context context, String name)
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

       public static final String DATABASE_NAME = PositionDatabaseDef.DATABASE_NAME;
       public static String DATABASE_PATH = null;
       public static final int DATABASE_VERSION = 1;

       public static String DATABASE_CREATE_ALL = PositionDatabaseDef.DATABASE_CREATE;
       public static String DATABASE_DROP_ALL = PositionDatabaseDef.DATABASE_DROP;
}

class PositionDatabaseManager
{
       private PositionDatabaseManager(Context AppContext)
       {
              appContext = AppContext;
       }

       private static PositionDatabaseManager self;

       public static PositionDatabaseManager getInstance(Context AppContext)
       {
              if (self == null)
                     self = new PositionDatabaseManager(AppContext);
              return self;
       }

       private Context appContext;
       private PositionDatabaseHelper dbHelper;
       private SQLiteDatabase mDB;

       private boolean fileExist(String FileName)
       {
              return new File(FileName).exists();
       }

       private void open()
       {
              dbHelper = new PositionDatabaseHelper(appContext, DBConstants.getDatabasePath(appContext));
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

       public long insertData(PositionDatabaseDef data)
       {
              open();
              ContentValues values = new ContentValues();
              values.put(PositionDatabaseDef.COLUMN_MAC, data.getMac());
              values.put(PositionDatabaseDef.COLUMN_CX, data.getValue_cx());
              values.put(PositionDatabaseDef.COLUMN_CY, data.getValue_cy());
              long rowid = mDB.insert(PositionDatabaseDef.DB_TABLE, null, values);
              close();
              return rowid;
       }

       public int deleteData(String DatabaseField, String KeyValue)
       {
              open();
              int deletedRows = mDB.delete(PositionDatabaseDef.DB_TABLE,
                      String.format("%s='%s'", DatabaseField, KeyValue), null);
              close();
              return deletedRows;
       }

       public void clearAllData()
       {
              open();
              mDB.execSQL(String.format("DELETE FROM %s", PositionDatabaseDef.DB_TABLE));
              close();
       }

       public int updateValue(String DatabaseField, String KeyValue, String ColumnToModify, Object newValue)
       {
              open();
              ContentValues values = new ContentValues();
              values.put(ColumnToModify, newValue.toString());
              int updatedRows = mDB.update(PositionDatabaseDef.DB_TABLE, values,
                      String.format("%s='%s'", DatabaseField, KeyValue), null);
              close();
              return updatedRows;
       }

       public List<PositionDatabaseDef> getAllRecords()
       {
              List<PositionDatabaseDef> records = new ArrayList<PositionDatabaseDef>();
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
              String query = String.format("select * from %s", PositionDatabaseDef.DB_TABLE);
              Cursor c = mDB.rawQuery(query, null);
              c.moveToFirst(); // Mielőtt lezárjuk a kapcsolatot, az első elemre kell lépni, különben nem lesz adat!
              close();
              return c;
       }

       public PositionDatabaseDef selectData(String ColumnName, String KeyValue)
       {
              open();
              String query = String.format("select * from %s where %s='%s'", PositionDatabaseDef.DB_TABLE, ColumnName, KeyValue);
              Cursor c = mDB.rawQuery(query, null);
              boolean hasData = c.moveToFirst();
              close();
              if (hasData)
                     return getDataByCursor(c);
              return null;
       }

       public List<PositionDatabaseDef> fetchAll()
       {
              open();
              String[] fields =
                      {PositionDatabaseDef.COLUMN_ID, PositionDatabaseDef.COLUMN_MAC, PositionDatabaseDef.COLUMN_CX, PositionDatabaseDef.COLUMN_CY};
              Cursor c = mDB.query(PositionDatabaseDef.DB_TABLE, fields, null, null, null, null, null);
              List<PositionDatabaseDef> data = new ArrayList<>();
              boolean hasData = c.moveToFirst();
              if(hasData) do
              {
                     PositionDatabaseDef item = getDataByCursor(c);
                     data.add(item);
                     hasData = c.moveToNext();
              }while(hasData);
              close();
              return data;
       }

       public PositionDatabaseDef fetchData(String DatabaseField, String KeyValue)
       {
              open();
              String[] fields =
                      {PositionDatabaseDef.COLUMN_ID, PositionDatabaseDef.COLUMN_MAC, PositionDatabaseDef.COLUMN_CX, PositionDatabaseDef.COLUMN_CY};
              Cursor c = mDB.query(PositionDatabaseDef.DB_TABLE, fields,
                      String.format("%s='%s'", DatabaseField, KeyValue), null, null, null, null);
              boolean hasData = c.moveToFirst();
              close();
              if (hasData)
                     return getDataByCursor(c);
              return null;
       }

       private PositionDatabaseDef getDataByCursor(Cursor c)
       {
              return new PositionDatabaseDef(c.getString(c.getColumnIndex(PositionDatabaseDef.COLUMN_MAC)),
                      Double.parseDouble(c.getString(c.getColumnIndex(PositionDatabaseDef.COLUMN_CX))), Double.parseDouble(c.getString(c.getColumnIndex(PositionDatabaseDef.COLUMN_CY))));
       }
}
