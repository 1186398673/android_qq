package com.example.myapplication;

import android.app.Activity;
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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardDatabaseHelper2 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cards.db";
    private static final int DATABASE_VERSION = 3; // 更新版本号以触发 onUpgrade
    private static final int REQUEST_WRITE_STORAGE = 112;

    private static final int REQUEST_READ_STORAGE = 113;
    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CONTENT_DEFINE = "content_define";
    public static final String COLUMN_CONTENT_MEANING = "content_meaning";
    public static final String COLUMN_CONTENT_RANGE = "content_range";
    public static final String COLUMN_CONTENT_EXAMPLE = "content_example";
    public static final String COLUMN_PARENT_TITLE = "parentTitle";
    public static final String COLUMN_LEVEL = "level"; // 新增的等级列

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_CONTENT_DEFINE + " TEXT, " +
                    COLUMN_CONTENT_MEANING + " TEXT, " +
                    COLUMN_CONTENT_RANGE + " TEXT, " +
                    COLUMN_CONTENT_EXAMPLE + " TEXT, " +
                    COLUMN_PARENT_TITLE + " TEXT, " +
                    COLUMN_LEVEL + " INTEGER DEFAULT 1" + // 默认等级为1
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
        if (oldVersion < 2) {
            // 升级到版本2：添加新的 level 列，默认为1
            db.execSQL("ALTER TABLE " + TABLE_CARDS + " ADD COLUMN " + COLUMN_LEVEL + " INTEGER DEFAULT 1");
        }
        if (oldVersion < 3) {
            // 如果将来有更多版本升级，可以在这里添加更多逻辑
        }
    }

    // 插入卡片数据
    public void insertCard(CardItem2 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_CONTENT_DEFINE, card.getContentDefine());
        values.put(COLUMN_CONTENT_MEANING, card.getContentMeaning());
        values.put(COLUMN_CONTENT_RANGE, card.getContentRange());
        values.put(COLUMN_CONTENT_EXAMPLE, card.getContentExample());
        values.put(COLUMN_PARENT_TITLE, card.getParentTitle());
        values.put(COLUMN_LEVEL, card.getLevel()); // 添加等级
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentTitle 查询卡片列表
    public List<CardItem2> getCardsByParentTitle(String parentTitle) {
        List<CardItem2> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, new String[]{COLUMN_ID, COLUMN_CONTENT, COLUMN_CONTENT_DEFINE,
                        COLUMN_CONTENT_MEANING, COLUMN_CONTENT_RANGE, COLUMN_CONTENT_EXAMPLE, COLUMN_PARENT_TITLE, COLUMN_LEVEL},
                COLUMN_PARENT_TITLE + "=?", new String[]{parentTitle}, null, null, COLUMN_LEVEL + " DESC"); // 按等级升序排序

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
                card.setLevel(cursor.getInt(7)); // 设置等级
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
        values.put(COLUMN_LEVEL, card.getLevel()); // 添加等级

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

    public void CardItem2_exportToCSV(Context context) {


        // 获取卡片列表
        List<CardItem2> cardList = getAllCards();

        // 定义 CSV 文件的标题
        String[] headers = {"ID", "Content", "Content Define", "Content Meaning", "Content Range", "Content Example", "Parent Title", "Level"};

        // 获取外部存储的公共下载目录
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CardData2");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        // 定义 CSV 文件名
        File file = new File(exportDir, "CardDatabase2" + System.currentTimeMillis() + ".csv");

        try {
            FileWriter writer = new FileWriter(file);
            // 写入标题
            writer.append(String.join(",", headers));
            writer.append("\n");

            // 写入数据
            for (CardItem2 card : cardList) {
                writer.append(String.valueOf(card.getid()));
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContent()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContentDefine()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContentMeaning()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContentRange()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getContentExample()) + "\"");
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getParentTitle()) + "\"");
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

    // 获取所有卡片数据
    private List<CardItem2> getAllCards() {
        List<CardItem2> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARDS, null, null, null, null, null, COLUMN_LEVEL + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CardItem2 card = new CardItem2();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                card.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                card.setContentDefine(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT_DEFINE)));
                card.setContentMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT_MEANING)));
                card.setContentRange(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT_RANGE)));
                card.setContentExample(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT_EXAMPLE)));
                card.setParentTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_TITLE)));
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
    public void importFromCSV(Uri uri, final Context context) {
        final AtomicBoolean success = new AtomicBoolean(true);

        // 使用AsyncTask或其他异步方式处理导入操作，避免阻塞主线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                SQLiteDatabase db = null;
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    if (inputStream == null) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context, "无法打开文件", Toast.LENGTH_SHORT).show()
                        );
                        success.set(false);
                        return;
                    }

                    br = new BufferedReader(new InputStreamReader(inputStream));
                    CSVReader reader = new CSVReader(br);
                    String[] values;
                    boolean isFirstLine = true;
                    db = getWritableDatabase();
                    db.beginTransaction();
                    while ((values = reader.readNext()) != null) {
                        if (isFirstLine) {
                            // 跳过标题行
                            isFirstLine = false;
                            continue;
                        }

                        // 检查字段数量
                        if (values.length != 8) {
                            // 跳过格式不正确的行
                            continue;
                        }

                        // 解析每一行
                        int id;
                        try {
                            id = Integer.parseInt(values[0]);
                        } catch (NumberFormatException e) {
                            // 跳过ID格式不正确的行
                            continue;
                        }
                        String content = values[1];
                        String contentDefine = values[2];
                        String contentMeaning = values[3];
                        String contentRange = values[4];
                        String contentExample = values[5];
                        String parentTitle = values[6];
                        int level;
                        try {
                            level = Integer.parseInt(values[7]);
                        } catch (NumberFormatException e) {
                            // 跳过level格式不正确的行
                            continue;
                        }

                        // 插入到数据库
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_ID, id);
                        cv.put(COLUMN_CONTENT, content);
                        cv.put(COLUMN_CONTENT_DEFINE, contentDefine);
                        cv.put(COLUMN_CONTENT_MEANING, contentMeaning);
                        cv.put(COLUMN_CONTENT_RANGE, contentRange);
                        cv.put(COLUMN_CONTENT_EXAMPLE, contentExample);
                        cv.put(COLUMN_PARENT_TITLE, parentTitle);
                        cv.put(COLUMN_LEVEL, level);
                        db.insert(TABLE_CARDS, null, cv);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } catch (IOException | CsvValidationException e) {
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
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (success.get()) {
                        Toast.makeText(context, "数据已成功导入", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "导入失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    // 解析 CSV 行
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