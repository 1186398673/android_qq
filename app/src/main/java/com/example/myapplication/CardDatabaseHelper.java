package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.CardItem;

import java.util.ArrayList;
import java.util.List;

public class CardDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CardDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // 表名和列名
    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ICON_ID = "icon_id";

    // 创建表的SQL语句
    private static final String CREATE_TABLE_CARDS =
            "CREATE TABLE " + TABLE_CARDS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_ICON_ID + " INTEGER"
                    + ")";

    public CardDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_TABLE_CARDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

    // 插入卡片数据
    public long insertCard(CardItem card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, card.getTitle());
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_ICON_ID, card.getIconResId());
        long id = db.insert(TABLE_CARDS, null, values);
        db.close();
        return id;
    }

    // 查询所有卡片数据
    // 在 CardDatabaseHelper 中添加
    public List<CardItem> getCardList() {
        List<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                int iconId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON_ID));
                cardList.add(new CardItem(id, title, content, iconId));
            }
            cursor.close();
        }
        db.close();
        return cardList;
    }

    // 在 CardDatabaseHelper 中添加
    public boolean deleteCardById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 在 CardDatabaseHelper 中添加
    public int deleteAllCards() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, "1", null);
        db.close();
        return rowsDeleted;
    }
}