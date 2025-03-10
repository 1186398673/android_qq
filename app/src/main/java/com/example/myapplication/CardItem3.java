package com.example.myapplication;



public class CardItem3 {
    private String content;
    private int Parentid;

    private int id;


    public CardItem3(String content,int Parentid,int id) {
        this.content = content;
        this.Parentid=Parentid;
        this.id=id;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParentid() {
        return Parentid;
    }



    public void setParentid(int Parentid) {
        this.Parentid = Parentid;
    }

    public int getid(){
        return id;
    }




}
