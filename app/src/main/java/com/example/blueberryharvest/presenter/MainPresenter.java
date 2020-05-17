package com.example.blueberryharvest.presenter;

import android.content.Context;

import com.example.blueberryharvest.data.Picker;

import java.sql.SQLException;
import java.util.List;

public class MainPresenter extends Presenter {

    public MainPresenter(Context c) throws SQLException {
        super(c);
    }

    public List<Picker> getPickers(String date) {

        return model.getPickers(date);
    }

    public int getNextID() {
        return super.model.getNextID();
    }

    public void addPicker(String name) {
        super.model.addPicker(name);
    }

    public boolean addBucket(String id, String date, float weight) {
        int tmp =  Integer.parseInt(id.substring(id.lastIndexOf(" ")+1));
        return super.model.addBucket(tmp, date, weight);
    }

}
