package com.example.blueberryharvest.dao;

import android.util.Log;

import com.example.blueberryharvest.data.Bucket;
import com.example.blueberryharvest.data.Picker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PickerAccess {
    public PickerAccess() {
    }

    public void create() throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String create = "create table if not exists picker (pickerID int not null primary key, " +
                    "name text not null)";
            PreparedStatement stmt = connection.prepareStatement(create);
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("picker access activity", sx.toString());
        }
    }

    public void insert(Picker picker) throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String insert = "insert into bucket values (?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(insert);
            stmt.setInt(1, picker.getNumID());
            stmt.setString(2, picker.getName());
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("picker access activity", sx.toString());
        }
    }

}
