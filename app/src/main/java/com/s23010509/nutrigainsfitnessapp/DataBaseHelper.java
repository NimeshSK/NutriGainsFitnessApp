package com.s23010509.nutrigainsfitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NutriGains.db";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROFILES = "user_profiles";

    // Users table columns
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_PASSWORD = "PASSWORD";

    // Profiles table columns
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "AGE";
    public static final String COL_4 = "GENDER";
    public static final String COL_5 = "HEIGHT";
    public static final String COL_6 = "WEIGHT";
    public static final String COL_7 = "GOAL";
    public static final String COL_USER_REF = "USER_ID";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");

        //profiles table
        db.execSQL("CREATE TABLE " + TABLE_PROFILES + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT, " +
                COL_7 + " TEXT, " +
                COL_USER_REF + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        onCreate(db);
    }

    // Insert user account
    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    // Insert profile data
    public boolean insertProfileData(String name, String age, String gender,
                                     String height, String weight, String goal, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, age);
        contentValues.put(COL_4, gender);
        contentValues.put(COL_5, height);
        contentValues.put(COL_6, weight);
        contentValues.put(COL_7, goal);
        contentValues.put(COL_USER_REF, userId);
        long result = db.insert(TABLE_PROFILES, null, contentValues);
        return result != -1;
    }

    // Check if email exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get user by email and password
    public Cursor getUserByEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{email, password});
    }

    // Get profile by user ID
    public Cursor getProfileByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROFILES + " WHERE " + COL_USER_REF + " = ?",
                new String[]{String.valueOf(userId)});
    }

    // Get user ID by email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?",
                new String[]{email});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            cursor.close();
        }
        return userId;
    }

    // Get all profiles
    public Cursor getAllProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROFILES, null);
    }

    // Update profile
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
        int result = db.update(TABLE_PROFILES, contentValues, "ID = ?", new String[]{id});
        return result > 0;
    }

    // Delete profile
    public Integer deleteProfile(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PROFILES, "ID = ?", new String[]{id});
    }
}