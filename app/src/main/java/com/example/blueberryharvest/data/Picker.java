package com.example.blueberryharvest.data;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Picker {
    private String name;

    private int numID;

    private Map<String, Record> records;

    public Picker(String name, int numID) {
        this.name = name;
        this.numID = numID;
        this.records = new HashMap<String, Record>();
    }

    @Override
    public String toString() {
        return "Picker{" +
                "name='" + name + '\'' +
                ", numID=" + numID +
                ", records=" + records +
                '}';
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

    public void insertRecord(String date, Record record) {
        this.records.put(date, record);
    }

}
