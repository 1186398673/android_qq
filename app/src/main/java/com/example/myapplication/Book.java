package com.example.myapplication;

import android.graphics.Bitmap;

public class Book {
    private int id;
    private String title;
    private String filePath;
    private Bitmap coverResId;

    public Book() {
        // 默认构造函数
    }

    public Book(int id, String title, String filePath, Bitmap coverResId) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.coverResId = coverResId;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getfilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getCoverResId() {
        return coverResId;
    }

    public void setCoverResId(Bitmap coverResId) {
        this.coverResId = coverResId;
    }
}