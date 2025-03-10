package com.example.myapplication;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CardDatabaseHelper3 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cards2.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PARENT_ID = "parentId"; // 更新后的列名

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_PARENT_ID + " INTEGER" + // 修改为 INTEGER 类型
                    ");";

    public CardDatabaseHelper3(Context context) {
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
    public void insertCard(CardItem3 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_ID, card.getParentid()); // 更新为 parentId
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentId 查询卡片列表
    public List<CardItem3> getCardsByParentId(int parentId) {
        List<CardItem3> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID, COLUMN_CONTENT, COLUMN_PARENT_ID},
                COLUMN_PARENT_ID + "=?", new String[]{String.valueOf(parentId)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                CardItem3 card = new CardItem3(cursor.getString(1), cursor.getInt(2), cursor.getInt(0)); // 更新构造方法参数顺序
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cardList;
    }

    // 批量更新 parentId
    public int updateParentId(int oldParentId, int newParentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARENT_ID, newParentId);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_PARENT_ID + "=?", new String[]{String.valueOf(oldParentId)});
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

    // 根据 parentId 删除所有匹配的卡片
    public int deleteCardsByParentId(int parentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, COLUMN_PARENT_ID + "=?", new String[]{String.valueOf(parentId)});
        db.close();
        return rowsDeleted;
    }
}
