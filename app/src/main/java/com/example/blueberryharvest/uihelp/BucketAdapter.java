package com.example.blueberryharvest.uihelp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Bucket;

import org.w3c.dom.Text;

import java.util.List;

public class BucketAdapter extends ArrayAdapter<Bucket> {

    public BucketAdapter(Context context, List<Bucket> buckets) {
        super(context, 0, buckets);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bucket bucket = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bucket, parent, false);
        }

        TextView timeView = convertView.findViewById(R.id.time_textview);
        TextView poundsView = convertView.findViewById(R.id.pounds_textview);

        String time = "Time: " + bucket.getTime();
        timeView.setText(time);
        String pounds = "Weight: " + bucket.getWeight() + " lbs";
        poundsView.setText(pounds);

        return convertView;
    }
}
