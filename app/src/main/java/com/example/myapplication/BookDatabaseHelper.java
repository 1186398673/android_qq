package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_BOOKS = "books";

    // 列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FILE_PATH = "file_path";
    public static final String COLUMN_COVER = "cover";

    public BookDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_COVER + " BLOB" + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        // 创建新表
        onCreate(db);
    }

    /**
     * 插入一本书到数据库
     *
     * @param book 要插入的书籍
     * @return 插入的行 ID，如果失败则返回 -1
     */
    public long insertBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_FILE_PATH, book.getfilePath());
        // 将 Bitmap 转换为字节数组
        if (book.getCoverResId() != null) {
            values.put(COLUMN_COVER, getBytes(book.getCoverResId()));
        }

        long id = db.insert(TABLE_BOOKS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有书籍
     *
     * @return 书籍列表
     */
    @SuppressLint("Range")
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                book.setFilePath(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_PATH)));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_COVER));
                if (blob != null) {
                    book.setCoverResId(BitmapFactory.decodeByteArray(blob, 0, blob.length));
                }
                bookList.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookList;
    }

    /**
     * 删除一本书籍
     *
     * @param id 书籍的 ID
     * @return 删除是否成功
     */
    public boolean deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRows = db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return affectedRows > 0;
    }

    /**
     * 将 Bitmap 转换为字节数组
     *
     * @param bitmap 要转换的 Bitmap
     * @return 字节数组
     */
    private byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * 将字节数组转换为 Bitmap
     *
     * @param image 字节数组
     * @return Bitmap
     */
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
