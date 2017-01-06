package com.timetrackingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
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
    public static final String TIME_PHOTO_TABLE = "time_photo";

    //Константы для полей таблицы "Категория"
    public static final String CATEGORY_ID = "ID";
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";
    public static final String CATEGORY_DESC = "CATEGORY_DESC";

    //таблица развязка между временем и категорией
    public static final String TIME_ID_REF = "time_id_ref";
    public static final String PHOTO_ID_REF="photo_id_ref";

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
        long res = sqLiteDatabase.insert(TIME_PHOTO_TABLE,null,contentValues);

    /*    ContentValues cv = new ContentValues();
        cv.put(TIME_ID_REF,1);
        cv.put(PHOTO_ID_REF,2);
        res = sqLiteDatabase.insert(TIME_PHOTO_TABLE,null,cv);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists "+CATEGORY_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+PHOTO_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+ RECORD);
        sqLiteDatabase.execSQL("drop table if exists "+ TIME_PHOTO_TABLE);
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
            idId = cursor.getColumnIndex(TIME_ID);
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
                photoRecords = getPhotoListByTimeRecordId(database,idRec);
                record = new Record(desc, segment, begin, end, idCat, titleCat, photoRecords);
                res.add(record);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    public List<Photo> getPhotoListByTimeRecordId(SQLiteDatabase database,int TimeId) {
        List<Photo> photosByCategory = new LinkedList<>();
      //  Cursor cursor = database.query(TIME_PHOTO_TABLE, null, TIME_ID_REF + "=?", new String[]{String.valueOf(TimeId)}, null, null, null);
        Cursor cursor = database.query(TIME_PHOTO_TABLE, null, TIME_ID_REF + "="+ String.valueOf(TimeId-1), null, null, null, null);
        int idCategoryIdx;
        int idPhotoIdx;
        int i = 0;
        Photo photo;
        int idValCategory,idValProtoID;
        if (cursor != null && cursor.moveToFirst()) {
            idCategoryIdx = cursor.getColumnIndex(DbUtils.TIME_ID_REF);
            idPhotoIdx = cursor.getColumnIndex(DbUtils.PHOTO_ID_REF);
            do {
                idValCategory = cursor.getInt(idCategoryIdx);
                idValProtoID = cursor.getInt(idPhotoIdx);
                photo = getPhotoById(database,idValProtoID);
                photosByCategory.add(photo);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return photosByCategory;
    }

    public Photo getPhotoById(SQLiteDatabase database,int id){
        Photo photo = null;
        int i = 0;
        Bitmap bmp;
        int idCat;
      //  Cursor cursor = database.query(PHOTO_TABLE,null,PHOTO_ID+"=?",new String[]{String.valueOf(id)},null,null,null);
        Cursor cursor = database.query(PHOTO_TABLE,null,PHOTO_ID+"="+String.valueOf(id),null,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndex(DbUtils.PHOTO_ID);
            int photoIdx = cursor.getColumnIndex(DbUtils.IMAGE);
            do {
                idCat = cursor.getInt(idIdx);
                bmp = DbBitmapUtils.getImage(cursor.getBlob(photoIdx));
                photo = new Photo(bmp,idCat);
                i++;
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return photo;
    }

    public long insertData(SQLiteDatabase database, ContentValues values, String table){
        long res =  database.insert(table,null,values);
        return res;
    }

    //беерт id у последней записси, что вставить его в развязку
    public int getNextId(SQLiteDatabase database){
        int res = 0;
        sqlQuery="SELECT * FROM "+RECORD+" WHERE " +TIME_ID+" = (SELECT MAX("+TIME_ID+")  FROM "+RECORD+")";
        Cursor cursor = database.rawQuery(sqlQuery,null);
        int idIdx;
        int IdVal = 0;
        if (cursor != null && cursor.moveToFirst()) {
            idIdx = cursor.getColumnIndex(DbUtils.TIME_ID);
            IdVal = cursor.getInt(idIdx);
        }
        res = IdVal;
        cursor.close();
        return  res;
    }

    public void insertRecord(SQLiteDatabase database, Record data)
    {
        ContentValues contentValues = new ContentValues();
        ContentValues cvR;
        contentValues.put(DESCRIPTION, data.getDesc());
        contentValues.put(START_TIME, data.getBegin());
        contentValues.put(END_TIME, data.getEnd());
        contentValues.put(TIME_SEGMENT, data.getInterval());
        contentValues.put(CATEGORY_ID_REF, data.getCategoryRef());

        cvR = new ContentValues();
        int id = getNextId(database);
        cvR.put(TIME_ID_REF, id);

        List<Photo> photos = data.getPhotos();
        for (Photo p : photos) {
            cvR.put(PHOTO_ID_REF, p.getId());
        }

     //   contentValues.put(TIME_ID, id);
        database.insert(DbUtils.TIME_PHOTO_TABLE, null, cvR);
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

    public List<Category> getAllCategories(SQLiteDatabase database)
    {
        return getCategories(database,CATEGORY_TABLE);
    }

    private List<Category> getCategories(SQLiteDatabase database, String table){
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

    public int pieData(SQLiteDatabase database,Category category){
        int res;
        String sql = "select sum(TIME_SEGMENT) from Record where CATEGORY_ID=?";
        String str = "";
        Cursor cursor = database.rawQuery(sql,new String[]{String.valueOf(category.getId())},null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (String cn : cursor.getColumnNames()) {
                    str = cursor.getString(cursor.getColumnIndex(cn));
                }
            }
            while (cursor.moveToNext());
        }
        if (str == null){
            res = 0;
        }
        else {
            res = Integer.valueOf(str);
        }
        cursor.close();
        return res;
    }

    public int getCountRecordFromCategory(SQLiteDatabase database,Category category,long startDate,long endDate){
        int res = 0;
        sqlQuery = "select * from "+RECORD+" where "+CATEGORY_ID_REF+" ="+String.valueOf(category.getId()+" and "+START_TIME+" between "+startDate+" and "+endDate);
        Cursor cursor = database.rawQuery(sqlQuery,null,null);
        res = cursor.getCount();
        cursor.close();
        return res;
    }

}
