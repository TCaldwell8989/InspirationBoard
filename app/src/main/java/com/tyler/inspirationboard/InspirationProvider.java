package com.tyler.inspirationboard;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tyler.inspirationboard.database.DbManager;

public class InspirationProvider extends ContentProvider {

    //Globally unique string that identifies the content provider to the Android framework
    private static final String AUTHORITY = "com.tyler.inspirationboard.inspirationprovider";

    //This represents the entire data set
    private static final String BASE_PATH = "inspirations";

    //https://stackoverflow.com/questions/4896677/android-uri-of-a-created-sqlite-database
    //content://com.package_name.dbprovider_name/table_name
    //CONTENT_URI is a uniform resource identifier that identifies the content provider
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    //Constants to identify the requested operation. Numeric values are arbitrary
    //Identifies the operation, INSPIRATIONS gives every record
    //INSPIRATIONS_ID gives a single record
    private static final int INSPIRATIONS = 1;
    private static final int INSPIRATIONS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH); //the purpose of UriMatcher class is to
    //parse a URI and then tell which operation has been requested

    public static final String CONTENT_ITEM_TYPE = "Inspiration";

    static {
        //this block will execute the first time anything is called from this class
        uriMatcher.addURI(AUTHORITY, BASE_PATH, INSPIRATIONS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", INSPIRATIONS_ID); // # is a wild card
        //that means if a URI starts with base_path and ends with a / and a number that means
        //looking for a particular inspiration
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DbManager dbManager = new DbManager(getContext());
        database = dbManager.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if(uriMatcher.match(uri) == INSPIRATIONS_ID) {
            selection = DbManager.INSPIRATION_ID + " = " + uri.getLastPathSegment();
        }

        //Return data from the database table inspirations
        return database.query(DbManager.TABLE_INSPIRATION, DbManager.ALL_COLUMNS,
                selection, null, null, null, DbManager.INSPIRATION_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //Returns a URI. That URI should match the pattern (base_path/#) # being the primary key
        long id = database.insert(DbManager.TABLE_INSPIRATION,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DbManager.TABLE_INSPIRATION, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DbManager.TABLE_INSPIRATION, values, selection, selectionArgs );
    }
}
