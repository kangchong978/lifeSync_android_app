package com.example.lifesync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String ACTIVITY_TASKS_TABLE_NAME = "ACTIVITY_TASK";

    // Table columns
    public static final String ID = "id";
    public static final String DAY = "day";
    public static final String ACTIVITY_NAME = "activityName";
    public static final String TARGET_VALUE = "targetValue";
    public static final String ENABLE = "enable";

    public static final String ACTIVITY_RECORDS_TABLE_NAME = "ACTIVITY_RECORDS";

    public static final String RAW_SENSOR_VALUE = "rawSensorValue";
    public static final String STEPS_VALUE = "stepsValue";
    public static final String BMI_VALUE = "bmiValue";
    public static final String DISTANCE_VALUE = "distanceValue";

    public static final String CALORIES_BURNED_VALUE = "caloriesBurnedValue";
    public static final String TIMESTAMP = "timestamp";
    public static final String ACTIVITY_TASK_IDS = "activityTaskIds";

    public static final String USER_INFO_TABLE_NAME = "USER_INFO";


    public static final String NAME = "name";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";

    public static final String AGE = "age";
    public static final String GENDER = "gender";


    // Database Information
    static final String DB_NAME = "database.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_ACTIVITY_TASKS_TABLE = "CREATE TABLE " + ACTIVITY_TASKS_TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DAY + " INTEGER, "
            + ACTIVITY_NAME + " TEXT, "
            + TARGET_VALUE + " INTEGER, "
            + ENABLE + " BOOLEAN"
            + ");";
    private static final String CREATE_ACTIVITY_RECORDS_TABLE = "CREATE TABLE " + ACTIVITY_RECORDS_TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RAW_SENSOR_VALUE + " INTEGER, "
            + STEPS_VALUE + " INTEGER, "
            + BMI_VALUE + " Double, "
            + DISTANCE_VALUE + " DOUBLE, "
            + CALORIES_BURNED_VALUE + " DOUBLE, "
            + TIMESTAMP + " INTEGER, "
            + ACTIVITY_TASK_IDS + " TEXT "
            + ");";

    private static final String CREATE_USER_INFO_TABLE = "CREATE TABLE " + USER_INFO_TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + HEIGHT + " DOUBLE, "
            + WEIGHT + " DOUBLE, "
            + AGE + " INTEGER, "
            + GENDER + " TEXT "
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACTIVITY_TASKS_TABLE);
        db.execSQL(CREATE_ACTIVITY_RECORDS_TABLE);
        db.execSQL(CREATE_USER_INFO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TASKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_RECORDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_INFO_TABLE_NAME);
        onCreate(db);
    }
}

