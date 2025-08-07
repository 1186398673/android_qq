package com.example.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    // 数据库名称和版本
    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1; // 更新版本号

    // 表名和列名
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMPLETED = "completed"; // 新增列

    // 创建表SQL语句（更新后的）
    private static final String CREATE_TABLE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_DATE + " INTEGER, " +
                    COLUMN_COMPLETED + " INTEGER DEFAULT 0)"; // 默认值为0（未完成）

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 添加任务
    public long insertTask(TaskItem task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DATE, task.getDate().getTime());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0); // 存储布尔值为整数

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();

        return id; // 返回新插入行的ID
    }

    // 删除任务
    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(TABLE_TASKS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)}) > 0;
        db.close();
        return result;
    }

    // 更新任务
    public boolean updateTask(TaskItem task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DATE, task.getDate().getTime());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        boolean result = db.update(TABLE_TASKS, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())}) > 0;
        db.close();
        return result;
    }

    // 获取所有任务
    public List<TaskItem> getAllTasks() {
        List<TaskItem> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
            null, 
            null, 
            null, 
            null, 
            null, 
            COLUMN_COMPLETED + " ASC"); // 新增排序条件

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskItem task = new TaskItem(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)),
                        new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1 // 映射为布尔值
                );
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return taskList;
    }

    // 按日期获取任务
    public List<TaskItem> getTasksByDate(Date date) {
        List<TaskItem> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 转换为当天开始和结束的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endTime = calendar.getTimeInMillis();

        // 查询当天任务
        Cursor cursor = db.query(TABLE_TASKS,
                null,
                COLUMN_DATE + " >= ? AND " + COLUMN_DATE + " < ?",
                new String[]{String.valueOf(startTime), String.valueOf(endTime)},
                null, 
                null, 
                COLUMN_COMPLETED + " ASC");  // 新增排序条件

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskItem task = new TaskItem(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)),
                        new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1 // 映射为布尔值
                );
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return taskList;
    }

    
}