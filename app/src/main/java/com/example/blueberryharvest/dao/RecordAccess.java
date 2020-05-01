package com.example.blueberryharvest.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.blueberryharvest.data.Picker;

import java.util.ArrayList;
import java.util.List;


public class RecordAccess {
    private DatabaseHelper dbhelper;

    public RecordAccess(Context c) {
        dbhelper = DatabaseHelper.getInstance(c);
    }

    public List<Picker> getPickers(String date) {
        List<Picker> pickers = new ArrayList<>();
        Picker p;
        Cursor res = dbhelper.getPickers(date);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            p = new Picker(res.getString(res.getColumnIndex("NAME")), res.getInt(res.getColumnIndex("ID")));
            pickers.add(p);
            res.moveToNext();
        }

        res.close();
        return pickers;
    }

    public boolean addDay(int id, String date) {
        return dbhelper.insertRecord(id, date);
    }


}
