package thesis.jager.indoorpositioning.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Jager on 2016.01.24..
 */
public class ExtStorageHelper
{
       public  static boolean isStorageAccessible()
       {
              String state = Environment.getExternalStorageState();
              if(state.equals(Environment.MEDIA_MOUNTED))
              {
                     return true;
              }
              return false;
       }

       public static String getPublicDatabasePath(Context context, String fileName)
       {
              File file =  context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
              return file.getPath()+ "/" + fileName;
       }

       public static String getPrivateDatabasePath(Context context, String fileName)
       {
              File f = context.getDatabasePath(fileName);
              return f.getPath();
       }

}
