package com.example.blueberryharvest.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection = null;

    public Database() {

    }

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:harvestdatabase.db");
        }
        catch(SQLException ex) {
            System.out.println("Unable to connection to database");
        }
        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
            connection = null;
        }
        catch(SQLException ex) {
            System.out.println("Unable to close connection");
        }
    }
}
