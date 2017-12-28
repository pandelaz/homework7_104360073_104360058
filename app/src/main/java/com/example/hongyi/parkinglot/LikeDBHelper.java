package com.example.hongyi.parkinglot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by hongyi on 2017/12/8.
 */
public class LikeDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "HighCPShop.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Like";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";

    public LikeDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        Log.d(TAG, "DBHepler: TABLE_NAME = " + TABLE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_ADDRESS + " TEXT, " +
                        COLUMN_LAT + " TEXT, " +
                        COLUMN_LNG + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertLike(Integer id, String name, String address, String LAT, String LNG) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_LAT, LAT);
        contentValues.put(COLUMN_LNG, LNG);

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateLike(Integer id, String name, String address, String LAT, String LNG) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_LAT, LAT);
        contentValues.put(COLUMN_LNG, LNG);

        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteLike(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "ShopDBHepler: deleteshop = " + String.valueOf(id));

        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getLike(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllLikes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        } catch (android.database.sqlite.SQLiteException e) {
            onCreate(db);
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        }
        return res;
    }
}
