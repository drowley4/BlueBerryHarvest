package com.example.blueberryharvest.dao;

import android.util.Log;

import com.example.blueberryharvest.data.Bucket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BucketAccess {
    public BucketAccess() {
    }

    public void create() throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();
            String create = "create table if not exists Buckets (time text not null primary key, " +
                    "pickerID int not null, date text not null, weight double not null)";
            PreparedStatement stmt = connection.prepareStatement(create);
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("bucket access activity", sx.toString());
        }
    }

    public void insert(Bucket bucket) throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String insert = "insert into Buckets values (?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(insert);
            stmt.setString(1, bucket.getTime());
            stmt.setInt(2, bucket.getPickerID());
            stmt.setString(3, bucket.getDate());
            stmt.setDouble(4, bucket.getWeight());
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("bucket access activity", sx.toString());
        }
    }

    public Bucket[] findAllBuckets(String date, int pickerID) throws SQLException {
        Database db = new Database();
        Connection connection = db.getConnection();

        String query = "select * from event where (date = ? AND pickerID = ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, date);
        stmt.setInt(2, pickerID);
        ResultSet results = stmt.executeQuery();
        int size = 0;
        while(results.next()) {
            size += 1;
        }
        results = stmt.executeQuery();
        Bucket[] buckets = new Bucket[size];
        int i = 0;
        while(results.next()) {
            buckets[i] = new Bucket(results.getDouble(4), results.getString(1),
                    results.getString(3), results.getInt(2));
            i += 1;
        }
        results.close();
        stmt.close();
        db.closeConnection();
        return buckets;
    }

}
