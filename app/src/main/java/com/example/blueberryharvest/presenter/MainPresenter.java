package com.example.blueberryharvest.presenter;

import android.content.Context;

import com.example.blueberryharvest.data.Model;
import com.example.blueberryharvest.data.Picker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter {
    private Model model;

    public MainPresenter(Context c) throws SQLException {
        model = Model.getInstance(c);
    }

    public List<Picker> getPickers(String date) {

        return model.getPickers(date);
    }

    public int getNextID() {
        return model.getNextID();
    }

    public void addPicker(String name) {
        this.model.addPicker(name);
    }

    public boolean addBucket(String id, String date, float weight) {
        if (id.contains(" ")) {
            int tmp =  Integer.parseInt(id.substring(id.lastIndexOf(" ")+1));
            return this.model.addBucket(tmp, date, weight);
        }
        else {
            return false;
        }
    }

    public double getTotalDayPounds(String date) {
        return this.model.getTotalDayPounds(date);
    }

    public ArrayList<String> getAllPickers() {
        return this.model.getAllPickers();
    }

    public boolean deletePicker(String id) {
        if (id.contains(" ")) {
            int tmp =  Integer.parseInt(id.substring(id.lastIndexOf(" ")+1));
            return this.model.deletePicker(tmp);
        }
        else {
            return false;
        }
    }
}
