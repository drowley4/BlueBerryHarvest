package com.example.blueberryharvest.data;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Picker {
    private String name = "";

    private int numID;

    private Map<String, Record> records;

    public Picker() {
        this.name = "";
        this.numID = 0;
        this.records = new HashMap<String, Record>();
    }

    public Picker(String name, int numID, String date) {
        this.name = name;
        this.numID = numID;
        this.records = new HashMap<String, Record>();
        this.insertRecord(date);
    }

    public String getName() {
        return name;
    }

    public int getNumID() {
        return numID;
    }

    public Record getRecord(String date) {
        return this.records.get(date);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumID(int numID) {
        this.numID = numID;
    }

    private void insertRecord(String date) {
        this.records.put(date, new Record(date));
    }

    public void addBucket(Bucket bucket, String date) {
        Record record = this.records.get(date);
        if(record != null) {
            record.insertBucket(bucket);
            this.records.put(date, record);
        }
        else {
            Log.d("Picker activity", "addBucket returned null");
        }

    }
}