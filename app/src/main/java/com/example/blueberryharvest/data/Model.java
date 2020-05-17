package com.example.blueberryharvest.data;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.blueberryharvest.dao.PickerAccess;
import com.example.blueberryharvest.dao.BucketAccess;
import com.example.blueberryharvest.dao.RecordAccess;
import com.opencsv.CSVWriter;

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
        return sortPickersTotal(this.pickers, date);
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

    public void updateBackupEmail(String email) {
        if(!pickerAccess.isThere(-1)) {
            pickerAccess.addPicker("backup", -1);
        }
        updatePicker(-1, "", email);
    }

    public void updateExportEmail(String email) {
        if(!pickerAccess.isThere(-2)) {
            pickerAccess.addPicker("export", -2);
        }
        updatePicker(-2, "", email);
    }

    public boolean updatePicker(int id, String name, String email) {
        return pickerAccess.updatePicker(id, name, email);
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

    public String getEmail(int id) {
        return pickerAccess.getEmail(id);
    }

    private List<Picker> sortPickersTotal(List<Picker> pickers, String date) {
        for(int i = 0; i < pickers.size(); i++) {
            for(int j= 0; j < pickers.size(); j++) {
                if(pickers.get(i).getRecord(date).getTotalPounds() > pickers.get(j).getRecord(date).getTotalPounds()) {
                    Collections.swap(pickers, i, j);
                }
            }
        }
        return pickers;
    }

    private List<Picker> sortPickersID(List<Picker> pickers) {
        for(int i = 0; i < pickers.size(); i++) {
            for(int j= 0; j < pickers.size(); j++) {
                if(pickers.get(i).getNumID() < pickers.get(j).getNumID()) {
                    Collections.swap(pickers, i, j);
                }
            }
        }
        return pickers;
    }

    public String exportDatabase(String date) {
        List<Picker> pickers = sortPickersID(getPickers(date));
        String folder_name = "blueberrytotals";
        File f = new File(Environment.getExternalStorageDirectory(), folder_name);
        if (!f.exists()) {
            f.mkdirs();
        }
        String nDate = date.substring(0,2) + "_" + date.substring(3,5) + "_" + date.substring(6,10);
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/blueberrytotals/" + nDate + ".csv");
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));

            List<String[]> data = new ArrayList<String[]>();
            String[] arr = {"name", "total"};
            data.add(arr);
            int tmpNum = 0;
            String[] arr2 = new String[2];
            arr2[1] = "0";
            for(Picker p: pickers) {
                if(p.getNumID() < 0) {
                    continue;
                }
                List<Bucket> buckets = p.getRecord(date).getBuckets();
                arr = new String[2];
                arr[0] = p.getName();
                double total = 0;
                for(Bucket b: buckets) {
                    total += b.getWeight();
                }
                arr[1] = Double.toString(total);
                while(tmpNum < p.getNumID()) {
                    data.add(arr2);
                    tmpNum += 1;
                }
                tmpNum += 1;
                data.add(arr);
            }

            writer.writeAll(data); // data is adding to csv

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csv;
    }

    public String exportIndividual(int id, String date) {
        List<Picker> pickers = sortPickersID(getPickers(date));
        Picker p = null;
        for(Picker pi: pickers) {
            if(pi.getNumID() == id) {
                p = pi;
                break;
            }
        }
        String folder_name = "blueberryindividuals";
        File f = new File(Environment.getExternalStorageDirectory(), folder_name);
        if (!f.exists()) {
            f.mkdirs();
        }
        String nDate = date.substring(0,2) + "_" + date.substring(3,5) + "_" + date.substring(6,10);
        String title = "id=" + p.getNumID() + "_" + nDate;
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/blueberryindividuals/" + title + ".csv");
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));

            List<String[]> data = new ArrayList<String[]>();
            String[] arr = {"name"};
            String[] arr2;
            data.add(arr);
            List<Bucket> buckets = p.getRecord(date).getBuckets();
            arr = new String[buckets.size()+1];
            arr2 = new String[buckets.size()+1];
            int index = 1;
            arr[0] = p.getName();
            arr2[0] = "time";
            double total = 0;
            for(Bucket b: buckets) {
                total += b.getWeight();
                arr[index] = Double.toString(b.getWeight());
                arr2[index] = b.getTime().substring(0, 5);
                index++;
            }
            data.add(arr);
            data.add(arr2);
            arr = new String[2];
            arr[0] = "total";
            arr[1] = Double.toString(total);
            data.add(arr);


            writer.writeAll(data); // data is adding to csv

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csv;
    }

    public String backupDatabase(String date) {
        List<Picker> pickers = getPickers(date);
        String time = "0";
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm");
            LocalTime now = LocalTime.now();
            time = dtf.format(now);
        }
        String folder_name = "blueberrybackups";
        File f = new File(Environment.getExternalStorageDirectory(), folder_name);
        if (!f.exists()) {
            f.mkdirs();
        }
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/blueberrybackups/T" + time + ".csv");
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv));

            List<String[]> data = new ArrayList<String[]>();
            String[] arr = {"name"};
            String[] arr2;
            data.add(arr);
            for(Picker p: pickers) {
                if(p.getNumID() < 0) {
                    continue;
                }
                List<Bucket> buckets = p.getRecord(date).getBuckets();
                arr = new String[buckets.size()+1];
                arr2 = new String[buckets.size()+1];
                int index = 1;
                arr[0] = p.getName();
                arr2[0] = "time";
                for(Bucket b: buckets) {
                    arr[index] = Double.toString(b.getWeight());
                    arr2[index] = b.getTime().substring(0, 5);
                    index++;
                }
                data.add(arr);
                data.add(arr2);
            }

            writer.writeAll(data); // data is adding to csv

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csv;
    }
}
