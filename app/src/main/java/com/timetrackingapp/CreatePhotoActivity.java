package com.timetrackingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.timetrackingapp.adapter.PhotoCameraAdapter;
import com.timetrackingapp.classes.Photo;
import com.timetrackingapp.db.DbUtils;

import java.util.ArrayList;
import java.util.List;

public class CreatePhotoActivity extends AppCompatActivity {

    private final int CAMERA_RESULT_ADD = 0;
    private List<Photo> allPhoto = new ArrayList<>();
    private PhotoCameraAdapter adapter;
    private SQLiteDatabase database;
    private DbUtils utils;
    private Photo p;

    private ListView mListPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo);

        mListPhoto = (ListView) findViewById(R.id.list_photo);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();

        allPhoto = utils.getAllPhoto(database);
        adapter = new PhotoCameraAdapter(this,R.layout.content_photo,allPhoto);
        mListPhoto.setAdapter(adapter);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_RESULT_ADD);

        mListPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                p = (Photo) adapterView.getItemAtPosition(i);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_RESULT_ADD: {
                try {
                    Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
                    utils.insertCameraImage(database, thumbnailBitmap);
                    allPhoto.add(new Photo(thumbnailBitmap));
                    adapter.notifyDataSetChanged();
                    break;
                } catch (NullPointerException e) {
                    Toast toast = Toast.makeText(this, "Добавление было отменено пользователем", Toast.LENGTH_LONG);
                    toast.show();
                    break;
                }
            }
        }
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
            utils.deleteCascadePhoto(database,p);
            allPhoto.remove(p);
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
