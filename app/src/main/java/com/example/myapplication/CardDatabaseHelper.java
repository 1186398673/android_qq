package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.CardItem;

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

public class CardDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CardDatabase.db";
    private static final int DATABASE_VERSION = 2; // 更新版本号以触发 onUpgrade
    private static final int REQUEST_WRITE_STORAGE = 112;
    // 表名和列名
    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ICON_ID = "icon_id";
    public static final String COLUMN_LEVEL = "level"; // 新增的等级列

    // 创建表的SQL语句
    private static final String CREATE_TABLE_CARDS =
            "CREATE TABLE " + TABLE_CARDS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_ICON_ID + " INTEGER,"
                    + COLUMN_LEVEL + " INTEGER DEFAULT 1" // 默认等级为1
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
        if (oldVersion < 2) {
            // 升级到版本2：添加新的 level 列，默认为1
            db.execSQL("ALTER TABLE " + TABLE_CARDS + " ADD COLUMN " + COLUMN_LEVEL + " INTEGER DEFAULT 1");
        }
        // 如果将来有更多版本升级，可以在这里添加更多逻辑
    }

    // 插入卡片数据
    public long insertCard(CardItem card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, card.getTitle());
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_ICON_ID, card.getIconResId());
        values.put(COLUMN_LEVEL, card.getLevel()); // 添加等级
        long id = db.insert(TABLE_CARDS, null, values);
        db.close();
        return id;
    }

    // 查询所有卡片数据
    public List<CardItem> getCardList() {
        List<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null, COLUMN_LEVEL + " DESC"); // 按等级升序排序
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                int iconId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON_ID));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL));
                cardList.add(new CardItem(id, title, content, iconId, level));
            }
            cursor.close();
        }
        db.close();
        return cardList;
    }

    // 删除卡片
    public boolean deleteCardById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CARDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 删除所有卡片
    public int deleteAllCards() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CARDS, "1", null);
        db.close();
        return rowsDeleted;
    }

    // 重命名卡片
    public boolean renameCard(int id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newTitle);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
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

    // 更新卡片等级
    public boolean updateCardLevel(int id, int newLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // 根据等级排序查询卡片
    public List<CardItem> getCardListSortedByLevel() {
        List<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null, COLUMN_LEVEL + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                int iconId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON_ID));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL));
                cardList.add(new CardItem(id, title, content, iconId, level));
            }
            cursor.close();
        }
        db.close();
        return cardList;
    }

    public void CardItem_exportToCSV(Context context) {
        // 检查权限

        // 获取卡片列表
        List<CardItem> cardList = getCardList();

        // 定义 CSV 文件的标题
        String[] headers = {"ID", "Title", "Content", "Icon ID", "Level"};

        // 获取外部存储的公共下载目录
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CardData");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        // 定义 CSV 文件名
        File file = new File(exportDir, "CardDatabase1"+ System.currentTimeMillis()+".csv");

        try {
            FileWriter writer = new FileWriter(file);
            // 写入标题
            writer.append(String.join(",", headers));
            writer.append("\n");

            // 写入数据
            for (CardItem card : cardList) {
                writer.append(String.valueOf(card.getId()));
                writer.append(",");
                writer.append("\"" + card.getTitle().replace("\"", "\"\"") + "\"");
                writer.append(",");
                writer.append("\"" + card.getContent().replace("\"", "\"\"") + "\"");
                writer.append(",");
                writer.append(String.valueOf(card.getIconResId()));
                writer.append(",");
                writer.append(String.valueOf(card.getLevel()));
                writer.append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(context, "数据已成功导出到 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    // 在类的顶部导入 AtomicBoolean
    public void importFromCSV(Uri uri,Context context) {

        final AtomicBoolean success = new AtomicBoolean(true);

        // 使用 AsyncTask 或其他异步方式处理导入操作，避免阻塞主线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedReader br = null;
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
                    CardDatabaseHelper dbHelper = new CardDatabaseHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
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

                        int id = Integer.parseInt(values[0]);
                        String title = values[1];
                        String content = values[2];
                        int iconId = Integer.parseInt(values[3]);
                        int level = Integer.parseInt(values[4]);

                        // 检查记录是否已存在，根据标题确认
                        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID},
                                COLUMN_TITLE + "=?", new String[]{title}, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                // 记录已存在，跳过插入
                                cursor.close();
                                continue;
                            }
                            cursor.close();
                        }

                        // 插入到数据库
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_ID, id);
                        cv.put(COLUMN_TITLE, title);
                        cv.put(COLUMN_CONTENT, content);
                        cv.put(COLUMN_ICON_ID, iconId);
                        cv.put(COLUMN_LEVEL, level);
                        db.insert(TABLE_CARDS, null, cv);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                    success.set(false);
                } finally {
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
                        if (success.get() ) {
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
    // 获取当前数据库中最大的 ID
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