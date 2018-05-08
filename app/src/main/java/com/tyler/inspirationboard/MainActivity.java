package com.tyler.inspirationboard;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tyler.inspirationboard.database.DbManager;
import com.tyler.inspirationboard.database.DbCursorAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.Date;

public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "MainActivity";
    // To identify which permission request is returning a result
    private static final int DISPLAY_REQUEST_CODE = 1001;
    private static final int TAKE_PICTURE_REQUEST_CODE = 0;

    // Used in the instance state Bundle, to preserve image during rotation
    private CursorAdapter cursorAdapter;
    ImageView mCameraPicture;
    private File mPhotoFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCameraPicture = (ImageView) findViewById(R.id.imageDocIcon);

        openNewInspirationText();
        openNewInspirationPhoto();

        cursorAdapter = new DbCursorAdapter(this, null, 0);
        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DisplayInspirationActivity.class);
                Uri uri = Uri.parse(InspirationProvider.CONTENT_URI + "/" + id);
                intent.putExtra(InspirationProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, DISPLAY_REQUEST_CODE);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void openNewInspirationPhoto() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_phone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Check to see if there is a camera on this device.
                if (pictureIntent.resolveActivity(getPackageManager()) == null) {
                    Toast.makeText(MainActivity.this, "Your device does not have a camera", Toast.LENGTH_LONG).show();
                } else {
                    startActivityForResult(pictureIntent, TAKE_PICTURE_REQUEST_CODE);
                }
            }
        });
    }

    private void openNewInspirationText() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_text);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), DisplayInspirationActivity.class);
                startActivityForResult(intent, DISPLAY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DISPLAY_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        } else if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
            Bitmap thumbnail = data.getParcelableExtra("data");

        }

    }

    public File getPhotoFile(int id) {
        File filesDir = getApplicationContext().getFilesDir();
        return new File(filesDir, getPhotoFilename(id));
    }

    public String getPhotoFilename(int id) {
        Log.d(TAG, "IMG_" + String.valueOf(id) + ".jpg");
        return "IMG_" + String.valueOf(id) + ".jpg";
    }

    private void insertInspiration(String inspirationText) {
        //ContentProvider is registered in manifest. To get to the ContentProvider
        // we'll call getContentResolver.

        ContentValues values = new ContentValues();
        values.put(DbManager.INSPIRATION_TEXT, inspirationText);
        Uri inspirationUri = getContentResolver().insert(InspirationProvider.CONTENT_URI, values);

        Log.d(TAG, "Inserted Note: " + inspirationUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;

            case R.id.action_delete_all:
                deleteAllInspirations();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertSampleData() {
        insertInspiration("Simple Inspiration");
        insertInspiration("Multi-line\nInspiration\nYAY\nCan't see");
        insertInspiration("Very long note with a lot of text that exceeds the width of the screen");
        //Data has changed, inform loader object to restart, and read data in again
        restartLoader();

    }

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void deleteAllInspirations() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if(button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(InspirationProvider.CONTENT_URI,
                                    null, //Deletes everthing when null
                                    null);

                            restartLoader(); //read data in again

                            Toast.makeText(MainActivity.this, R.string.deleted,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation)
                .setPositiveButton(getString(android.R.string.ok), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, InspirationProvider.CONTENT_URI,
                null, null, null, null);
        //Projection is coded in provider
        //Selection to null returns entire data set
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //onCreateLoader executes on the background thread
        // onLoadFinished brings the data back. Swap cursor to cursor adapter
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //Called when data needs to be emptied
        cursorAdapter.swapCursor(null);
    }
}
