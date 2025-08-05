package com.example.task;

import java.io.Serializable;

import java.util.Date;
public class TaskItem implements Serializable {
    private int id;
    private String title;
    private String content;

    private Date date;


    // 默认构造方法（自动设置当前日期）
    public TaskItem(int id, String title, String content) {
        this(id, title, content, new Date());
    }


    // 完整构造方法
    public TaskItem(int id, String title, String content, Date date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
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

}