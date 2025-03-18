package com.example.myapplication;

public class CardItem2 {
    private String content;          // 卡片内容
    private String contentDefine;   // 定义
    private String contentMeaning;  // 意义
    private String contentRange;    // 适用范围
    private String contentExample;  // 例子
    private String parentTitle;     // 父标题
    private int id,level;                 // 卡片ID

    // 构造方法：包含所有字段
    public CardItem2(String content, String contentDefine, String contentMeaning,
                     String contentRange, String contentExample, String parentTitle, int id,int level) {
        this.content = content;
        this.contentDefine = contentDefine;
        this.contentMeaning = contentMeaning;
        this.contentRange = contentRange;
        this.contentExample = contentExample;
        this.parentTitle = parentTitle;
        this.id = id;
        this.level=level;
    }

    // 默认构造方法
    public CardItem2() {
        // 默认构造方法
    }

    // Getter 和 Setter 方法

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentDefine() {
        return contentDefine;
    }

    public void setContentDefine(String contentDefine) {
        this.contentDefine = contentDefine;
    }

    public String getContentMeaning() {
        return contentMeaning;
    }

    public void setContentMeaning(String contentMeaning) {
        this.contentMeaning = contentMeaning;
    }

    public String getContentRange() {
        return contentRange;
    }

    public void setContentRange(String contentRange) {
        this.contentRange = contentRange;
    }

    public String getContentExample() {
        return contentExample;
    }

    public void setContentExample(String contentExample) {
        this.contentExample = contentExample;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
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



}