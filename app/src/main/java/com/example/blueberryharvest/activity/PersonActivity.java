package com.example.blueberryharvest.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blueberryharvest.R;
import com.example.blueberryharvest.data.Bucket;
import com.example.blueberryharvest.presenter.PersonPresenter;
import com.example.blueberryharvest.uihelp.BucketAdapter;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.blueberryharvest.activity.MainActivity.verifyStoragePermissions;

public class PersonActivity extends AppCompatActivity {
    private PersonPresenter presenter;
    private Dialog myDialog;
    private List<Bucket> buckets;
    private int pickerID;
    private String harvestDate;
    private BucketAdapter bucketAdapter;
    private TextView dateTextView;
    private TextView nameView;
    private TextView emailTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Log.d("person activity", "onCreate() called");
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        try {
            presenter = new PersonPresenter(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.myDialog = new Dialog(this);
        TextView idView = (TextView) findViewById(R.id.id_textview);
        this.nameView = (TextView) findViewById(R.id.name_textview);
        Button updateButton = (Button) findViewById(R.id.update_picker_button);
        Button emailButton = (Button) findViewById(R.id.email_button);
        TextView totalTextView = (TextView) findViewById(R.id.total_textview);
        emailTextView = (TextView) findViewById(R.id.email_textview);

        this.dateTextView = (TextView) findViewById(R.id.date_textview);


        Intent intentExtras = getIntent();
        this.pickerID = intentExtras.getIntExtra("id", 0);
        this.harvestDate = intentExtras.getStringExtra("date");
        String pickerName = intentExtras.getStringExtra("name");

        this.buckets = this.presenter.getBuckets(this.pickerID, this.harvestDate);
        this.bucketAdapter = new BucketAdapter(getApplicationContext(), this.buckets);
        final ListView bucketListView = (ListView) findViewById(R.id.bucket_list);
        bucketListView.setAdapter(bucketAdapter);

        double total = 0;
        for(Bucket b: this.buckets) {
            total += b.getWeight();
        }
        totalTextView.setText("Total: " + Double.toString(total));

        String id = "ID: " + this.pickerID + "";
        idView.setText(id);
        String name = "Name: " + pickerName;
        this.nameView.setText(name);
        this.dateTextView.setText(this.harvestDate);
        this.emailTextView.setText("Email: " + presenter.getEmail(pickerID));

        this.dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePicker();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = presenter.getEmail(pickerID);
                if(!email.equals("")) {
                    emailPicker();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No Email given to send to", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bucketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView timeTextView = view.findViewById(R.id.time_textview);
                TextView weightTextView = view.findViewById(R.id.pounds_textview);
                deleteBucket(timeTextView.getText().toString(), weightTextView.getText().toString());
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
            harvestDate = dtf.format(now);
            dTextView.setText(harvestDate);
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
                harvestDate = m + "/" + d + "/" + year;
                dTextView.setText(harvestDate);
            }
        });

        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTextView.setText(harvestDate);
                updateBuckets();
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    private void deleteBucket(String time, String weight) {
        this.myDialog.setContentView(R.layout.delete_dialog_box);
        TextView timeTextView = this.myDialog.findViewById(R.id.first_textview);
        TextView weightTextView = this.myDialog.findViewById(R.id.second_textview);
        Button yesButton = this.myDialog.findViewById(R.id.yes_button);
        Button noButton = this.myDialog.findViewById(R.id.no_button);
        timeTextView.setText(time);
        weightTextView.setText(weight);
        final String tmpTime = time;
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter.deleteBucket(pickerID, tmpTime)) {
                    Toast.makeText(myDialog.getContext(), "Bucket Deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(myDialog.getContext(), "Bucket Not Deleted", Toast.LENGTH_SHORT).show();
                }
                myDialog.dismiss();
                updateBuckets();
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

    private void updateBuckets() {
        this.buckets.clear();
        this.buckets.addAll(presenter.getBuckets(this.pickerID, this.harvestDate));
        this.bucketAdapter.notifyDataSetChanged();
    }

    private void updatePicker() {
        this.myDialog.setContentView(R.layout.update_picker);

        Button updateButton = (Button) this.myDialog.findViewById(R.id.update_picker_button);
        Button cancelButton = (Button) this.myDialog.findViewById(R.id.cancel_picker_button);
        final EditText nameEditText = (EditText) this.myDialog.findViewById(R.id.name_editview);
        final EditText emailEditText = (EditText) this.myDialog.findViewById(R.id.email_editview);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                if(tmp.equals("") && email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nothing to update", Toast.LENGTH_LONG).show();
                }
                else if(presenter.updatePicker(tmp, pickerID, email)) {
                    Toast.makeText(getApplicationContext(), "Picker Updated", Toast.LENGTH_SHORT).show();
                    if(!tmp.equals("")) {
                        String name = "Name: " + tmp;
                        nameView.setText(tmp);
                    }
                    if(!email.equals("")) {
                        emailTextView.setText(email);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Picker Update Failed", Toast.LENGTH_LONG).show();
                }
                myDialog.dismiss();
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

    private void emailPicker() {
        String date = dateTextView.getText().toString();
        verifyStoragePermissions(PersonActivity.this);
        String csv = presenter.email(date, pickerID);
        File file = new File(csv);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.putExtra(Intent.EXTRA_EMAIL, new String[] { presenter.getEmail(pickerID) });
        i.putExtra(Intent.EXTRA_SUBJECT,"Blueberry total for " + date);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.blueberryharvest.myprovider", file);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("message/rfc822");

        startActivity(Intent.createChooser(i, "Send Records..."));
    }
}
