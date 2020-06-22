package com.example.blueberryharvest.data;


public class Bucket {
    private double weight;
    private String time;
    private String date;
    private String variety;


    public Bucket(double weight, String time, String date, String variety) {
        this.weight = weight;
        this.time = time;
        this.date = date;
        this.variety = variety;
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

    public String getVariety() {
        return variety;
    }
    public void setDate(String date) {
        this.date = date;
    }

}
