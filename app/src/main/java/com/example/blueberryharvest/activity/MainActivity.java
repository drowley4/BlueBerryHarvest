package com.example.blueberryharvest.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Picker;
import com.example.blueberryharvest.presenter.MainPresenter;
import com.example.blueberryharvest.uihelp.PickerAdapter;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainPresenter presenter;
    private Dialog myDialog;
    private List<Picker> pickers;
    private PickerAdapter pickerAdapter;
    private String date;
    private TextView totalPoundsView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Log.d("main activity", "onCreate() called");
        try {
            this.presenter = new MainPresenter(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.myDialog = new Dialog(this);
        Button addPickerButton = (Button) findViewById(R.id.add_picker_button);
        Button addBucketButton = (Button) findViewById(R.id.add_bucket_button);
        dateTextView = (TextView) findViewById(R.id.date_textview);
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



        pickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String finalDate = date;
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
                changeDate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void changeDate() {
        this.myDialog.setContentView(R.layout.change_date);
        Button changeDateButton = (Button) this.myDialog.findViewById(R.id.change_date_button);
        final TextView dTextView = (TextView) this.myDialog.findViewById(R.id.date_textview);
        CalendarView calendarView = (CalendarView) this.myDialog.findViewById(R.id.calender_view);
        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            date = dtf.format(now);
            dTextView.setText(date);
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String m = month+1 + "";
                String d = dayOfMonth + "";
                if(d.length() == 1) {
                    d = "0" + d;
                }
                if(m.length() == 1) {
                    m = "0" + m;
                }
                date = m + "/" + d + "/" + year;
                dTextView.setText(date);
            }
        });

        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTextView.setText(date);
                updatePickers();
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    private void addBucket() {
        this.myDialog.setContentView(R.layout.add_bucket);
        Button addBucketButton = (Button) this.myDialog.findViewById(R.id.add_bucket_button);
        Button cancelButton = (Button) this.myDialog.findViewById(R.id.cancel_bucket_button);
        final AutoCompleteTextView nameidEditView = (AutoCompleteTextView) this.myDialog.findViewById(R.id.name_editview);
        final ArrayList<String> pickerNamesID = presenter.getAllPickers();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,pickerNamesID);
        nameidEditView.setDropDownHeight(500);
        nameidEditView.setThreshold(1);
        nameidEditView.setAdapter(adapter);
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
                String nameID = nameidEditView.getText().toString();
                int weightIndex = weightPicker.getValue();
                if(pickerNamesID.contains(nameID)) {
                    boolean test = presenter.addBucket(nameID, date, Float.parseFloat(numbers[weightIndex]));
                    myDialog.dismiss();
                    if(test) {
                        updatePickers();
                        Toast.makeText(getApplicationContext(), "Bucket Added", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Bucket Add Failed", Toast.LENGTH_LONG).show();
                    }
                    myDialog.dismiss();
                    updatePickers();
                    Toast.makeText(getApplicationContext(), "Bucket Added", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Select a Valid Picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        this.myDialog.show();
    }

    private void addPicker() {
        this.myDialog.setContentView(R.layout.add_picker);
        TextView idView = (TextView) myDialog.findViewById(R.id.id_textview);
        final EditText nameView = (EditText) myDialog.findViewById(R.id.name_editview);
        Button addPickerButton = (Button) myDialog.findViewById(R.id.add_picker_button);
        Button cancelButton = (Button) myDialog.findViewById(R.id.cancel_picker_button);

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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        this.myDialog.show();
    }

    public void updatePickers() {
        String totalPounds = "Total Pounds Today: " + this.presenter.getTotalDayPounds(this.date);
        this.totalPoundsView.setText(totalPounds);
        this.pickers.clear();
        this.pickers.addAll(this.presenter.getPickers(this.date));
        this.pickerAdapter.date = this.date;
        this.pickerAdapter.notifyDataSetChanged();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1
            );
        }
    }

}
