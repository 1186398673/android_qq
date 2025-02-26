package com.example.myapplication;


public class CardItem2 {
    private String content;
    private String parentTitle;

    private int id;


    public CardItem2(String content,String parentTitle,int id) {
        this.content = content;
        this.parentTitle=parentTitle;
        this.id=id;

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

    public int getid(){
        return id;
    }




}