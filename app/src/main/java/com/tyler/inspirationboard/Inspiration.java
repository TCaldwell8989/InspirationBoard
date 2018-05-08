package com.tyler.inspirationboard;

import java.util.UUID;

// Model Layer for Inspiration Board
public class Inspiration {

    private int mId;
    private String mText;

    public Inspiration(int id, String text) {
        this.mId = id;
        this.mText = text;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getPhotoFilename() {
        return "IMG_" + String.valueOf(getId()) + ".jpg";
    }

}
