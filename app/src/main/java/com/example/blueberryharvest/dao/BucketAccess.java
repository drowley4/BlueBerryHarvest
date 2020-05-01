package com.example.blueberryharvest.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.blueberryharvest.data.Bucket;

import java.util.ArrayList;
import java.util.List;


public class BucketAccess {
    private DatabaseHelper dbhelper;
    public BucketAccess(Context c) {
        dbhelper = DatabaseHelper.getInstance(c);
    }

    public boolean addBucket(int id, String date, String time, float weight) {
        dbhelper.insertBucket(id, date, time, weight);

        return true;
    }

    public List<Bucket> getBuckets(int id, String date) {
        List<Bucket> buckets = new ArrayList<>();
        Cursor res = dbhelper.getBuckets(id, date);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            buckets.add(new Bucket(res.getFloat(res.getColumnIndex("WEIGHT")),res.getString(res.getColumnIndex("TIME")), date, id));
            res.moveToNext();
        }
        res.close();
        return buckets;
    }

    public boolean deleteBucket(int id, String time) {
        return dbhelper.deleteBucket(id, time);
    }

}
