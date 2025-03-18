package com.example.myapplication;



public class CardItem3 {
    private String content;
    private String Parentid;

    private String Parenttile;

    private int id,level;

    public CardItem3() {
        // 默认构造方法
    }

    public CardItem3(String content,String Parentid,int id,int level,String Parenttile) {
        this.content = content;
        this.Parentid=Parentid;
        this.id=id;
        this.level=level;
        this.Parenttile=Parenttile;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentid() {
        return Parentid;
    }

    public void setParentid(String Parentid) {
        this.Parentid = Parentid;
    }


    public String getParenttile() {
        return Parenttile;
    }

    public void setParenttile(String Parenttile) {
        this.Parenttile = Parenttile;
    }

    public int getid(){
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




}
