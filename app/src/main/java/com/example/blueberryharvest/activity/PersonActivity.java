package com.example.blueberryharvest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Bucket;
import com.example.blueberryharvest.presenter.PersonPresenter;
import com.example.blueberryharvest.uihelp.BucketAdapter;

import java.sql.SQLException;
import java.util.List;

public class PersonActivity extends AppCompatActivity {
    private PersonPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Log.d("person activity", "onCreate() called");

        try {
            presenter = new PersonPresenter();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TextView idView = (TextView) findViewById(R.id.id_textview);
        TextView nameView = (TextView) findViewById(R.id.name_textview);
        TextView dateView = (TextView) findViewById(R.id.date_textview);

        Intent intentExtras = getIntent();
        int pickerID = intentExtras.getIntExtra("id", 0);
        String harvestDate = intentExtras.getStringExtra("date");
        String pickerName = intentExtras.getStringExtra("name");

        List<Bucket> buckets = presenter.getBuckets(pickerID, harvestDate);
        BucketAdapter bucketAdapter = new BucketAdapter(getApplicationContext(), buckets);
        final ListView bucketListView = (ListView) findViewById(R.id.bucket_list);
        bucketListView.setAdapter(bucketAdapter);

        String id = "ID: " + pickerID + "";
        idView.setText(id);
        String name = "Name: " + pickerName;
        nameView.setText(name);
        dateView.setText(harvestDate);

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "changing date feature is not yet ready", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
