package com.example.blueberryharvest.presenter;

import android.content.Context;

import com.example.blueberryharvest.data.Model;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Presenter {
    Model model;

    Presenter(Context c) throws SQLException {
        this.model = Model.getInstance(c);
    }

    public ArrayList<String> getAllPickers() {
        return this.model.getAllPickers();
    }

    public double getTotalDayPounds(String date) {
        return this.model.getTotalDayPounds(date);
    }

    public String getCherrySetting() {
        return model.getCherrySetting();
    }
}
