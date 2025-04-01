package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardDatabaseHelper3 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cards2.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PARENT_ID = "parentId";
    public static final String COLUMN_PARENT_TILE = "parentTile";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_IMAGE_URL = "imageUrl";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_PARENT_ID + " INTEGER, " +
                    COLUMN_PARENT_TILE + " TEXT, " +
                    COLUMN_LEVEL + " INTEGER DEFAULT 1, " +
                    COLUMN_IMAGE_URL + " TEXT" +
                    ");";

    public CardDatabaseHelper3(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 插入卡片数据
    public void insertCard(CardItem3 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_ID, card.getParentid());
        values.put(COLUMN_PARENT_TILE, card.getParenttile());
        values.put(COLUMN_LEVEL, card.getLevel());
        values.put(COLUMN_IMAGE_URL, card.getImageUrl());
        db.insert(TABLE_CARDS, null, values);
        db.close();
    }

    // 根据 parentId 查询卡片列表
    public List<CardItem3> getCardsByParentId(String parentId) {
        List<CardItem3> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PARENT_ID + " = ?";
        String[] selectionArgs = { parentId };
        Cursor cursor = db.query(TABLE_CARDS, null, selection, selectionArgs, null, null, COLUMN_LEVEL + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CardItem3 card = new CardItem3();
                card.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                card.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                card.setParentid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_ID)));
                card.setParenttile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_TILE)));
                card.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                card.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                cardList.add(card);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return cardList;
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

    // 更新 parentId
    public int updateParentId(String oldParentId, String newParentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PARENT_ID, newParentId);
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_PARENT_ID + " = ?", new String[]{oldParentId});
        db.close();
        return rowsAffected;
    }

    // 更新卡片的所有字段
    public boolean updateCard(CardItem3 card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, card.getContent());
        values.put(COLUMN_PARENT_ID, card.getParentid());
        values.put(COLUMN_PARENT_TILE, card.getParenttile());
        values.put(COLUMN_LEVEL, card.getLevel());
        values.put(COLUMN_IMAGE_URL, card.getImageUrl());
        int rowsAffected = db.update(TABLE_CARDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(card.getid())});
        db.close();
        return rowsAffected > 0;
    }

    // 导出数据为 CSV 文件
    public void CardItem3_exportToCSV(Context context) {
        List<CardItem3> cardList = getAllCards();
        String[] headers = {"ID", "Content", "Parent ID", "Parent Tile", "Level", "Image URL"};

        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CardData3");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "CardDatabase3" + System.currentTimeMillis() + ".csv");

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(String.join(",", headers));
            writer.append("\n");

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
                writer.append(",");
                writer.append("\"" + escapeSpecialCharacters(card.getImageUrl()) + "\"");
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
                card.setParenttile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_TILE)));
                card.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                card.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                SQLiteDatabase db = null;
                CSVReader csvReader = null;
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

                    csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    String[] values;
                    boolean isFirstLine = true;
                    db = getWritableDatabase();
                    db.beginTransaction();
                    while ((values = csvReader.readNext()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        if (values.length != 6) {
                            continue;
                        }

                        int id;
                        try {
                            id = Integer.parseInt(values[0]);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        String content = values[1];
                        String parentId = values[2];
                        String parentTile = values[3];
                        int level;
                        try {
                            level = Integer.parseInt(values[4]);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        String imageUrl = values[5];

                        CardItem3 card = new CardItem3(content, parentId, id, level, parentTile,imageUrl);
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_CONTENT, card.getContent());
                        cv.put(COLUMN_PARENT_ID, card.getParentid());
                        cv.put(COLUMN_PARENT_TILE, card.getParenttile());
                        cv.put(COLUMN_LEVEL, card.getLevel());
                        cv.put(COLUMN_IMAGE_URL, card.getImageUrl());
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
                    if (csvReader != null) {
                        try {
                            csvReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

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

    // 保存图片到外部存储
    public Uri saveImageToExternalStorage(Uri imageUri, Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "外部存储不可用", Toast.LENGTH_SHORT).show();
            return null;
        }

        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "CardData3_pic");
        if (!exportDir.exists()) {
            boolean wasCreated = exportDir.mkdirs();
            if (!wasCreated) {
                Toast.makeText(context, "无法创建目录", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            String fileName = "image_" + System.currentTimeMillis() + ".png";
            File file = new File(exportDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Uri fileUri = Uri.fromFile(file);
            return fileUri;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "保存图片失败", Toast.LENGTH_SHORT).show();
            return null;
        }
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
        return maxId + 1;
    }
}