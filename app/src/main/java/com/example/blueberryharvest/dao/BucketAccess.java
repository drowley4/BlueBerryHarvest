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

    public boolean addBucket(int id, String date, String time, float weight, String variety) {
        dbhelper.insertBucket(id, date, time, weight, variety);
        return true;
    }

    public List<Bucket> getBuckets(int id, String date, String variety) {
        List<Bucket> buckets = new ArrayList<>();
        List<Bucket> tmpBuckets = new ArrayList<>();
        Cursor res = dbhelper.getBuckets(id, date);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            buckets.add(new Bucket(res.getFloat(res.getColumnIndex("WEIGHT")),res.getString(res.getColumnIndex("TIME")), date, res.getString(res.getColumnIndex("VARIETY"))));
            res.moveToNext();
        }
        res.close();
        for(int i = 0; i < buckets.size(); i++) {
            if (buckets.get(i).getVariety() == null) {
                tmpBuckets.add(buckets.get(i));
                continue;
            }
            if(buckets.get(i).getVariety().equals(variety)) {
                tmpBuckets.add(buckets.get(i));
            }
        }
        return tmpBuckets;
    }

    public boolean deleteBucket(int id, String time) {
        return dbhelper.deleteBucket(id, time);
    }

}
