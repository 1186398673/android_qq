package com.example.myapplication;


import android.annotation.SuppressLint;
import android.database.Cursor;

import java.io.Serializable;




public class CardItem implements Serializable{

    private int id,level;

    private String title;
    private String content;
    private int iconResId;

    public CardItem( int id,String title, String content, int iconResId,int level) {
        this.id=id;
        this.title = title;
        this.content = content;
        this.iconResId = iconResId;
        this.level=level;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }



}


