package com.example.blueberryharvest.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Picker;
import com.example.blueberryharvest.presenter.MainPresenter;
import com.example.blueberryharvest.uihelp.PickerAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainPresenter presenter;
    private Dialog myDialog;
    private List<Picker> pickers;
    private PickerAdapter pickerAdapter;
    private String date;
    private TextView totalPoundsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("main activity", "onCreate() called");
        this.presenter = new MainPresenter();

        this.myDialog = new Dialog(this);
        Button addPickerButton = (Button) findViewById(R.id.add_picker_button);
        Button addBucketButton = (Button) findViewById(R.id.add_bucket_button);
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        this.totalPoundsView = (TextView) findViewById(R.id.total_day_pounds);

        this.date = "";

        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            this.date = dtf.format(now);
        }
        dateTextView.setText(this.date);
        String totalPounds = "Total Pounds Today: " + this.presenter.getTotalDayPounds(this.date);
        this.totalPoundsView.setText(totalPounds);

        this.pickers = this.presenter.getPickers(date);
        this.pickerAdapter = new PickerAdapter(getApplicationContext(), pickers, date);
        final ListView pickerListView = (ListView) findViewById(R.id.picker_list);
        pickerListView.setAdapter(pickerAdapter);


        final String finalDate = date;
        pickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idView = view.findViewById(R.id.id_textview);
                String  pickerID = idView.getText().toString();
                pickerID = pickerID.substring(4);
                TextView nameView = view.findViewById(R.id.name_textview);
                String pickerName = nameView.getText().toString();
                pickerName = pickerName.substring(6);
                Intent i = new Intent(getApplicationContext(), PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", Integer.parseInt(pickerID));
                bundle.putString("date", finalDate);
                bundle.putString("name", pickerName);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        addPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicker();
            }
        });

        addBucketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBucket();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "changing date feature is not yet ready", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBucket() {
        this.myDialog.setContentView(R.layout.add_bucket);
        Button addBucketButton = (Button) this.myDialog.findViewById(R.id.add_bucket_button);
        final EditText nameidEditView = (EditText) this.myDialog.findViewById(R.id.name_editview);

        final String[] numbers = new String[32];
        double num = 0.25;
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = Double.toString(num);
            num += 0.25;
        }
        final NumberPicker weightPicker = (NumberPicker) this.myDialog.findViewById(R.id.weight_picker);
        weightPicker.setMaxValue(numbers.length-1);
        weightPicker.setMinValue(0);
        weightPicker.setDisplayedValues(numbers);
        weightPicker.setWrapSelectorWheel(true);

        addBucketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nameid = nameidEditView.getText().toString();
                int weightIndex = weightPicker.getValue();
                presenter.addBucket(Integer.valueOf(nameid), date, Double.valueOf(numbers[weightIndex]));
                myDialog.dismiss();
                updatePickers();
                Toast.makeText(getApplicationContext(), "Bucket Added", Toast.LENGTH_SHORT).show();
            }
        });

        this.myDialog.show();
    }


    private void addPicker() {
        this.myDialog.setContentView(R.layout.add_picker);
        TextView idView = (TextView) myDialog.findViewById(R.id.id_textview);
        final EditText nameView = (EditText) myDialog.findViewById(R.id.name_editview);
        Button addPickerButton = (Button) myDialog.findViewById(R.id.add_picker_button);

        String id = "ID: " + this.presenter.getNextID();
        idView.setText(id);

        addPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameView.getText().toString();
                presenter.addPicker(name);
                myDialog.dismiss();
                updatePickers();
            }
        });

        this.myDialog.show();
    }

    public void updatePickers() {
        String totalPounds = "Total Pounds Today: " + this.presenter.getTotalDayPounds(this.date);
        this.totalPoundsView.setText(totalPounds);
        this.pickers = this.presenter.getPickers(this.date);
        this.pickerAdapter.notifyDataSetChanged();
    }

}
