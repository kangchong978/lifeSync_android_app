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

    public void insertUserInfo(String name, int weight, int height, int age) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.WEIGHT, weight);
        contentValue.put(DatabaseHelper.HEIGHT, height);
        contentValue.put(DatabaseHelper.AGE, age);
        database.insert(DatabaseHelper.USER_INFO_TABLE_NAME, null, contentValue);
    }

    public void insertActivityRecord(int raw, int steps, int bmi, double distance, int calories, int timestamp, String taskIds) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.RAW_SENSOR_VALUE, raw);
        contentValue.put(DatabaseHelper.STEPS_VALUE, steps);
        contentValue.put(DatabaseHelper.BMI_VALUE, bmi);
        contentValue.put(DatabaseHelper.DISTANCE_VALUE, distance);
        contentValue.put(DatabaseHelper.CALORIES_BURNED_VALUE, calories);
        contentValue.put(DatabaseHelper.TIMESTAMP, timestamp);
        contentValue.put(DatabaseHelper.ACTIVITY_TASK_IDS, taskIds);
        database.insert(DatabaseHelper.ACTIVITY_RECORDS_TABLE_NAME, null, contentValue);
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
        return activityTasks;
    }

    public ActivityTask fetchActivityTaskById(int taskId) {
        ActivityTask activityTask = null;

        String[] columns = new String[]{DatabaseHelper.ID, DatabaseHelper.DAY, DatabaseHelper.ACTIVITY_NAME, DatabaseHelper.TARGET_VALUE, DatabaseHelper.ENABLE};
        String selection = DatabaseHelper.ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(taskId)};

        Cursor cursor = database.query(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DAY));
            @SuppressLint("Range") String activityName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIVITY_NAME));
            @SuppressLint("Range") int targetValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TARGET_VALUE));
            @SuppressLint("Range") int enable = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENABLE));

            ActivityClass activityClass = ActivityClass.getClassFromString(activityName);
            activityTask = new ActivityTask(id, activityClass, 0, targetValue);
            // Do something with the fetched data (e.g., display it)
            Log.d("Activity Task", "ID: " + id + ", Day: " + day + ", Activity Name: " + activityName + ", Target Value: " + targetValue + ", Enable: " + enable);
        }

        cursor.close();
        return activityTask;
    }

    public UserInfo fetchUserInfo() {
        String[] columns = new String[]{DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.WEIGHT, DatabaseHelper.HEIGHT, DatabaseHelper.AGE};
        Cursor cursor = database.query(
                DatabaseHelper.USER_INFO_TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                DatabaseHelper.ID + " DESC",
                "1"
        );

        UserInfo userInfo = null; // Initialize as null

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            @SuppressLint("Range") int weight = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEIGHT));
            @SuppressLint("Range") int height = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.HEIGHT));
            @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.AGE));

            userInfo = new UserInfo(id, name, weight, height, age);
        }

        if (cursor != null) {
            cursor.close(); // Close the cursor when you're done with it
        }

        return userInfo;
    }

    public List<HistoryActivity> fetchActivityRecords() {
        List<HistoryActivity> activityHistories = new ArrayList<HistoryActivity>();

        String[] columns = new String[]{DatabaseHelper.ID, DatabaseHelper.RAW_SENSOR_VALUE, DatabaseHelper.STEPS_VALUE, DatabaseHelper.BMI_VALUE, DatabaseHelper.DISTANCE_VALUE, DatabaseHelper.CALORIES_BURNED_VALUE, DatabaseHelper.TIMESTAMP, DatabaseHelper.ACTIVITY_TASK_IDS};
        Cursor cursor = database.query(DatabaseHelper.ACTIVITY_RECORDS_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
            @SuppressLint("Range") int timestamp = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TIMESTAMP));
            @SuppressLint("Range") int stepsValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STEPS_VALUE));
            @SuppressLint("Range") int bmiValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.BMI_VALUE));
            @SuppressLint("Range") int distanceValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DISTANCE_VALUE));
            @SuppressLint("Range") int caloriesValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CALORIES_BURNED_VALUE));
            @SuppressLint("Range") String taskIdsString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIVITY_TASK_IDS));
            String[] taskIds = taskIdsString.split(",");


            //            @SuppressLint("Range") int day = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DAY));
//            @SuppressLint("Range") String activityName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIVITY_NAME));
//            @SuppressLint("Range") int targetValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TARGET_VALUE));
//            @SuppressLint("Range") int enable = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENABLE));
//
//            if (enable == 1 && day == targetDay) {
//                ActivityClass activityClass = ActivityClass.getClassFromString(activityName);
//                activityTasks.add(new ActivityTask(id, activityClass, 0, targetValue));
//                // Do something with the fetched data (e.g., display it)
//                Log.d("Activity Tasks", "ID: " + id + ", Day: " + day + ", Activity Name: " + activityName + ", Target Value: " + targetValue + ", Enable: " + enable);
//
//            }
            List<ActivityTask> activityTasks = new ArrayList<ActivityTask>();
            for (int i = 0; i < taskIds.length; i++) {
                if(taskIds[i] != ""){
                    ActivityTask activityTask = fetchActivityTaskById(Integer.parseInt(taskIds[i]));

                    int value = 0;
                    switch ( activityTask.getActivityClass()){
                        case Unknown:
                            break;
                        case Steps:
                            value = stepsValue;
                            break;
                        case Distance:
                            value =  distanceValue;
                            break;
                        case BMI:
                            value =  bmiValue;
                            break;
                        case CaloriesBurned:
                            value = caloriesValue;
                            break;
                    }
                    
                    activityTask.setValue(value);
                    activityTasks.add(activityTask);
                }
            }


            HistoryActivity historyActivity = new HistoryActivity(timestamp, activityTasks);
            activityHistories.add(historyActivity);
        }
        return activityHistories;
    }

    public int updateActivityTask(int id, String activityName, int targetValue) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ACTIVITY_NAME, activityName);
        contentValue.put(DatabaseHelper.TARGET_VALUE, targetValue);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public int updateActivityTask(int id, String activityName) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ACTIVITY_NAME, activityName);
        int i = database.update(DatabaseHelper.ACTIVITY_TASKS_TABLE_NAME, contentValue, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public int updateActivityTask(int id, int targetValue) {
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
