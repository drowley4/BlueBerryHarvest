package com.example.blueberryharvest.data;


import java.util.List;

public class Record {

    private String date;

    private double totalPounds;

    private List<Bucket> buckets;

    Record(String date, List<Bucket> buckets) {
        this.date = date;
        this.buckets = buckets;
        this.sum();
    }

    public double getTotalPounds() {
        return totalPounds;
    }
    private void sum() {
        this.totalPounds = 0;
        for(Bucket b: this.buckets) {
            this.totalPounds += b.getWeight();
        }
    }

    public String getDate() {
        return date;
    }

    List<Bucket> getBuckets() {
        return buckets;
    }


}
