package com.timetrackingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.timetrackingapp.classes.Category;
import com.timetrackingapp.classes.PieData;
import com.timetrackingapp.classes.Record;
import com.timetrackingapp.db.DbUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StatisticActivity extends AppCompatActivity {

    private EditText begin;
    private EditText end;
    private ListView mList;

    private PieChart graficoPartidos;
    private SparseBooleanArray checked;
    private SQLiteDatabase database;
    private DbUtils utils;
    private ArrayAdapter<Object> adapter;
    private List<Category> allCategories = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private DateFormat MM = new SimpleDateFormat("MM");
    private int beginMonth;
    private int beginYear;
    private int endMonth;
    private int endYear;
    private DateFormat yy = new SimpleDateFormat("yyyy");
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setTitle("Статистика");

        mList = (ListView) findViewById(R.id.listView);
        graficoPartidos = (PieChart) findViewById(R.id.asdf);
        graficoPartidos.getBackgroundPaint().setColor(Color.WHITE);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();


        allCategories = utils.getAllCategories(database);
        viewList();
        drawPie();


    }

    public void viewList()
    {
        List<Record> records = utils.getRecords(database);
        Map<String, Integer> mapCount  = new HashMap<>();

        Object[] title;
        Object[] count;
        String t;
        Integer c;


        for(Record r: records)
        {
            int u = Integer.parseInt(String.valueOf(r.getInterval()));
            if(flag && Integer.parseInt(MM.format(new Date(r.getBegin())))==01)
            {
                if(mapCount.get(r.getCategoryTitle())==null)
                {
                    mapCount.put(r.getCategoryTitle(), u);
                }
                else {
                    mapCount.put(r.getCategoryTitle(), mapCount.get(r.getCategoryTitle()) + u);
                }
            }
            else
            {
                if(!flag) {
                    if (mapCount.get(r.getCategoryTitle()) == null) {
                        mapCount.put(r.getCategoryTitle(), u);
                    } else {
                        mapCount.put(r.getCategoryTitle(), mapCount.get(r.getCategoryTitle()) + u);
                    }
                }
            }
        }

        title = mapCount.keySet().toArray();
        count = mapCount.values().toArray();

        for(int i =mapCount.size()-1; i>=0; i--)
        {
            for(int j=0; j<i; j++)
            {
                if((Integer)count[j+1] >= (Integer) count[j])
                {
                    c = (Integer) count[j+1];
                    count[j+1] = count[j];
                    count[j] = c;

                    t = (String) title[j+1];
                    title[j+1] = title[j];
                    title[j] = t;
                }
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, title);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mList.setAdapter(adapter);
    }


    private void drawPie() {
        try{
            Random random = new Random();
            ArrayList<PieData> times = new ArrayList<>();
            for (Category category:allCategories){
                times.add(new PieData(category.getTitle(),utils.pieData(database,category)));
            }
            for (PieData pieData:times){
                if (pieData.getTime()!=0){//убираю сегменты с нулевыми отрезками, дабы не загромождать подпись
                    Segment segment = new Segment(pieData.getCategory(),pieData.getTime());
                    graficoPartidos.addSeries(segment, new SegmentFormatter(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), Color.BLACK,Color.BLACK, Color.BLACK));
                }
            }
            PieRenderer pieRenderer = graficoPartidos.getRenderer(PieRenderer.class);
            pieRenderer.setDonutSize((float) 0 / 100,   PieRenderer.DonutMode.PERCENT);
        }
        catch (NullPointerException e){
            Toast toast = Toast.makeText(this,"Нет данных для построения круговой диаграммы",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void showStatisticRecord()
    {
        checked = mList.getCheckedItemPositions();
        List<String> selectItem = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectItem.add( (String) adapter.getItem(position));
        }

        StatisticRecordActivity.SELECT_ITEM = selectItem;
        Intent intent = new Intent(StatisticActivity.this, StatisticRecordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statistic_settings) {
            showStatisticRecord();
        }
        if(id == R.id.time_settings)
        {
            flag = true;
            beginMonth = endMonth = 1;
            beginYear = endYear = 2017;
            viewList();
        }
        if(id == R.id.all_time_settings)
        {
            flag = false;
            viewList();
        }
        if(id== R.id.date_settings)
        {

        }

        return super.onOptionsItemSelected(item);
    }
}
