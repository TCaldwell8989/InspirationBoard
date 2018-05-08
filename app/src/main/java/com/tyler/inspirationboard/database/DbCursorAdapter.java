package com.tyler.inspirationboard.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyler.inspirationboard.R;
import com.tyler.inspirationboard.database.DbManager;

public class DbCursorAdapter extends CursorAdapter {


    public DbCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate and return inspiration_list_item
        return LayoutInflater.from(context).inflate(
                R.layout.inspirtation_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Display data in cursor
        String inspirationText = cursor.getString(cursor.getColumnIndex(DbManager.INSPIRATION_TEXT));
        ImageView iv = view.findViewById(R.id.imageDocIcon);
        TextView tv = view.findViewById(R.id.tvInspiration);

        tv.setText(inspirationText);

    }
}
