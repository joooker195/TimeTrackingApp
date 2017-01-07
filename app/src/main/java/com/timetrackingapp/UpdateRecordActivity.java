package com.timetrackingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.timetrackingapp.adapter.CustomPhotoAdapter;
import com.timetrackingapp.classes.Category;
import com.timetrackingapp.classes.Photo;
import com.timetrackingapp.classes.Record;
import com.timetrackingapp.db.DbUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdateRecordActivity extends AppCompatActivity implements Comparable {

    public static String DESC = "";

    private TextView mTitle;
    private EditText mBegin;
    private EditText mEnd;
    private EditText mDesc;
    private Button mAddButton;
    private TimePicker mTimePicker;
    private Spinner mPhotoSpinner;

    private DbUtils utils;
    private SQLiteDatabase database;

    private List<Photo> allPhoto = new ArrayList<>();
    private List<Photo> selectedListPhotos = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Record> records = new ArrayList<>();
    private CustomPhotoAdapter customPhotoAdapter;
    private Photo selectedPhoto;
    private Record record;

    private long begin;
    private long end;

    private int idCategoryRef;

    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();

        records = utils.getRecords(database);

        for (Record r : records) {
            if (r.getDesc().equals(DESC)) {
                record = r;
                break;
            }
        }

        mBegin = (EditText) findViewById(R.id.time_begin);
        mEnd = (EditText) findViewById(R.id.time_end);
        mDesc = (EditText) findViewById(R.id.add_desc);
        mAddButton = (Button) findViewById(R.id.add_record_button);
        mPhotoSpinner = (Spinner) findViewById(R.id.photoSpinner);
        mTitle = (TextView) findViewById(R.id.cat_title);

        mTitle.setText(record.getCategoryTitle());

        mBegin.setInputType(InputType.TYPE_NULL);
        mEnd.setInputType(InputType.TYPE_NULL);

        mBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBegin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mBegin);
            }
        });

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEnd.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mEnd);
            }
        });

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        allPhoto = utils.getAllPhoto(database);

        customPhotoAdapter = new CustomPhotoAdapter(UpdateRecordActivity.this, R.layout.content_photo, allPhoto);
        mPhotoSpinner.setAdapter(customPhotoAdapter);


        mPhotoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPhoto = (Photo) adapterView.getItemAtPosition(i);
                selectedListPhotos.add(selectedPhoto);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void updateData()
    {
        record.setDesc(mDesc.getText().toString());
        record.setBegin(begin);
        record.setEnd(end);
        record.setPhotos(allPhoto);
        record.setInterval(getInterval(begin,end));
        utils.updateRecord(DESC, record, database);

        Intent intent = new Intent(UpdateRecordActivity.this, RecordListActivity.class);
        startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }


    private void onClickTimePicker(final EditText editTime) {

        LayoutInflater layoutInflater = LayoutInflater.from(UpdateRecordActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.content_time_picker_activity, null);
        mTimePicker = (TimePicker) promptView.findViewById(R.id.tp);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateRecordActivity.this);
        alertDialogBuilder.setTitle("Выберите время");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                editTime.setText(new StringBuilder()
                                        .append(mTimePicker.getCurrentHour()).append(":")
                                        .append(mTimePicker.getCurrentMinute()));

                                try {
                                    if(flag) {
                                        begin = parseTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
                                    }
                                    else
                                    {
                                        end = parseTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private static long parseTime(int timeIntHour, int timeIntMinute) throws ParseException {
        String timeHour = String.valueOf(timeIntHour);
        String timeMinute = String.valueOf(timeIntMinute);
        String time = timeHour + ":" + timeMinute;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.parse(time).getTime();
    }

    public long getInterval(long begin, long end)
    {
        long interval = 0;
        try {

            SimpleDateFormat sH = new SimpleDateFormat("HH");
            SimpleDateFormat sM = new SimpleDateFormat("mm");

            Date dBegin = new Date(begin);
            Date dEnd = new Date(end);

            String beginH = sH.format(dBegin);
            String endH = sH.format(dEnd);
            String beginM = sM.format(dBegin);
            String endM = sM.format(dEnd);

            long m = Integer.parseInt(endM) - Integer.parseInt(beginM);
            if (m < 0) {
                m = -m;
            }
            int h = Integer.parseInt(endH) - Integer.parseInt(beginH);
            if (h < 0) {
                throw new Exception();
            }
            interval = h * 60 + m;

            return interval;
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }
        finally {
            return interval;
        }
    }
}
