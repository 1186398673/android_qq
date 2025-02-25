package com.example.myapplication;


public class CardItem2 {
    private String content;
    private String parentTitle;


    public CardItem2(String content,String parentTitle) {
        this.content = content;
        this.parentTitle=parentTitle;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentTitle() {
        return parentTitle;
    }



    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }




}