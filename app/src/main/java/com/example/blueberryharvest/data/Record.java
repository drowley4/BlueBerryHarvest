package com.example.blueberryharvest.data;


import java.util.ArrayList;
import java.util.List;

public class Record {

    private String date;

    private double totalPounds;

    private List<Bucket> buckets;

    public Record(String date, List<Bucket> buckets) {
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
