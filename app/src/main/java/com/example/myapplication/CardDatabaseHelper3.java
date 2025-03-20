package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardDatabaseHelper3 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cards2.db";
    private static final int DATABASE_VERSION = 3; // 更新版本号以触发 onUpgrade

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PARENT_ID = "parentId";
    public static final String COLUMN_PARENT_TILE = "parentTile"; // 新增的 parentTile 列
    public static final String COLUMN_LEVEL = "level"; // 新增的等级列

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_PARENT_ID + " INTEGER, " +
                    COLUMN_PARENT_TILE + " TEXT, " + // 新增的 parentTile 列
                    COLUMN_LEVEL + " INTEGER DEFAULT 1" + // 默认等级为1
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
        if (oldVersion < 2) {
            // 升级到版本2：添加新的 level 列，默认为1
            db.execSQL("ALTER TABLE " + TABLE_CARDS + " ADD COLUMN " + COLUMN_LEVEL + " INTEGER DEFAULT 1");
        }
        if (oldVersion < 3) {
            // 升级到版本3：添加新的 parentTile 列，允许为空
            db.execSQL("ALTER TABLE " + TABLE_CARDS + " ADD COLUMN " + COLUMN_PARENT_TILE + " TEXT");
        }
        // 如果将来有更多版本升级，可以在这里添加更多逻辑
    }

    // 插入卡片数据
    public void insertCard(CardItem3 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_ID, card.getParentid());
        values.put(COLUMN_PARENT_TILE, card.getParenttile()); // 添加 parentTile
        values.put(COLUMN_LEVEL, card.getLevel());
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentId 查询卡片列表
    public List<CardItem3> getCardsByParentId(String parentId) {
        List<CardItem3> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID, COLUMN_CONTENT, COLUMN_PARENT_ID, COLUMN_PARENT_TILE, COLUMN_LEVEL},
                COLUMN_PARENT_ID + "=?", new String[]{parentId}, null, null, COLUMN_LEVEL + " DESC"); // 按等级升序排序

        if (cursor.moveToFirst()) {
            do {
                CardItem3 card = new CardItem3();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                card.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                card.setParentid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_ID)));
                card.setParenttile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_TILE))); // 设置 parentTile
                card.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cardList;
    }

    // 批量更新 parentId
    public int updateParentId(String oldParentId, String newParentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARENT_ID, newParentId);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_PARENT_ID + "=?", new String[]{String.valueOf(oldParentId)});
        db.close();
        return rowsAffected;
    }

    // 更新卡片内容
    public boolean updateCardContent(int id, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, newContent);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 删除卡片
    public boolean deleteCardById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 根据 parentId 删除所有匹配的卡片
    public int deleteCardsByParentId(String parentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, COLUMN_PARENT_ID + "=?", new String[]{String.valueOf(parentId)});
        db.close();
        return rowsDeleted;
    }

    // 根据 parentTile 删除所有匹配的卡片
    public int deleteCardsByParentTile(String parentTile) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, COLUMN_PARENT_TILE + "=?", new String[]{parentTile});
        db.close();
        return rowsDeleted;
    }

    // 更新卡片等级
    public boolean updateCardLevel(int id, int newLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 更新卡片的所有字段
    public boolean updateCard(CardItem3 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_ID, card.getParentid());
        values.put(COLUMN_PARENT_TILE, card.getParenttile()); // 添加 parentTile
        values.put(COLUMN_LEVEL, card.getLevel());
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(card.getid())});
        db.close();
        return rowsAffected > 0;
    }

    // 导出数据为 CSV 文件
    public void CardItem3_exportToCSV(Context context) {


        // 获取卡片列表
        List<CardItem3> cardList = getAllCards();

        // 定义 CSV 文件的标题
        String[] headers = {"ID", "Content", "Parent ID", "Parent Tile", "Level"};

        // 获取外部存储的公共下载目录
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CardData3");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        // 定义 CSV 文件名
        File file = new File(exportDir, "CardDatabase3" + System.currentTimeMillis() + ".csv");

        try {
            FileWriter writer = new FileWriter(file);
            // 写入标题
            writer.append(String.join(",", headers));
            writer.append("\n");

            // 写入数据
            for (CardItem3 card : cardList) {
                writer.append(String.valueOf(card.getid()));
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContent()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getParentid()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getParenttile()) + "\"");
                writer.append(",");
                writer.append(String.valueOf(card.getLevel()));
                writer.append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(context, "数据已成功导出到 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败: " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    // 获取所有卡片数据
    private List<CardItem3> getAllCards() {
        List<CardItem3> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null, COLUMN_LEVEL + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CardItem3 card = new CardItem3();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                card.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                card.setParentid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_ID)));
                card.setParenttile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_TILE))); // 设置 parentTile
                card.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                cardList.add(card);
            }
            cursor.close();
        }
        db.close();
        return cardList;
    }

    // 转义特殊字符
    private String escapeSpecialCharacters(String data) {
        String escapedData = data;
        if (data.contains("\"")) {
            escapedData = data.replace("\"", "\"\"");
        }
        return escapedData;
    }

    // 从 CSV 文件导入数据
    public void importFromCSV( Uri uri,final Context context) {

        final AtomicBoolean success = new AtomicBoolean(true);
        // 使用 AsyncTask 或其他异步方式处理导入操作，避免阻塞主线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedReader br = null;
                SQLiteDatabase db = null;
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    if (inputStream == null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "无法打开文件", Toast.LENGTH_SHORT).show();
                            }
                        });
                        success.set(false);
                        return;
                    }

                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    boolean isFirstLine = true;
                    db = getWritableDatabase();
                    db.beginTransaction();
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            // 跳过标题行
                            isFirstLine = false;
                            continue;
                        }

                        // 解析每一行
                        String[] values = parseCsvLine(line);
                        if (values.length != 5) {
                            // 跳过格式不正确的行
                            continue;
                        }



                        String content = values[1];
                        int id = Integer.parseInt(values[0]);
                        String parentId = values[2];
                        String parentTile = values[3];
                        int level = Integer.parseInt(values[4]);

                        // 创建新的 CardItem3 对象
                        CardItem3 card = new CardItem3(content, parentId, id, level, parentTile);

                        // 插入到数据库
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_CONTENT, card.getContent());
                        cv.put(COLUMN_PARENT_ID, card.getParentid());
                        cv.put(COLUMN_PARENT_TILE, card.getParenttile());
                        cv.put(COLUMN_LEVEL, card.getLevel());
                        db.insert(TABLE_CARDS, null, cv);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                    success.set(false);
                } finally {
                    if (db != null) {
                        db.close();
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // 在主线程中显示结果
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (success.get()) {
                            Toast.makeText(context, "数据已成功导入", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "导入失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values.toArray(new String[0]);
    }
    public int getMaxId() {
        int maxId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT MAX(" + COLUMN_ID + ") AS max_id FROM " + TABLE_CARDS;
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                maxId = cursor.getInt(cursor.getColumnIndexOrThrow("max_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return maxId+1;
    }
}