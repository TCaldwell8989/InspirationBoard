package com.tyler.inspirationboard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;

public class DbManager extends SQLiteOpenHelper {


    // Constants for DB name and version
    private static final String DATABASE_NAME = "inspirationboard.db";
    private static final int DATABASE_VERSION = 1;

    // Constants for identifying table and columns
    public static final String TABLE_INSPIRATION = "inspirations";
    public static final String INSPIRATION_ID = "_id";
    public static final String INSPIRATION_TEXT = "text";
    public static final String INSPIRATION_CREATED = "dateCreated";
    public static final String INSPIRATION_PICTURE = "picture";

    // Create Table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_INSPIRATION + "(" +
                    INSPIRATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INSPIRATION_TEXT + " TEXT, " +
                    INSPIRATION_CREATED + " TEXT default CURRENT_TIMESTAMP," +
                    INSPIRATION_PICTURE + " TEXT" +
                    ")";

    public static final String[] ALL_COLUMNS =
            {INSPIRATION_ID, INSPIRATION_TEXT, INSPIRATION_CREATED, INSPIRATION_PICTURE};



    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSPIRATION);
        onCreate(db);
    }
}
