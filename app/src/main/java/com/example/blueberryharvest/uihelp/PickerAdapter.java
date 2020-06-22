package com.example.blueberryharvest.uihelp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Picker;


import java.util.List;

public class PickerAdapter extends ArrayAdapter<Picker> {
    public String date;

    public PickerAdapter(Context context, List<Picker> pickers, String date) {
        super(context, 0, pickers);
        this.date = date;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Picker picker = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_picker, parent, false);
        }
        TextView idView = (TextView) convertView.findViewById(R.id.id_textview);
        TextView nameView = (TextView) convertView.findViewById(R.id.name_textview);
        TextView poundsView = (TextView) convertView.findViewById(R.id.total_pounds_textview);

        String id = "ID: " + picker.getNumID() + "";
        idView.setText(id);
        String name = "Name: " + picker.getName();
        nameView.setText(name);
        String totalPounds = "Total Pounds: " +  picker.getRecord(this.date).getTotalPounds() + "";
        poundsView.setText(totalPounds);

        return convertView;
    }
}
