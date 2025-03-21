package com.deltacodex.EventPulse.SQLite_DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "users";
    private static final String COL_ID = "id";
    private static final String COL_FULLNAME = "fullName";
    private static final String COL_MOBILE = "mobile";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FULLNAME + " TEXT NOT NULL, " +
                COL_MOBILE + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Register a new user
    public boolean registerUser(String fullName, String mobile, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FULLNAME, fullName);
        values.put(COL_MOBILE, mobile);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Check if a user exists by their full name and password
    public boolean checkUserExists(String fullName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE fullName=? AND password=?", new String[]{fullName, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if a user exists during signup (by email or mobile)
    public boolean checkUserExists_Signup(String email, String mobile) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE email=? OR mobile=?", new String[]{email, mobile});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get email by full name (username)
    public String getEmailByUsername(String fullName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM " + TABLE_NAME + " WHERE fullName = ?", new String[]{fullName});

        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL)); // Fetch the email column correctly
            cursor.close();
            return email;
        }
        cursor.close();
        return null; // Return null if no email is found
    }
}
