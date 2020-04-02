package com.example.blueberryharvest.presenter;

import com.example.blueberryharvest.data.Model;
import com.example.blueberryharvest.data.Picker;

import java.sql.SQLException;
import java.util.List;

public class MainPresenter {
    private Model model;

    public MainPresenter() throws SQLException {
        model = Model.getInstance();
    }

    public List<Picker> getPickers(String date) {

        return model.getPickers("");
    }

    public int getNextID() {
        return model.getNextID();
    }

    public void addPicker(String name) {
        this.model.addPicker(name);
    }

    public void addBucket(int id, String date, double weight) {
        this.model.addBucket(id, date, weight);
    }

    public double getTotalDayPounds(String date) {
        return this.model.getTotalDayPounds(date);
    }

}
