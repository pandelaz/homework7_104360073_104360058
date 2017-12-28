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
public class ParkDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "HighCPShop.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Park";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATA = "pdata";
    public static final String COLUMN_PID = "pid";

    public ParkDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        Log.d(TAG, "DBHepler: TABLE_NAME = " + TABLE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_DATA + " TEXT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_PID + " TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertPark(Integer id, String pdata, String name, String pid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATA, pdata);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PID, pid);


        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updatePark(Integer id, String pdata, String name, String pid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATA, pdata);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PID, pid);

        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePark(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "ShopDBHepler: deleteshop = " + String.valueOf(id));

        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public void DeleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "DeleteTable: Order_TABLE_NAME = " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public Cursor getPark(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        } catch (android.database.sqlite.SQLiteException e) {
            onCreate(db);
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        }
        return res;
    }


    public Cursor getParkbyName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_NAME + "=?", new String[]{name});
        } catch (android.database.sqlite.SQLiteException e) {
            onCreate(db);
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_NAME + "=?", new String[]{name});
        }
        return res;
    }

    public Cursor getParkbyID(String pid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_PID + "=?", new String[]{pid});
        } catch (android.database.sqlite.SQLiteException e) {
            onCreate(db);
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    COLUMN_PID + "=?", new String[]{pid});
        }
        return res;
    }

    public Cursor getAllParks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        return res;
    }
}
