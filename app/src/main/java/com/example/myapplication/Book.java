package com.example.myapplication;

import android.graphics.Bitmap;

public class Book {
    private int id;
    private String title;
    private Bitmap coverResId;

    public Book(int id, String title, Bitmap coverResId) {
        this.id = id;
        this.title = title;
        this.coverResId = coverResId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getCoverResId() {
        return coverResId;
    }
}