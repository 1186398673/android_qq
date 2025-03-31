package com.example.myapplication;

public class CardItem3 {
    private String content;
    private String parentId;
    private String parentTitle;
    private int id;
    private int level;
    private String imageUrl; // 新增的图片URL

    public CardItem3() {
        // 默认构造方法
    }

    public CardItem3(String content, String parentId, int id, int level, String parentTitle, String imageUrl) {
        this.content = content;
        this.parentId = parentId;
        this.id = id;
        this.level = level;
        this.parentTitle = parentTitle;
        this.imageUrl = imageUrl;
    }

    // Getter 和 Setter 方法
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentid() {
        return parentId;
    }

    public void setParentid(String parentId) {
        this.parentId = parentId;
    }

    public String getParenttile() {
        return parentTitle;
    }

    public void setParenttile(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public int getid() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}