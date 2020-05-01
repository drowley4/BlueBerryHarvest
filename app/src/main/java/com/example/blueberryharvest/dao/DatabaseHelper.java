package com.example.blueberryharvest.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance = null;

    // database name
    private static final String DATABASE_NAME = "BlueBerry.db";

    // table names
    private static final String TABLE_PICKERS = "Pickers";
    private static final String TABLE_BUCKETS = "Buckets";
    private static final String TABLE_RECORDS = "Records";

    // pickers table's column names
    private static final String PICKER_COL_1 = "ID";
    private static final String PICKER_COL_2 = "NAME";

    // buckets table's column names
    private static final String BUCKET_COL_1 = "ID";
    private static final String BUCKET_COL_2 = "DATE";
    private static final String BUCKET_COL_3 = "TIME";
    private static final String BUCKET_COL_4 = "WEIGHT";

    // records table's column names
    private static final String RECORD_COL_1 = "ID";
    private static final String RECORD_COL_2 = "DATE";
    private static final String RECORD_COL_3 = "TOTAL";

    // create table statements
    private static final String CREATE_PICKERS_TABLE = "create table " + TABLE_PICKERS + " (ID INTEGER PRIMARY KEY, NAME TEXT)";
    private static final String CREATE_BUCKETS_TABLE = "create table " + TABLE_BUCKETS + " (ID INTEGER, DATE TEXT, TIME TEXT, WEIGHT FLOAT, CONSTRAINT PK_Bucket PRIMARY KEY (ID,TIME))";
    private static final String CREATE_RECORDS_TABLE = "create table " + TABLE_RECORDS + " (ID INTEGER, DATE TEXT, TOTAL FLOAT, CONSTRAINT PK_Record PRIMARY KEY (ID, DATE))";

    // drop table statements
    private static final String DROP_PICKERS_TABLE = "DROP TABLE IF EXISTS " + TABLE_PICKERS;
    private static final String DROP_BUCKETS_TABLE = "DROP TABLE IF EXISTS " + TABLE_BUCKETS;
    private static final String DROP_RECORDS_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECORDS;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }


    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PICKERS_TABLE);
        db.execSQL(CREATE_BUCKETS_TABLE);
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_PICKERS_TABLE);
        db.execSQL(DROP_BUCKETS_TABLE);
        db.execSQL(DROP_RECORDS_TABLE);
        onCreate(db);
    }

    public boolean insertPicker(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PICKER_COL_1, id);
        values.put(PICKER_COL_2, name);
        if (db.insert(TABLE_PICKERS, null, values) == -1) {
            return false;
        }
        return true;
    }

    public boolean insertBucket(int id, String date, String time, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUCKET_COL_1, id);
        values.put(BUCKET_COL_2, date);
        values.put(BUCKET_COL_3, time);
        values.put(BUCKET_COL_4, weight);
        if (db.insert(TABLE_BUCKETS, null, values) == -1) {
            return false;
        }
        return true;
    }

    public boolean insertRecord(int id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECORD_COL_1, id);
        values.put(RECORD_COL_2, date);
        values.put(RECORD_COL_3, 0);
        if (db.insert(TABLE_RECORDS, null, values) == -1) {
            return false;
        }
        return true;
    }

    public boolean updateRecord(int id, String date, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RECORD_COL_3, weight);
        if(db.update(TABLE_RECORDS, values, "ID=? AND DATE=?", new String[]{Integer.toString(id), date}) == 1) {
            return true;
        }
        return false;
    }

    public Cursor getPickers(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Records.ID,Records.TOTAL,Pickers.NAME FROM " + TABLE_RECORDS + " INNER JOIN " + TABLE_PICKERS + " ON Records.ID=Pickers.ID WHERE Records.DATE=?", new String[]{date});
        return res;
    }

    public int numberOfPickers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT ID FROM " + TABLE_PICKERS, null);
        res.moveToFirst();
        int numRows = 0;
        while (!res.isAfterLast()) {
            numRows = res.getInt(res.getColumnIndex("ID"));
            res.moveToNext();
        }
        res.close();
        return numRows;
    }

    public Cursor getBuckets(int id, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Buckets.TIME,Buckets.WEIGHT FROM " + TABLE_BUCKETS + " WHERE Buckets.DATE=? AND Buckets.ID=?", new String[]{date, Integer.toString(id)});
        return res;
    }

    public Cursor getPickerNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT ID,NAME FROM " + TABLE_PICKERS, null);
        return res;
    }

    public boolean deleteBucket(int id, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(TABLE_BUCKETS, "TIME=? AND ID=?", new String[]{time, Integer.toString(id)});
        if (ret == 0) {
            return false;
        }
        return true;
    }

    public boolean deletePicker(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUCKETS, "ID=?", new String[]{Integer.toString(id)});
        db.delete(TABLE_RECORDS, "ID=?", new String[]{Integer.toString(id)});
        db.delete(TABLE_PICKERS, "ID=?", new String[]{Integer.toString(id)});
        return true;
    }
}
