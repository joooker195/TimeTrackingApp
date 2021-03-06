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
import android.view.Menu;
import android.view.MenuItem;
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

public class AddRecordActivity extends AppCompatActivity implements Comparable{

    public static String TITLE = "";

    private TextView mTitle;
    private EditText mBeginTime;
    private EditText mEndTime;
    private EditText mDateBegin;
    private EditText mDateEnd;
    private EditText mDesc;
    private Button mAddButton;
    private TimePicker mTimePicker;
    private Spinner mPhotoSpinner;

    private DbUtils utils;
    private SQLiteDatabase database;

    private List<Photo> allPhoto = new ArrayList<>();
    private List<Photo> selectedListPhotos = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private CustomPhotoAdapter customPhotoAdapter;
    private Photo selectedPhoto;

    private long begin;
    private long end;

    private int idCategoryRef;

    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        mTitle = (TextView) findViewById(R.id.cat_title);
        mBeginTime = (EditText) findViewById(R.id.time_begin);
        mEndTime = (EditText) findViewById(R.id.time_end);
        mDateBegin = (EditText) findViewById(R.id.date_begin);
        mDateEnd = (EditText) findViewById(R.id.date_end);
        mDesc = (EditText) findViewById(R.id.add_desc);
        mAddButton = (Button) findViewById(R.id.add_record_button);
        mPhotoSpinner = (Spinner) findViewById(R.id.photoSpinner);

        mTitle.setText(TITLE);

        mBeginTime.setInputType(InputType.TYPE_NULL);
        mEndTime.setInputType(InputType.TYPE_NULL);

        mBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBeginTime.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mBeginTime);
            }
        });

        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEndTime.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onClickTimePicker(mEndTime);
            }
        });

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        allPhoto = utils.getAllPhoto(database);

        customPhotoAdapter = new CustomPhotoAdapter(AddRecordActivity.this, R.layout.content_photo, allPhoto);
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

    public void addData() throws ParseException {

        String title = mTitle.getText().toString();
        String desc = mDesc.getText().toString();

        idCategoryRef = utils.getIdCategoryByName(title, database);

        long interval = getInterval(begin,end);

        utils.insertRecord(database, new Record(desc, interval, begin, end, idCategoryRef, "", selectedListPhotos));
        Intent intent = new Intent();
        intent.putExtra("rec",new Record(desc, interval, begin, end, idCategoryRef));
        setResult(RESULT_OK, intent);
        finish();
    }


    private void onClickTimePicker(final EditText editTime) {

        LayoutInflater layoutInflater = LayoutInflater.from(AddRecordActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.content_time_picker_activity, null);
        mTimePicker = (TimePicker) promptView.findViewById(R.id.tp);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddRecordActivity.this);
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

                                        begin = parseTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), mDateBegin.getText().toString());
                                    }
                                    else
                                    {
                                        end = parseTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), mDateEnd.getText().toString());
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

    private static long parseTime(int timeIntHour, int timeIntMinute, String date) throws ParseException {
        String timeHour = String.valueOf(timeIntHour);
        String timeMinute = String.valueOf(timeIntMinute);
        String time = date + " " + timeHour + ":" + timeMinute;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
        return simpleDateFormat.parse(time).getTime();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.category_del) {
            String title = mTitle.getText().toString();

            categories = utils.getAllCategories(database);
            for(Category c: categories)
            {
                if(c.getTitle().equals(title)) {
                    utils.deleteCascadeCategory(database, c);
                    break;
                }
            }
            Intent intent= new Intent(AddRecordActivity.this, AddRecordActivity.class);
            intent.removeExtra("cat");
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public long getInterval(long begin, long end)
    {
        long interval;
        SimpleDateFormat sH = new SimpleDateFormat("HH");
        SimpleDateFormat sM = new SimpleDateFormat("mm");
        SimpleDateFormat sMM = new SimpleDateFormat("MM");
        SimpleDateFormat sD = new SimpleDateFormat("dd");
        SimpleDateFormat sY = new SimpleDateFormat("yy");


        Date dBegin = new Date(begin);
        Date dEnd = new Date(end);

        String beginH = sH.format(dBegin);
        String endH = sH.format(dEnd);
        String beginM = sM.format(dBegin);
        String endM = sM.format(dEnd);
        String beginMM = sMM.format(dBegin);
        String endMM = sMM.format(dEnd);
        String beginD = sD.format(dBegin);
        String endD = sD.format(dEnd);
        String beginY = sY.format(dBegin);
        String endY = sY.format(dEnd);

        long yy = Integer.parseInt(endY) - Integer.parseInt(beginY);
        if(yy<0)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }


        long mM = Integer.parseInt(endMM) - Integer.parseInt(beginMM);
        if(mM<0 && yy==0)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }

        long dd = Integer.parseInt(endD) - Integer.parseInt(beginD);
        if(dd<0 && mM==0 && yy==0)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }

        int h = Integer.parseInt(endH) - Integer.parseInt(beginH);
        if(h<0 && dd==0 && mM==0 && yy==0)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }

        long m = Integer.parseInt(endM) - Integer.parseInt(beginM);
        if(m<0 && dd==0 && mM==0 && h==0 && yy==0)
        {
            Toast toast = Toast.makeText(this, "Не верно введено время", Toast.LENGTH_LONG);
            toast.show();
        }


        //     interval = h*60+m;
        interval = yy*365*24*60 + mM*30*24*60 + dd*24*60 + h*60 + m;

        return interval;
    }
}
