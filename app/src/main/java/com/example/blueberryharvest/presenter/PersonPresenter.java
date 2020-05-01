package com.example.blueberryharvest.presenter;

import android.content.Context;

import com.example.blueberryharvest.data.Bucket;
import com.example.blueberryharvest.data.Model;

import java.sql.SQLException;
import java.util.List;

public class PersonPresenter {
    private Model model;

    public PersonPresenter(Context c) throws SQLException {
        model = Model.getInstance(c);
    }

    public List<Bucket> getBuckets(int id, String date) {
        return model.getBuckets(id, date);
    }

    public boolean deleteBucket(int id, String time) {
        return model.deleteBucket(id, time.substring(6));
    }

}
