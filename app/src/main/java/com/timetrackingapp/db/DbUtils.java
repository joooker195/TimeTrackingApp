package com.timetrackingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.timetrackingapp.classes.Category;
import com.timetrackingapp.classes.Photo;
import com.timetrackingapp.classes.Record;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ксю on 21.12.2016.
 */
public class DbUtils extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    private static final String LOG_TAG = "DbUtils";

    //названия таблиц
    public static final String DATABASE_NAME = "TIME_TRACKER";
    public static final String CATEGORY_TABLE = "CATEGORY";
    public static final String PHOTO_TABLE = "PHOTO";
    public static final String RECORD = "RECORD";
    public static final String TIME_TO_PHOTO_TABLE = "TIME_TO_PHOTO";

    //Константы для полей таблицы "Категория"
    public static final String CATEGORY_ID = "ID";
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";
    public static final String CATEGORY_DESC = "CATEGORY_DESC";

    //таблица развязка между временем и категорией
    public static final String TIME_ID_REF = "TIME_ID_REF";
    public static final String PHOTO_ID_REF="PHOTO_ID_REF";

    //таблица с фотками
    public static final String PHOTO_ID = "ID";
    public static final String IMAGE = "IMAGE";

    //таблица с отметками времени
    public static final String TIME_ID = "ID";//первичный ключ глав таблицы время
    public static final String CATEGORY_ID_REF = "CATEGORY_ID";// id категории
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";
    public static final String TIME_SEGMENT = "TIME_SEGMENT";

    //Запросы на создание таблиц
    public static final String CREATE_CATEGORY_QUERY = "CREATE TABLE `Category` (\n" +
            "\t`ID`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`CATEGORY_TITLE`\tINTEGER\n" +
            "\t`CATEGORY_DESC`\tINTEGER\n" +
            ");";
    public static final String CREATE_PHOTO_QUERY = "CREATE TABLE `Photo` (\n" +
            "\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`IMAGE`\tBLOB\n" +
            ");";
    public static final String CREATE_TIMERECORD_QUERY = "CREATE TABLE `Record` (\n" +
            "\t`ID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`CATEGORY_ID`\tINTEGER,\n" +
            "\t`DESCRIPTION`\tTEXT,\n" +
            "\t`START_TIME`\tNUMERIC,\n" +
            "\t`END_TIME`\tNUMERIC,\n" +
            "\t`TIME_SEGMENT`\tINTEGER,\n" +
            " FOREIGN KEY(CATEGORY_ID) REFERENCES Category(id) ON UPDATE CASCADE\n"+
            ");";
    public static final String CREATE_REFERENCE_TABLE="CREATE TABLE `time_photo` (\n" +
            "\t`time_id_ref`\tINTEGER,\n" +
            "\t`photo_id_ref`\tINTEGER, \n" +
            " FOREIGN KEY(time_id_ref) REFERENCES Category(id) ON UPDATE CASCADE on delete cascade,\n"+
            "FOREIGN KEY(photo_id_ref) REFERENCES Photo(id) ON UPDATE CASCADE on delete cascade"+
            ");";

    public String sqlQuery = "";//cтрока для запросов
    private Calendar calendar;

    public DbUtils(Context context, String name, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.d(LOG_TAG,"Databse create called");
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");//Активирует вторичные (FK) ключи
        sqLiteDatabase.execSQL(CREATE_CATEGORY_QUERY);
        sqLiteDatabase.execSQL(CREATE_PHOTO_QUERY);
        sqLiteDatabase.execSQL(CREATE_TIMERECORD_QUERY);
        sqLiteDatabase.execSQL(CREATE_REFERENCE_TABLE);
        Log.d(LOG_TAG,"Table created sucs");
        insertCategories(sqLiteDatabase,new Category("Работа"));
        insertCategories(sqLiteDatabase,new Category("Обед"));
        insertCategories(sqLiteDatabase,new Category("Отдых"));
        insertCategories(sqLiteDatabase,new Category("Сон"));

        //инициализируем развязку
  /*      ContentValues contentValues = new ContentValues();
        contentValues.put(TIME_ID_REF,1);
        contentValues.put(PHOTO_ID_REF,1);
        sqLiteDatabase.insert(TIME_TO_PHOTO_TABLE,null,contentValues);

        ContentValues cv = new ContentValues();
        cv.put(TIME_ID_REF,1);
        cv.put(PHOTO_ID_REF,2);
        sqLiteDatabase.insert(TIME_TO_PHOTO_TABLE,null,cv);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists "+CATEGORY_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+PHOTO_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+ RECORD);
        sqLiteDatabase.execSQL("drop table if exists "+ TIME_TO_PHOTO_TABLE);
        Log.d(LOG_TAG,"Drop tables sucs");
        onCreate(sqLiteDatabase);
    }

    public Cursor getAllRecords(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.query(tableName, null,null, null, null, null, null);
        return  cursor;
    }

    public List<Record> getRecords(SQLiteDatabase database){
        List<Record> res = new LinkedList<>();
        Record record;
        List<Photo> photoRecords = new LinkedList<>();
        int i = 0;
        int idId,descriptionId,startDateId,endDateId,segmentId,categoryId;
        int idRec,idCat;
        long begin,end, segment;
        String desc, titleCat;
        Cursor cursor = getAllRecords(database,RECORD);
        if (cursor != null && cursor.moveToFirst()) {
            idId = cursor.getColumnIndex(CATEGORY_ID);
            descriptionId = cursor.getColumnIndex(DESCRIPTION);
            startDateId = cursor.getColumnIndex(START_TIME);
            endDateId = cursor.getColumnIndex(END_TIME);
            segmentId = cursor.getColumnIndex(TIME_SEGMENT);
            categoryId = cursor.getColumnIndex(CATEGORY_ID_REF);
            do {
                idRec = cursor.getInt(idId);
                desc = cursor.getString(descriptionId);
                begin = cursor.getLong(startDateId);
                end = cursor.getLong(endDateId);
                segment = cursor.getLong(segmentId);
                idCat = cursor.getInt(categoryId);
                titleCat = getCategoryTitleById(idCat, database);
          //     photoRecords = getPhotoListByTimeRecordId(database,iDval);
                record = new Record(desc, segment, begin, end, idCat, titleCat);
                res.add(record);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    public long insertData(SQLiteDatabase database, ContentValues values, String table){
        long res =  database.insert(table,null,values);
        return res;
    }

    public void insertRecord(SQLiteDatabase database, Record data)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION, data.getDesc());
        contentValues.put(START_TIME, data.getBegin());
        contentValues.put(END_TIME, data.getEnd());
        contentValues.put(TIME_SEGMENT, data.getInterval());
        contentValues.put(CATEGORY_ID_REF, data.getCategoryRef());
        database.beginTransaction();
        long res =  database.insert(DbUtils.RECORD, null, contentValues);
        Log.d(LOG_TAG,"InsertResult "+res);
        database.setTransactionSuccessful();
        database.endTransaction();

    }

    //content values вставляет одну запись
    public void insertCategories(SQLiteDatabase database, Category data){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_TITLE,data.getTitle());
     //   contentValues.put(CATEGORY_DESC,data.getDesc());
        database.beginTransaction();
        long res =  database.insert(DbUtils.CATEGORY_TABLE, null, contentValues);
        Log.d(LOG_TAG,"InsertResult "+res);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public int update(SQLiteDatabase database,String tableName,ContentValues contentValues,String fieldCause, String[] whereArgs)
    {
        int updCount = database.update(tableName, contentValues, fieldCause+"= ?", whereArgs);
        return updCount;
    }

    public int getIdCategoryByName(String name,SQLiteDatabase database){
        int res = 0;
        for (Category c: getCategories(database,CATEGORY_TABLE)){
            if(c.getTitle().equals(name)){
                res = c.getId();
                break;
            }
        }
        return res;
    }

    public String getCategoryTitleById(int id, SQLiteDatabase database)
    {
        String res = "";
        for (Category c: getCategories(database,CATEGORY_TABLE)){
            if(c.getId() == id){
                res = c.getTitle();
                break;
            }
        }
        return res;
    }


    public List<Category> getCategories(SQLiteDatabase database, String table){
        List<Category> result = new LinkedList<>();
        Cursor cursor = database.query(table, null,null, null, null, null, null);
        int i = 0;
        String name;
        int id;
        Category category;
        if (cursor != null && cursor.moveToFirst()) {
            int idId = cursor.getColumnIndex(DbUtils.CATEGORY_ID);
            int titleId = cursor.getColumnIndex(DbUtils.CATEGORY_TITLE);
            do {
                id = cursor.getInt(idId);
                name = cursor.getString(titleId);
                category = new Category(id, name);
                result.add(category);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

    public void initPhotoTable(SQLiteDatabase database,int resID,Context ctx){
        Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(), resID);
        byte[] image = DbBitmapUtils.getBytes(icon);
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE,image);
        insertData(database,contentValues,PHOTO_TABLE);
    }

    public void insertCameraImage(SQLiteDatabase database, Bitmap bitmap){
        byte[] image = DbBitmapUtils.getBytes(bitmap);
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE,image);
        insertData(database,contentValues,PHOTO_TABLE);
    }

    public List<Photo> getAllPhoto(SQLiteDatabase database){
        List<Photo> res = new LinkedList<>();
        Cursor cursor = getAllRecords(database,PHOTO_TABLE);
        int idId;
        int i = 0;
        int imageId;
        Photo photo;
        int id;
        Bitmap bitmap;
        if (cursor != null && cursor.moveToFirst()) {
            idId = cursor.getColumnIndex(PHOTO_ID);
            imageId = cursor.getColumnIndex(IMAGE);
            do {
                id = cursor.getInt(idId);
                bitmap =DbBitmapUtils.getImage(cursor.getBlob(imageId));
                photo = new Photo(id, bitmap);
                res.add(photo);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return  res;
    }

    public int deleteEntity(SQLiteDatabase database,String table,String causeColumn,String[] causeArgs)
    {
        int res =  database.delete (table, causeColumn+"=?", causeArgs);
        return res;
    }

    public void deleteRazvByPhoto(Photo photo){
        sqlQuery = "";
    }

    //Вставляет записи в таблицу времени
    public void initRecordTable(Record record, SQLiteDatabase database) {
        calendar = Calendar.getInstance();
        calendar.set(2016, 0, 5, 1, 0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_ID_REF, 1);
        contentValues.put(START_TIME, calendar.getTimeInMillis());
        calendar.set(2016, 0, 31);
        contentValues.put(END_TIME, calendar.getTimeInMillis());
        contentValues.put(TIME_SEGMENT, 10);
        contentValues.put(DESCRIPTION, "asdf");
        database.insert(RECORD, null, contentValues);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(CATEGORY_ID_REF, 1);
        calendar.set(2016, 1, 1, 1, 0);
        contentValues1.put(START_TIME, calendar.getTimeInMillis());
        calendar.set(2016, 4, 1, 1, 0);
        contentValues1.put(END_TIME, calendar.getTimeInMillis());
        contentValues1.put(TIME_SEGMENT, 10);
        contentValues1.put(DESCRIPTION, "efef");
        database.insert(RECORD, null, contentValues1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(CATEGORY_ID_REF, 1);
        calendar.set(2016, 2, 21, 1, 0);
        contentValues2.put(START_TIME, calendar.getTimeInMillis());
        calendar.set(2016, 3, 21, 1, 0);
        contentValues2.put(END_TIME, calendar.getTimeInMillis());
        contentValues2.put(TIME_SEGMENT, 10);
        contentValues2.put(DESCRIPTION, "rgrg");
        database.insert(RECORD, null, contentValues2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(CATEGORY_ID_REF, 2);
        calendar.set(2016, 2, 21, 1, 0);
        contentValues3.put(START_TIME, calendar.getTimeInMillis());
        calendar.set(2016, 3, 21, 1, 0);
        contentValues3.put(END_TIME, calendar.getTimeInMillis());
        contentValues3.put(TIME_SEGMENT, 50);
        contentValues3.put(DESCRIPTION, "rgrg");
        database.insert(RECORD, null, contentValues3);

        ContentValues contentValues4 = new ContentValues();
        contentValues4.put(CATEGORY_ID_REF, 3);
        calendar.set(2016, 2, 21, 1, 0);
        contentValues4.put(START_TIME, calendar.getTimeInMillis());
        calendar.set(2016, 3, 21, 1, 0);
        contentValues4.put(END_TIME, calendar.getTimeInMillis());
        contentValues4.put(TIME_SEGMENT, 5);
        contentValues4.put(DESCRIPTION, "rgrg");
        database.insert(RECORD, null, contentValues4);
    }

    public ArrayList<Category> parseCursor(Cursor cursor) {
        ArrayList<Category> listCategories = new ArrayList<>();
        String title;
        int id;
        String desc;
        Category category;
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int idId = cursor.getColumnIndex(DbUtils.CATEGORY_ID);
            int categoryId = cursor.getColumnIndex(DbUtils.CATEGORY_TITLE);
            int descId = cursor.getColumnIndex(DbUtils.CATEGORY_DESC);
            do {
                id = cursor.getInt(idId);
                title = cursor.getString(categoryId);
            //    desc = cursor.getString(descId);
                category = new Category(id, title);
                listCategories.add(category);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return listCategories;
    }

}
