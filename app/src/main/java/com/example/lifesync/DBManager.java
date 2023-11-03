package com.example.lifesync;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DatabaseHelper dbHelper;

    final private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertActivityTask(int day, String activityName, int targetValue, boolean enable) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.DAY, day);
        contentValue.put(DatabaseHelper.ACTIVITY_NAME, activityName);
        contentValue.put(DatabaseHelper.TARGET_VALUE, targetValue);
        contentValue.put(DatabaseHelper.ENABLE, enable);
        database.insert(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, null, contentValue);
    }

    public List<ActivityTask> fetchActivityTasks(int targetDay) {
        List<ActivityTask> activityTasks = new ArrayList<ActivityTask>();

        String[] columns = new String[]{DatabaseHelper.ID, DatabaseHelper.DAY, DatabaseHelper.ACTIVITY_NAME, DatabaseHelper.TARGET_VALUE, DatabaseHelper.ENABLE};
        Cursor cursor = database.query(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DAY));
            @SuppressLint("Range") String activityName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIVITY_NAME));
            @SuppressLint("Range") int targetValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TARGET_VALUE));
            @SuppressLint("Range") int enable = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENABLE));

            if (enable == 1 && day == targetDay) {
                ActivityClass activityClass = ActivityClass.getClassFromString(activityName);
                activityTasks.add(new ActivityTask(id, activityClass, 0, targetValue));
                // Do something with the fetched data (e.g., display it)
                Log.d("Activity Tasks", "ID: " + id + ", Day: " + day + ", Activity Name: " + activityName + ", Target Value: " + targetValue + ", Enable: " + enable);

            }
        }
//        cursor.close();
//        return cursor;
        return activityTasks;
    }

    public int updateActivityTask(int id, String activityName, int targetValue) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ACTIVITY_NAME, activityName);
        contentValue.put(DatabaseHelper.TARGET_VALUE, targetValue);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public int updateActivityTask(int id, String activityName ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ACTIVITY_NAME, activityName);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public int updateActivityTask(int id , int targetValue) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TARGET_VALUE, targetValue);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public int removeActivityTask(int id) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ENABLE, 0);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

//    public void delete(long _id) {
//        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
//    }

}
