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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.timetrackingapp.db.DbUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordActivity extends AppCompatActivity implements Comparable{

    public static String TITLE = "";

    private TextView mTitle;
    private EditText mBegin;
    private EditText mEnd;
    private EditText mDesc;
    private Button mAddButton;
    private TimePicker mTimePicker;

    private DbUtils utils;
    private SQLiteDatabase database;

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private Calendar fromCalendar;
    private Calendar toCalendar;

    long begin;
    long end;

    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mTitle = (TextView) findViewById(R.id.cat_title);
        mBegin = (EditText) findViewById(R.id.time_begin);
        mEnd = (EditText) findViewById(R.id.time_end);
        mDesc = (EditText) findViewById(R.id.add_desc);
        mAddButton = (Button) findViewById(R.id.add_record_button);

        mTitle.setText(TITLE);

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


    }

    public void onAddData(View view){
     /*   startHour = startTime.getCurrentHour();
        startMinute = startTime.getCurrentMinute();
        String startTimeStr = startHour+":"+startMinute;
        endHour = endTime.getCurrentHour();
        endMinute = endTime.getCurrentMinute();
        String  description = descriptionEdit.getText().toString();
        String segment = segmentEdit.getText().toString();
        validate(segment);
        TimeRecord newDaata = new TimeRecord(111,111,description,selectedCategory,selectedListPhotos,segment);
        utils.insertTimeRecord(database,newDaata);
        intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();*/


        String title = mTitle.getText().toString();
     //   utils.insertCategories(database,new Category(title, ""));
        Intent intent = new Intent();
        //intent.putExtra("cat",new Category(title, ""));
        setResult(RESULT_OK, intent);
        finish();
    }


    private void onClickTimePicker(final EditText editTime) {

        LayoutInflater layoutInflater = LayoutInflater.from(RecordActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.time_picker_activity, null);
        mTimePicker = (TimePicker) promptView.findViewById(R.id.tp);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecordActivity.this);
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

    @Override
    public int compareTo(Object o) {
        return 0;
    }
    private static long parseTime(int timeIntHour, int timeIntMinute) throws ParseException {
        String timeHour = String.valueOf(timeIntHour);
        String timeMinute = String.valueOf(timeIntMinute);
        String time = timeHour + ":" + timeMinute;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.parse(time).getTime();
    }
}
