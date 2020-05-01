package com.example.blueberryharvest.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.blueberryharvest.dao.PickerAccess;
import com.example.blueberryharvest.dao.BucketAccess;
import com.example.blueberryharvest.dao.RecordAccess;

public class Model {
    private static Model instance = null;
    private List<Picker> pickers;
    private BucketAccess bucketAccess;
    private PickerAccess pickerAccess;
    private RecordAccess recordAccess;

    private Model(Context c) throws SQLException {
        pickers = new ArrayList<Picker>();
        bucketAccess = new BucketAccess(c);
        pickerAccess = new PickerAccess(c);
        recordAccess = new RecordAccess(c);



        String date = "";

        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            date = dtf.format(now);
        }


        String time = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime now = LocalTime.now();
            time = dtf.format(now);
        }
    }

    public static Model getInstance(Context c) throws SQLException {
        if (instance == null) {
            instance = new Model(c);
            return instance;
        }
        return instance;
    }

    public Picker findPicker(int id) {
        ArrayList<Picker> allPickers = pickerAccess.getAllPickers();
        for(int i = 0; i < allPickers.size(); i++) {
            if(allPickers.get(i).getNumID() == id) {
                return allPickers.get(i);
            }
        }
        return null;
    }


    public List<Picker> getPickers(String date) {
        Log.d("Model Activity", "getPickers() called " + date);
        this.pickers = recordAccess.getPickers(date);
        for(Picker p: this.pickers) {
            p.insertRecord(date, new Record(date, bucketAccess.getBuckets(p.getNumID(), date)));
        }
        return this.pickers;
    }

    public List<Bucket> getBuckets(int id, String date) {
        return bucketAccess.getBuckets(id, date);

    }

    public boolean addBucket(int id, String date, float weight) {
        String time = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime now = LocalTime.now();
            time = dtf.format(now);
        }
     //   Picker picker = this.findPicker(id);
     //   Bucket bucket = new Bucket(weight, time, date, id);
    //    picker.addBucket(bucket, date);
        boolean tmp = bucketAccess.addBucket(id, date, time, weight);
        Picker p = findPicker(id);
        if (p.getRecord(date) == null) {
            recordAccess.addDay(id, date);
        }
        return tmp;
    }

    public void addPicker(String name) {
        String date = "";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            date = dtf.format(now);
        }
        int nextID = pickerAccess.getNextID();
        pickerAccess.addPicker(name, nextID);
        recordAccess.addDay(nextID, date);
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
        return pickerAccess.getNextID();
    }

    public ArrayList<String> getAllPickers() {
        return pickerAccess.getAllPickerNames();
    }

    public boolean deletePicker(int id) {
        return pickerAccess.deletePicker(id);
    }

    public boolean deleteBucket(int id, String time) {
        return bucketAccess.deleteBucket(id, time);
    }
}
