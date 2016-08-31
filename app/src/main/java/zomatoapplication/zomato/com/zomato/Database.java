package zomatoapplication.zomato.com.zomato;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Bipin on 5/26/2016.
 */
public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Zomato.db";
    public static final String TABLE_NAME1 = "Restro_table";

    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_LAT = "LAT";
    public static final String COL_LONG = "LON";

    public Database(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,LAT DOUBLE,LON DOUBLE)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME1);
        onCreate(db);

    }

    public boolean insertdata(String name,Double lat,Double lon){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME,name);
        contentValues.put(COL_LAT,lat);
        contentValues.put(COL_LONG,lon);

        long result = db.insert(TABLE_NAME1,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;


    }

    public ArrayList<String> location(int pos)
    {
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " SELECT "+"*"+ " FROM " + TABLE_NAME1  +  " WHERE " + COL_ID + "= " + pos + ";";
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor != null)
        {
            if(cursor.moveToFirst()){


               String name =(cursor.getString(1));
                String lat = String.valueOf((cursor.getDouble(2)));
                String lon = String.valueOf((cursor.getDouble(3)));

                arrayList.add(name);
                arrayList.add(lat);
                arrayList.add(lon);



                Log.d("name,lat,long", "location: "+name+lat+lon);
            }
            cursor.close();
        }

            return arrayList;

    }



}
