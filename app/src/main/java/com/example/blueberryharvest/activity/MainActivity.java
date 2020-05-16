package com.example.blueberryharvest.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

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
        Log.d("main activity", "onCreate() called");
        try {
            this.presenter = new MainPresenter(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.myDialog = new Dialog(this);
        Button addPickerButton = (Button) findViewById(R.id.add_picker_button);
        Button addBucketButton = (Button) findViewById(R.id.add_bucket_button);
        Button deletePickerButton = (Button) findViewById(R.id.delete_picker_button);
        dateTextView = (TextView) findViewById(R.id.date_textview);
        this.totalPoundsView = (TextView) findViewById(R.id.total_day_pounds);
        Button backupButton = (Button) findViewById(R.id.backup_button);
        Button exportButton = (Button) findViewById(R.id.export_button);

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

        deletePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicker();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(MainActivity.this);
                String csv = presenter.backup(date);
                /*File file = new File(csv);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                i.putExtra(Intent.EXTRA_EMAIL, new String[] { "ddonrowley@gmail.com" });
                i.putExtra(Intent.EXTRA_SUBJECT,"sub");
                i.putExtra(Intent.EXTRA_TEXT, "body");
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.blueberryharvest.myprovider", file);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.setType("message/rfc822");

                startActivity(Intent.createChooser(i, "Send feedback..."));*/
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(MainActivity.this);
                String csv = presenter.export(date);
                File file = new File(csv);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                i.putExtra(Intent.EXTRA_EMAIL, new String[] { "ddonrowley@gmail.com" });
                i.putExtra(Intent.EXTRA_SUBJECT,"Blueberry totals for " + date);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.blueberryharvest.myprovider", file);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.setType("message/rfc822");

                startActivity(Intent.createChooser(i, "Send Records..."));
            }
        });

    }

    private void changeDate() {
        this.myDialog.setContentView(R.layout.change_date);
        Button changeDateButton = (Button) this.myDialog.findViewById(R.id.change_date_button);
        final TextView dTextView = (TextView) this.myDialog.findViewById(R.id.date_textview);
        CalendarView calendarView = (CalendarView) this.myDialog.findViewById(R.id.calender_view);

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
                    presenter.addBucket(nameID, date, Float.parseFloat(numbers[weightIndex]));
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

    private void deletePicker() {
        this.myDialog.setContentView(R.layout.delete_picker);
        Button deletebutton = (Button) this.myDialog.findViewById(R.id.delete_button);
        Button cancelbutton = (Button) this.myDialog.findViewById(R.id.cancel_picker_button);
        final AutoCompleteTextView nameidEditView = (AutoCompleteTextView) this.myDialog.findViewById(R.id.name_editview);
        final ArrayList<String> pickerNamesID = presenter.getAllPickers();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,pickerNamesID);
        nameidEditView.setDropDownHeight(500);
        nameidEditView.setThreshold(1);
        nameidEditView.setAdapter(adapter);

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameID = nameidEditView.getText().toString();
                if(pickerNamesID.contains(nameID)) {
                    myDialog.dismiss();
                    helpDelete(nameID);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Select a Valid Picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        this.myDialog.show();
    }

    private void helpDelete(final String nameid) {
        this.myDialog.setContentView(R.layout.delete_dialog_box);
        TextView nameTextView = this.myDialog.findViewById(R.id.first_textview);
        Button yesButton = this.myDialog.findViewById(R.id.yes_button);
        Button noButton = this.myDialog.findViewById(R.id.no_button);
        nameTextView.setText(nameid);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter.deletePicker(nameid)) {
                    Toast.makeText(myDialog.getContext(), "Picker Deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(myDialog.getContext(), "Picker Not Deleted", Toast.LENGTH_SHORT).show();
                }
                myDialog.dismiss();
                updatePickers();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
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
