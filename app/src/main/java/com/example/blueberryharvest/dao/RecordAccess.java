package com.example.blueberryharvest.dao;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordAccess {
    public RecordAccess() {
    }

    public void create() throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String create = "create table if not exists Records (pickerID int not null, " +
                    "date text not null, CONSTRAINT ID_Date PRIMARY KEY (pickerID,date))";
            PreparedStatement stmt = connection.prepareStatement(create);
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("record access activity", sx.toString());
        }
    }

    public void insert(int pickerID, String date) throws SQLException {
        try {
            Database db = new Database();
            Connection connection = db.getConnection();

            String insert = "insert into Records values (?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(insert);
            stmt.setInt(1, pickerID);
            stmt.setString(2, date);
            stmt.executeUpdate();
            stmt.close();
            db.closeConnection();
        }
        catch(SQLException sx) {
            Log.d("record access activity", sx.toString());
        }
    }
}
