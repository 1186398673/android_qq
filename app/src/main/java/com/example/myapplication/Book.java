package com.example.myapplication;

public class Book {
    private int id;
    private String title;
    private int coverResId;

    public Book(int id, String title, int coverResId) {
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

    public int getCoverResId() {
        return coverResId;
    }
}