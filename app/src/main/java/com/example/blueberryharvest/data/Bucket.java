package com.example.blueberryharvest.data;


public class Bucket {
    private double weight;
    private String time;
    private String date;
    private int pickerID;


    public Bucket(double weight, String time, String date, int pickerID) {
        this.weight = weight;
        this.time = time;
        this.date = date;
        this.pickerID = pickerID;
    }

    public double getWeight() {
        return weight;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
