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
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PARENT_TITLE = "parentTitle";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

    // 插入卡片
    public void insertCard(CardItem2 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_TITLE, card.getParentTitle());
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentTitle 查询卡片列表
    public List<CardItem2> getCardsByParentTitle(String parentTitle) {
        List<CardItem2> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID, COLUMN_CONTENT, COLUMN_PARENT_TITLE},
                COLUMN_PARENT_TITLE + "=?", new String[]{parentTitle}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                CardItem2 card = new CardItem2(cursor.getString(1), cursor.getString(2),cursor.getInt(0));
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

        // 更新所有 parentTitle 匹配的卡片
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

    public boolean deleteCardById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 根据 parentTitle 删除所有匹配的卡片
    public int deleteCardsByParentTitle(String parentTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 删除所有 parentTitle 匹配的卡片
        int rowsDeleted = db.delete(TABLE_CARDS, COLUMN_PARENT_TITLE + "=?", new String[]{parentTitle});
        db.close();
        return rowsDeleted;
    }
}
