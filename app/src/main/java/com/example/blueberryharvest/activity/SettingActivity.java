package com.example.blueberryharvest.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.presenter.SettingPresenter;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.example.blueberryharvest.activity.MainActivity.verifyStoragePermissions;

public class SettingActivity extends AppCompatActivity {
    private SettingPresenter presenter;
    private Dialog myDialog;
    private TextView backupTextView;
    private TextView exportTextView;
    private TextView dateTextView;
    private TextView totalTextView;
    private TextView cherrySettingTextview;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            presenter = new SettingPresenter(this);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        this.myDialog = new Dialog(this);
        backupTextView = (TextView) findViewById(R.id.backup_textview);
        exportTextView = (TextView) findViewById(R.id.export_textview);
        dateTextView = (TextView) findViewById(R.id.date_textview);
        totalTextView = (TextView) findViewById(R.id.total_textview);
        cherrySettingTextview = findViewById(R.id.cherry_setting_textview);
        LinearLayout backupLayout = (LinearLayout) findViewById(R.id.backup_linear);
        LinearLayout exportLayout = (LinearLayout) findViewById(R.id.export_linear);
        Button backupButton = (Button) findViewById(R.id.backup_button);
        Button exportButton = (Button) findViewById(R.id.export_button);
        Button changeCherrySettingButton = (Button) findViewById(R.id.change_cherry_setting_button);
        final Button deletePickerButton = (Button) findViewById(R.id.delete_picker_button);
        this.date = "";

        if(Build.VERSION.SDK_INT > 25) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate now = LocalDate.now();
            this.date = dtf.format(now);
        }
        String tmp = "Fruit Type: " + presenter.getCherrySetting();
        cherrySettingTextview.setText(tmp);
        updateBackgroud();
        dateTextView.setText(this.date);
        String email = presenter.getBackupEmail();
        if(email == null) {
            email = "";
        }
        backupTextView.setText(email);
        email = presenter.getExportEmail();
        if(email == null) {
            email = "";
        }
        exportTextView.setText(email);

        backupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail("backup");
            }
        });

        exportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail("export");
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });

        changeCherrySettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("activity", presenter.getCherrySetting());
                presenter.switchCherrySetting();
                String tmp = "Fruit Type: " + presenter.getCherrySetting();
                cherrySettingTextview.setText(tmp);
                updateBackgroud();
            }
        });

        deletePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicker();
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter.getExportEmail() == null) {
                    Toast.makeText(getApplicationContext(), "No Email given to send to", Toast.LENGTH_SHORT).show();
                }
                else {
                    export();
                }
            }
        });

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter.getBackupEmail() == null) {
                    Toast.makeText(getApplicationContext(), "No Email given to send to", Toast.LENGTH_SHORT).show();
                }
                else {
                    backup();
                }
            }
        });

        final AutoCompleteTextView nameidEditView = (AutoCompleteTextView) findViewById(R.id.name_editview);
        final ArrayList<String> pickerNamesID = presenter.getAllPickers();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,pickerNamesID);
        nameidEditView.setDropDownHeight(500);
        nameidEditView.setThreshold(1);
        nameidEditView.setAdapter(adapter);
    }

    private void updateEmail(final String type) {
        this.myDialog.setContentView(R.layout.update_email);
        final EditText emailEditText = (EditText) myDialog.findViewById(R.id.email_editview);
        Button updateButton = (Button) myDialog.findViewById(R.id.update_email_button);
        Button cancelButton = (Button) myDialog.findViewById(R.id.cancel_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = emailEditText.getText().toString();
                if(tmp.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nothing to update", Toast.LENGTH_LONG).show();
                }
                else if(type.equals("backup")) {
                    presenter.updateBackupEmail(tmp);
                    myDialog.dismiss();
                    backupTextView.setText(tmp);
                }
                else {
                    presenter.updateExportEmail(tmp);
                    myDialog.dismiss();
                    exportTextView.setText(tmp);
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

    private void deletePicker() {
        final AutoCompleteTextView nameidEditView = (AutoCompleteTextView) findViewById(R.id.name_editview);
        final ArrayList<String> pickerNamesID = presenter.getAllPickers();

        String nameID = nameidEditView.getText().toString();
        if(pickerNamesID.contains(nameID)) {
            myDialog.dismiss();
            helpDelete(nameID);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Select a Valid Picker", Toast.LENGTH_SHORT).show();
        }
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
                myDialog.dismiss();
                String tmp = "Total Pounds Today: " + presenter.getTotalDayPounds(date);
                totalTextView.setText(tmp);
            }
        });
        myDialog.show();
    }

    private void export() {
        verifyStoragePermissions(SettingActivity.this);
        String csv = presenter.export(date);
        File file = new File(csv);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.putExtra(Intent.EXTRA_EMAIL, new String[] { presenter.getExportEmail() });
        i.putExtra(Intent.EXTRA_SUBJECT,presenter.getCherrySetting() + " totals for " + date);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.blueberryharvest.myprovider", file);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("message/rfc822");

        startActivity(Intent.createChooser(i, "Send Records..."));
    }

    private void backup() {
        verifyStoragePermissions(SettingActivity.this);
        String csv = presenter.backup(date);
        File file = new File(csv);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.putExtra(Intent.EXTRA_EMAIL, new String[]{presenter.getBackupEmail()});
        i.putExtra(Intent.EXTRA_SUBJECT, presenter.getCherrySetting() + " backups for " + date);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.blueberryharvest.myprovider", file);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("message/rfc822");

        startActivity(Intent.createChooser(i, "Send Records..."));
    }

    private void updateBackgroud() {
        presenter.cleanFolders();
        String line = "Total Pounds Today: " + presenter.getTotalDayPounds(date);
        totalTextView.setText(line);
        String type = presenter.getCherrySetting();
        RelativeLayout tmp = findViewById(R.id.everything);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        LinearLayout backupLinear = findViewById(R.id.backup_linear);
        LinearLayout exportLinear = findViewById(R.id.export_linear);
        LinearLayout deleteLinear = findViewById(R.id.delete_picker_linear);
        LinearLayout cherryLinear = findViewById(R.id.cherry_setting_linear);
        if(type.equals("cherry")) {
            setTitle("Cherry Harvest");
            tmp.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCherry));
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCherry));
            backupLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightCherry));
            exportLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightCherry));
            deleteLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightCherry));
            cherryLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightCherry));
        }
        else if(type.equals("blueberry")) {
            setTitle("Blueberry Harvest");
            tmp.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBerry));
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBerry));
            backupLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightBerry));
            exportLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightBerry));
            deleteLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightBerry));
            cherryLinear.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightBerry));
        }
    }

}
