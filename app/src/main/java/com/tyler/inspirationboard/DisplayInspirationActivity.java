package com.tyler.inspirationboard;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tyler.inspirationboard.database.DbManager;

import java.sql.Blob;

public class DisplayInspirationActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private ImageView photo;
    private String inspirationFilter;
    private String oldText;
    private byte[] inspirationPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_inspiration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.inspiration_edittext);
        photo = (ImageView) findViewById(R.id.inspiration_bitmap_photo);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(InspirationProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_inspiration));
        } else {
            action = Intent.ACTION_EDIT;
            inspirationFilter = DbManager.INSPIRATION_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DbManager.ALL_COLUMNS,
                    inspirationFilter,
                    null,
                    null);

            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DbManager.INSPIRATION_TEXT));
            inspirationPhoto = cursor.getBlob(cursor.getColumnIndex(DbManager.INSPIRATION_PICTURE));

            photo.setImageBitmap(BitmapFactory.decodeByteArray(inspirationPhoto, 0, inspirationPhoto.length));

            editor.setText(oldText);
            editor.requestFocus();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_display, menu);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishDisplay();
                break;
            case R.id.action_delete:
                deleteNote();
                break;

        }
        return true;
    }

    private void finishDisplay() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertInspiration(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateInspiration(newText);
                }
        }

        finish();

    }

    private void deleteNote() {
        getContentResolver().delete(InspirationProvider.CONTENT_URI,
                inspirationFilter, null);
        Toast.makeText(this, R.string.delete_inspiration, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateInspiration(String inspirationText) {
        ContentValues values = new ContentValues();
        values.put(DbManager.INSPIRATION_TEXT, inspirationText);
        getContentResolver().update(InspirationProvider.CONTENT_URI, values, inspirationFilter, null);
        Toast.makeText(this, R.string.inspiration_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertInspiration(String inspirationText) {
        ContentValues values = new ContentValues();
        values.put(DbManager.INSPIRATION_TEXT, inspirationText);
        getContentResolver().insert(InspirationProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishDisplay();
        super.onBackPressed();
    }

}
