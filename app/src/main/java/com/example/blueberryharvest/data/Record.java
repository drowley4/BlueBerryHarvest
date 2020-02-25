package com.example.blueberryharvest.data;


import java.util.ArrayList;
import java.util.List;

public class Record {

    private String date;

    private double totalPounds;

    private List<Bucket> buckets;

    public Record(String date) {
        this.date = date;
        this.totalPounds = 0;
        this.buckets = new ArrayList<Bucket>();
    }

    public double getTotalPounds() {
        return totalPounds;
    }

    public String getDate() {
        return date;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void insertBucket(Bucket bucket) {
        this.buckets.add(bucket);
        this.totalPounds = this.totalPounds + bucket.getWeight();
    }

    public Boolean removeBucket(Bucket bucket) {
        if(this.buckets.contains(bucket)) {
            this.totalPounds = this.totalPounds - bucket.getWeight();
            this.buckets.remove(bucket);
            return true;
        }
        else {
            return false;
        }
    }
}
