package com.s23010509.nutrigainsfitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NutriGains.db";
    public static final String TABLE_NAME = "user_profiles";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "AGE";
    public static final String COL_4 = "GENDER";
    public static final String COL_5 = "HEIGHT";
    public static final String COL_6 = "WEIGHT";
    public static final String COL_7 = "GOAL";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert profile data
    public boolean insertProfileData(String name, String age, String gender,
                                     String height, String weight, String goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, name);
        contentValues.put(COL_3, age);
        contentValues.put(COL_4, gender);
        contentValues.put(COL_5, height);
        contentValues.put(COL_6, weight);
        contentValues.put(COL_7, goal);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Returns true if inserted successfully
    }

    // Get all profiles (optional - for future use)
    public Cursor getAllProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Update profile (optional - for future use)
    public boolean updateProfile(String id, String name, String age, String gender,
                                 String height, String weight, String goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, name);
        contentValues.put(COL_3, age);
        contentValues.put(COL_4, gender);
        contentValues.put(COL_5, height);
        contentValues.put(COL_6, weight);
        contentValues.put(COL_7, goal);

        int result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return result > 0;
    }

    // Delete profile (optional - for future use)
    public Integer deleteProfile(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}