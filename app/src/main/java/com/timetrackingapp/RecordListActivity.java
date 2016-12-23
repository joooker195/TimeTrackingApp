package com.timetrackingapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.timetrackingapp.adapter.RecordAdapter;
import com.timetrackingapp.classes.Record;
import com.timetrackingapp.db.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordListActivity extends AppCompatActivity {

    private ListView mListRecord;
    private SQLiteDatabase database;
    private DbUtils utils;
    private List<Record> allRecords = new ArrayList<>();
    private RecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        setTitle("Отметки времени");

        mListRecord = (ListView) findViewById(R.id.record_list_view);
        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();//дает бд на запись
        // utils.initTimeTable(null,database);//забиваю бд данными
        allRecords = utils.getRecords(database);
        adapter = new RecordAdapter(this,R.layout.record_row,allRecords);
      //  mListRecord.setOnItemClickListener(RecordListActivity.this);
        mListRecord.setAdapter(adapter);

    }
}
