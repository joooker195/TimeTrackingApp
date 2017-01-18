package com.timetrackingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StatisticActivity extends AppCompatActivity {

    private ListView mList;
    private PieChart mGraficoPartidos;
    private SparseBooleanArray mCheckedPosition;

    private SQLiteDatabase database;
    private DbUtils utils;

    private ArrayAdapter<Object> adapter;
    private List<Category> allCategories = new ArrayList<>();

    private DateFormat mParseMonth = new SimpleDateFormat("MM");
    private DateFormat mParseYear = new SimpleDateFormat("yyyy");
    private int mBeginMonth;
    private int mBeginYear;
    private int mEndMonth;
    private int mEndYear;
    private boolean isMonthTimeFlag = false;
    private boolean isUserTimeFlag = false;
    private boolean isAllTimeFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setTitle("Статистика");

        mList = (ListView) findViewById(R.id.listView);
        mGraficoPartidos = (PieChart) findViewById(R.id.asdf);
        mGraficoPartidos.getBackgroundPaint().setColor(Color.WHITE);

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
            int interval = Integer.parseInt(String.valueOf(r.getInterval()));
            int beginMonth = Integer.parseInt(mParseMonth.format(new Date(r.getBegin())));
            int beginYear = Integer.parseInt(mParseYear.format(new Date(r.getBegin())));
            int endMonth = Integer.parseInt(mParseMonth.format(new Date(r.getEnd())));
            int endYear = Integer.parseInt(mParseYear.format(new Date(r.getEnd())));
            if(isMonthTimeFlag && beginMonth == mBeginMonth && mBeginYear == beginYear
                    && mEndMonth == endMonth && mEndYear == endYear)
            {
                if(mapCount.get(r.getCategoryTitle())==null)
                {
                    mapCount.put(r.getCategoryTitle(), interval);
                }
                else {
                    mapCount.put(r.getCategoryTitle(), mapCount.get(r.getCategoryTitle()) + interval);
                }
            }

            if(isUserTimeFlag && beginMonth >= mBeginMonth && mBeginYear >= beginYear
                    && mEndMonth <= endMonth && mEndYear <= endYear)
            {
                if(mapCount.get(r.getCategoryTitle())==null)
                {
                    mapCount.put(r.getCategoryTitle(), interval);
                }
                else {
                    mapCount.put(r.getCategoryTitle(), mapCount.get(r.getCategoryTitle()) + interval);
                }
            }

            if(isAllTimeFlag) {
                if (mapCount.get(r.getCategoryTitle()) == null) {
                    mapCount.put(r.getCategoryTitle(), interval);
                } else {
                    mapCount.put(r.getCategoryTitle(), mapCount.get(r.getCategoryTitle()) + interval);
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
                    mGraficoPartidos.addSeries(segment, new SegmentFormatter(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), Color.BLACK,Color.BLACK, Color.BLACK));
                }
            }
            PieRenderer pieRenderer = mGraficoPartidos.getRenderer(PieRenderer.class);
            pieRenderer.setDonutSize((float) 0 / 100,   PieRenderer.DonutMode.PERCENT);
        }
        catch (NullPointerException e){
            Toast toast = Toast.makeText(this,"Нет данных для построения круговой диаграммы",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void showStatisticRecord()
    {
        mCheckedPosition = mList.getCheckedItemPositions();
        List<String> selectItem = new ArrayList<>();
        for (int i = 0; i < mCheckedPosition.size(); i++) {
            int position = mCheckedPosition.keyAt(i);
            // Add sport if it is mCheckedPosition i.e.) == TRUE!
            if (mCheckedPosition.valueAt(i))
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
        if(id == R.id.month_time_settings)
        {
            isMonthTimeFlag = true;
            isUserTimeFlag = false;
            isAllTimeFlag = false;
            mBeginMonth = mEndMonth = 1;
            mBeginYear = mEndYear = 2017;
            viewList();
        }
        if(id == R.id.all_time_settings)
        {
            isMonthTimeFlag = false;
            isUserTimeFlag = false;
            isAllTimeFlag = true;
            viewList();
        }
        if(id== R.id.user_time_settings)
        {
            isMonthTimeFlag = false;
            isUserTimeFlag = true;
            isAllTimeFlag = false;
            onClickDatePicker();

        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickDatePicker() {

        LayoutInflater layoutInflater = LayoutInflater.from(StatisticActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.content_add_time_statistic, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StatisticActivity.this);
        alertDialogBuilder.setTitle("Выберите период");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    EditText mBegin = (EditText) promptView.findViewById(R.id.edit_begin);
                                    EditText mEnd = (EditText) promptView.findViewById(R.id.edit_end);

                                    String begin = mBegin.getText().toString();
                                    String end = mEnd.getText().toString();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

                                    long time = simpleDateFormat.parse(begin).getTime();
                                    mBeginMonth = Integer.parseInt(mParseMonth.format(new Date(time)));
                                    mBeginYear = Integer.parseInt(mParseYear.format(new Date(time)));

                                    time = simpleDateFormat.parse(end).getTime();
                                    mEndMonth = Integer.parseInt(mParseMonth.format(new Date(time)));
                                    mEndYear = Integer.parseInt(mParseYear.format(new Date(time)));
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                viewList();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                 });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
