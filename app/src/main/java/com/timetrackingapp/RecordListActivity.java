package com.timetrackingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private Record r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        setTitle("Отметки времени");

        mListRecord = (ListView) findViewById(R.id.record_list_view);
        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();//дает бд на запись
        allRecords = utils.getRecords(database);
        adapter = new RecordAdapter(this,R.layout.content_record_row,allRecords);
        mListRecord.setAdapter(adapter);
        mListRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                r = (Record) adapterView.getItemAtPosition(i);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.record_del) {
            utils.deleteRecord(database,r.getDesc());
            allRecords.remove(r);
            adapter.notifyDataSetChanged();
        }
        if (id == R.id.record_update) {

            UpdateRecordActivity.DESC = r.getDesc();

            Intent intent = new Intent(RecordListActivity.this, UpdateRecordActivity.class);
            startActivity(intent);

            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
