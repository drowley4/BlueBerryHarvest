package com.example.blueberryharvest.presenter;

import com.example.blueberryharvest.data.Bucket;
import com.example.blueberryharvest.data.Model;

import java.sql.SQLException;
import java.util.List;

public class PersonPresenter {
    private Model model;

    public PersonPresenter() throws SQLException {
        model = Model.getInstance();
    }

    public List<Bucket> getBuckets(int id, String date) {
        return model.getBuckets(id, date);
    }

}
