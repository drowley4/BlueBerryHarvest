package com.example.blueberryharvest.dao;

import android.content.Context;
import android.database.Cursor;

import com.example.blueberryharvest.data.Picker;

import java.util.ArrayList;


public class PickerAccess {
    private DatabaseHelper dbhelper;
    public PickerAccess(Context c) {
        dbhelper = DatabaseHelper.getInstance(c);
    }

    public int getNextID() {
        return dbhelper.numberOfPickers()+1;
    }

    public boolean addPicker(String name, int id) {
        dbhelper.insertPicker(id, name);
        return true;
    }

    public ArrayList<String> getAllPickerNames() {
        String id;
        int tmp;
        String name;
        ArrayList<String> names = new ArrayList<>();
        Cursor res = dbhelper.getPickerNames();
        res.moveToFirst();
        while(!res.isAfterLast()) {
            tmp = res.getInt(res.getColumnIndex("ID"));
            if(tmp < 0) {
                res.moveToNext();
                continue;
            }
            id = Integer.toString(tmp);
            name = res.getString(res.getColumnIndex("NAME"));
            names.add(name + " " + id);
            res.moveToNext();
        }
        return names;
    }

    public ArrayList<Picker> getAllPickers() {
        ArrayList<Picker> pickers = new ArrayList<>();
        Cursor res = dbhelper.getPickerNames();
        res.moveToFirst();
        while(!res.isAfterLast()) {
            int id = res.getInt(res.getColumnIndex("ID"));
            String name = res.getString(res.getColumnIndex("NAME"));
            pickers.add(new Picker(name, id));
            res.moveToNext();
        }
        return pickers;
    }

    public boolean deletePicker(int id) {
        return dbhelper.deletePicker(id);
    }

    public boolean updatePicker(int id, String name, String email) {
        return dbhelper.updatePicker(id, name, email);
    }

    public String getEmail(int id) {
        return dbhelper.getEmail(id);
    }

    public boolean isThere(int id) {
        return dbhelper.isThere(id);
    }

}
