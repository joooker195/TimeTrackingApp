package com.timetrackingapp;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StatisticActivity extends AppCompatActivity {

    private EditText begin;
    private EditText end;
    private ListView mList;

    private PieChart graficoPartidos;
    private SQLiteDatabase database;
    private DbUtils utils;
    private ArrayAdapter<Object> adapter;
    private List<Category> allCategories = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setTitle("Статистика");

        begin = (EditText) findViewById(R.id.time_begin_statistic);
        end = (EditText) findViewById(R.id.time_end_statistic);
        mList = (ListView) findViewById(R.id.listView);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();
       // allCategories = utils.parseCursor(utils.getAllRecords(database,DbUtils.CATEGORY_TABLE));
        allCategories = utils.getAllCategories(database);
      /*  for (int i=0; i<allCategories.size(); i++)
        {

            titles.add(i,allCategories.get(i).getTitle());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mList.setAdapter(adapter);*/
//        drawPie();
        viewList();

    }

    public void viewList()
    {
        List<Record> records = utils.getRecords(database);
        Map<String, Integer> mapCount  = new HashMap<>();

        Object[] title;
        Object[] count;
        String t;
        Integer c;

        for(int i=0; i<records.size(); i++)
        {
            if(mapCount.get(records.get(i).getCategoryTitle()) == null)
            {
                mapCount.put(records.get(i).getCategoryTitle(), 0);
            }
            else {
                mapCount.put(records.get(i).getCategoryTitle(), mapCount.get(records.get(i).getCategoryTitle()) + 1);
            }
          //  mapTitle.put(records.get(i).getCategoryTitle(), records.get(i).getCategoryTitle());
        }
        title = mapCount.keySet().toArray();
        count = mapCount.values().toArray();

        for(int i =0; i<mapCount.size(); i++)
        {
            for(int j=mapCount.size()-1; j<i; j++)
            {
                if((Integer)count[j-1] > (Integer) count[j])
                {
                    c = (Integer) count[j-1];
                    count[j-1] = count[j];
                    count[j] = c;

                    t = (String) title[j-1];
                    title[j-1] = title[j];
                    title[j] = t;
                }
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, title);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
