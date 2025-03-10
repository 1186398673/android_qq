package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CardDatabaseHelper2 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cards.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CONTENT_DEFINE = "content_define";
    public static final String COLUMN_CONTENT_MEANING = "content_meaning";
    public static final String COLUMN_CONTENT_RANGE = "content_range";
    public static final String COLUMN_CONTENT_EXAMPLE = "content_example";
    public static final String COLUMN_PARENT_TITLE = "parentTitle";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_CONTENT_DEFINE + " TEXT, " +
                    COLUMN_CONTENT_MEANING + " TEXT, " +
                    COLUMN_CONTENT_RANGE + " TEXT, " +
                    COLUMN_CONTENT_EXAMPLE + " TEXT, " +
                    COLUMN_PARENT_TITLE + " TEXT" +
                    ");";

    public CardDatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单的升级策略：删除旧表并创建新表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

    // 插入卡片
    public void insertCard(CardItem2 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_CONTENT_DEFINE, card.getContentDefine());
        values.put(COLUMN_CONTENT_MEANING, card.getContentMeaning());
        values.put(COLUMN_CONTENT_RANGE, card.getContentRange());
        values.put(COLUMN_CONTENT_EXAMPLE, card.getContentExample());
        values.put(COLUMN_PARENT_TITLE, card.getParentTitle());
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentTitle 查询卡片列表
    public List<CardItem2> getCardsByParentTitle(String parentTitle) {
        List<CardItem2> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID, COLUMN_CONTENT, COLUMN_CONTENT_DEFINE,
                        COLUMN_CONTENT_MEANING, COLUMN_CONTENT_RANGE, COLUMN_CONTENT_EXAMPLE, COLUMN_PARENT_TITLE},
                COLUMN_PARENT_TITLE + "=?", new String[]{parentTitle}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                CardItem2 card = new CardItem2();
                card.setId(cursor.getInt(0));
                card.setContent(cursor.getString(1));
                card.setContentDefine(cursor.getString(2));
                card.setContentMeaning(cursor.getString(3));
                card.setContentRange(cursor.getString(4));
                card.setContentExample(cursor.getString(5));
                card.setParentTitle(cursor.getString(6));
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cardList;
    }

    // 批量更新 parentTitle
    public int updateParentTitle(String oldParentTitle, String newParentTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARENT_TITLE, newParentTitle);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_PARENT_TITLE + "=?", new String[]{oldParentTitle});
        db.close();
        return rowsAffected;
    }

    public boolean updateCardContent(int id, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, newContent);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateCard(CardItem2 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_CONTENT_DEFINE, card.getContentDefine());
        values.put(COLUMN_CONTENT_MEANING, card.getContentMeaning());
        values.put(COLUMN_CONTENT_RANGE, card.getContentRange());
        values.put(COLUMN_CONTENT_EXAMPLE, card.getContentExample());
        values.put(COLUMN_PARENT_TITLE, card.getParentTitle());

        // 使用 id 作为条件更新
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(card.getid())});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteCardById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 根据 parentTitle 删除所有匹配的卡片
    public int deleteCardsByParentTitle(String parentTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, COLUMN_PARENT_TITLE + "=?", new String[]{parentTitle});
        db.close();
        return rowsDeleted;
    }
}