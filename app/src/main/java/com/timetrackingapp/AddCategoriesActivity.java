package com.timetrackingapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.timetrackingapp.classes.Category;
import com.timetrackingapp.db.DbUtils;

public class AddCategoriesActivity extends AppCompatActivity {

    private EditText mTitle;
    private android.widget.EditText mDesc;
    private Button mAddButton;

    private DbUtils utils;
    private SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categories);

        mTitle = (EditText) findViewById(R.id.title_category);
        mAddButton = (Button) findViewById(R.id.add_category_button);

        utils = new DbUtils(this, DbUtils.DATABASE_NAME, DbUtils.DATABASE_VERSION);
        database = utils.getWritableDatabase();//дает бд на запись

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
                Intent intent= new Intent(AddCategoriesActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void addCategory()
    {
        String title = mTitle.getText().toString();
        utils.insertCategories(database,new Category(title));
        Intent intent = new Intent();
        intent.putExtra("cat",new Category(title));
        setResult(RESULT_OK, intent);
        finish();
    }
}
