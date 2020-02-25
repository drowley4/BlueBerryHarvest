package com.example.blueberryharvest.data;


public class Bucket {
    private double weight;
    private String time;
    private String date;


    public Bucket(double weight, String time, String date) {
        this.weight = weight;
        this.time = time;
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
