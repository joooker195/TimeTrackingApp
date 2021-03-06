package com.timetrackingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.timetrackingapp.adapter.RVAdapter;
import com.timetrackingapp.classes.Category;
import com.timetrackingapp.db.DbUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final  String LOG_TAG = "CategoryActivity";
    public static final int ADD_RESULT_CODE =1;
    public static final int EDIT_CODE = 2;

    private SQLiteDatabase database;
    private DbUtils utils;

    private ArrayList<Category> listCategories = new ArrayList<>();

    private RecyclerView mCategories;
    private RVAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                Intent intent= new Intent(MainActivity.this, AddCategoriesActivity.class);
                startActivity(intent);
            }
        });

        mCategories = (RecyclerView) findViewById(R.id.category_list);
        mCategories.setHasFixedSize(true);
        mCategories.setLayoutManager(new LinearLayoutManager(this));

        //----------база данный
        initDb();
        getCategories();
        //----------
        mAdapter.notifyDataSetChanged();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.restart) {

            finish();
            this.startActivity(new Intent(this, this.getClass()));
       //     mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statistics) {

            Intent intent = new Intent(MainActivity.this, StatisticActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_make_photo)
        {
            Intent intent = new Intent(MainActivity.this, CreatePhotoActivity.class);
            startActivity(intent);
        }
        else
        if (id == R.id.nav_records)
        {
            Intent intent = new Intent(MainActivity.this, RecordListActivity.class);
            startActivity(intent);
        }
        else
        if (id == R.id.nav_about) {
            this.deleteDatabase(DbUtils.DATABASE_NAME);//программмно удаляет б
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getCategories()
    {
        listCategories = utils.parseCursor(utils.getAllRecords(database,DbUtils.CATEGORY_TABLE));
        mAdapter = new RVAdapter(listCategories, MainActivity.this);
        mCategories.setAdapter(mAdapter);

    }

    public void initDb()
    {

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();//дает бд на запись

    }

}
