package com.example.task;

import java.io.Serializable;

import java.util.Date;
public class TaskItem implements Serializable {
    private int id;
    private String title;
    private String content;
    private boolean completed; 
    private Date date;


    // 默认构造方法（自动设置当前日期）
    public TaskItem(int id, String title, String content) {
        this(id, title, content, new Date(), false);
    }


    public TaskItem(int id, String title, String content, Date date, boolean completed) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.completed = completed;
    }



    // Getters and setters
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // 新增日期的getter/setter
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}