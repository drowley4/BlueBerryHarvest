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
        Database db = new Database();
        Connection connection = db.getConnection();

        String create = "create table if not exists bucket (date text not null primary key, " +
                "time text not null, weight double not null)";
        PreparedStatement stmt = connection.prepareStatement(create);
        stmt.executeUpdate();
        stmt.close();
        db.closeConnection();
    }

    public void insert(Bucket bucket) throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String insert = "insert into bucket values (?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(insert);
            stmt.setString(1, bucket.getDate());
            stmt.setString(2, bucket.getTime());
            stmt.setDouble(3, bucket.getWeight());
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("bucket access activity", sx.toString());
        }
    }

    public Bucket[] findAllBuckets(String date) throws SQLException {
        Database db = new Database();
        Connection connection = db.getConnection();

        String query = "select * from event where date = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, date);
        ResultSet results = stmt.executeQuery();
        int size = 0;
        while(results.next()) {
            size += 1;
        }
        results = stmt.executeQuery();
        Bucket[] buckets = new Bucket[size];
        int i = 0;
        while(results.next()) {
            buckets[i] = new Bucket(results.getDouble(3), results.getString(2),
                    results.getString(1));
            i += 1;
        }
        results.close();
        stmt.close();
        db.closeConnection();
        return buckets;
    }

}
