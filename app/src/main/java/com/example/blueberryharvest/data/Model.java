package com.example.blueberryharvest.data;

import android.os.Build;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static Model instance = null;
    private List<Picker> pickers;
    private int nextID;

    private Model() {
        pickers = new ArrayList<Picker>();
        String date = "";

        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            date = dtf.format(now);
        }
        Picker picker1 = new Picker("Crystal Rowley", 1, date);
        Picker picker2 = new Picker("Rachel Rowley", 2, date);
        Picker picker3 = new Picker("Stewart Rowley", 3, date);
        Picker picker4 = new Picker("Emily Rowley", 4, date);
        Picker picker5 = new Picker("Douglas Rowley", 5, date);
        Picker picker6 = new Picker("Mckenzie Rowley", 6, date);
        Picker picker7 = new Picker("Blake Rowley", 7, date);
        Picker picker8 = new Picker("Jaq Rowley", 8, date);
        Picker picker9 = new Picker("Tom Rowley", 9, date);
        Picker picker10 = new Picker("Shanie Rowley", 10, date);
        Picker picker11 = new Picker("Kyle Rowley", 11, date);
        Picker picker12 = new Picker("John Rowley", 12, date);

        String time = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime now = LocalTime.now();
            time = dtf.format(now);
        }
        Bucket bucket1 = new Bucket(6.5, time, "12/12/2012");
        Bucket bucket2 = new Bucket(7.0, time, "12/12/2012");
        Bucket bucket3 = new Bucket(4.5, time, "12/12/2012");
        Bucket bucket4 = new Bucket(6.25, time, "12/12/2012");
        picker1.addBucket(bucket1, date);
        picker1.addBucket(bucket2, date);
        picker1.addBucket(bucket3, date);
        picker1.addBucket(bucket4, date);

        pickers.add(picker1);
        pickers.add(picker2);
        pickers.add(picker3);
        pickers.add(picker4);
        pickers.add(picker5);
        pickers.add(picker6);
        pickers.add(picker7);
        pickers.add(picker8);
        pickers.add(picker9);
        pickers.add(picker10);
        pickers.add(picker11);
        pickers.add(picker12);

        this.nextID = 13;
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
            return instance;
        }
        return instance;
    }

    public Picker findPicker(int id) {
        for(int i = 0; i < this.pickers.size(); i++) {
            if(this.pickers.get(i).getNumID() == id) {
                return this.pickers.get(i);
            }
        }
        return null;
    }


    public List<Picker> getPickers(String date) {
        return this.pickers;
    }

    public List<Bucket> getBuckets(int id, String date) {
        for(int i = 0; i < this.pickers.size(); i++) {
            if(this.pickers.get(i).getNumID() == id) {
                return this.pickers.get(i).getRecord(date).getBuckets();
            }
        }
        Log.d("model activity", "getBuckets returned null");
        return null;
    }

    public void addBucket(int id, String date, double weight) {
        String time = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime now = LocalTime.now();
            time = dtf.format(now);
        }
        Picker picker = this.findPicker(id);
        Bucket bucket = new Bucket(weight, time, date);
        picker.addBucket(bucket, date);
    }

    public void addPicker(String name) {
        String date = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            date = dtf.format(now);
        }
        Picker newPicker = new Picker(name, this.nextID, date);
        pickers.add(newPicker);
        this.nextID += 1;
    }

    public double getTotalDayPounds(String date) {
        List<Picker> dayPickers = this.getPickers(date);
        double total = 0.0;
        for (Picker p : dayPickers) {
            total += p.getRecord(date).getTotalPounds();
        }
        return total;
    }

    public int getNextID() {
        return nextID;
    }
}
