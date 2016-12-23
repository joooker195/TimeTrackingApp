package com.timetrackingapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.timetrackingapp.classes.Category;
import com.timetrackingapp.db.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class StatisticRecordActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private DbUtils utils;
    private ArrayAdapter<String> adapter;
    private ListView mList;

    public static List<String> SELECT_ITEM = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_record);

        mList = (ListView) findViewById(R.id.listView2);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();

        List<Category> allCategories = new ArrayList<>();
        allCategories = utils.getAllCategories(database);

        ArrayList<String> result = new ArrayList<>();

        if(SELECT_ITEM.size() == 0)
        {
            for (Category category:allCategories){
                result.add(category.getTitle() +": " + String.valueOf(utils.pieData(database,category)));
            }
        }
        else
        {
            for (Category category:allCategories){
                for(int i=0; i<SELECT_ITEM.size(); i++) {
                    if(SELECT_ITEM.get(i).equals(category.getTitle())) {
                        result.add(category.getTitle() + ": " + String.valueOf(utils.pieData(database, category)));
                    }
                }
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, result);
        mList.setAdapter(adapter);
    }
}
