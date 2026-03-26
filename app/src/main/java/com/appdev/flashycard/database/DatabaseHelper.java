package com.appdev.flashycard.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FlashyCard.db";
    private static final int DATABASE_VERSION = 3; // Incremented version for new tables

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL = "email";

    // Table Login
    public static final String TABLE_LOGIN = "login";
    public static final String COLUMN_LOGIN_ID = "login_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FK_USER_ID = "fk_user_id";

    // Table Flashcard Sets
    public static final String TABLE_FLASHCARD_SETS = "flashcard_sets";
    public static final String COLUMN_SET_ID = "set_id";
    public static final String COLUMN_SET_FK_USER_ID = "fk_user_id";
    public static final String COLUMN_SET_TITLE = "title";
    public static final String COLUMN_SET_DESCRIPTION = "description";
    public static final String COLUMN_SET_COLOR = "color_hex";

    // Table Flashcards
    public static final String TABLE_FLASHCARDS = "flashcards";
    public static final String COLUMN_CARD_ID = "card_id";
    public static final String COLUMN_CARD_FK_SET_ID = "fk_set_id";
    public static final String COLUMN_CARD_FK_USER_ID = "fk_user_id";
    public static final String COLUMN_CARD_TERM = "term";
    public static final String COLUMN_CARD_DEFINITION = "definition";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE" + ")";

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + COLUMN_LOGIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_FK_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_FK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

        String CREATE_SETS_TABLE = "CREATE TABLE " + TABLE_FLASHCARD_SETS + "("
                + COLUMN_SET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SET_FK_USER_ID + " INTEGER,"
                + COLUMN_SET_TITLE + " TEXT,"
                + COLUMN_SET_DESCRIPTION + " TEXT,"
                + COLUMN_SET_COLOR + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_SET_FK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

        String CREATE_FLASHCARDS_TABLE = "CREATE TABLE " + TABLE_FLASHCARDS + "("
                + COLUMN_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CARD_FK_SET_ID + " INTEGER,"
                + COLUMN_CARD_FK_USER_ID + " INTEGER,"
                + COLUMN_CARD_TERM + " TEXT,"
                + COLUMN_CARD_DEFINITION + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CARD_FK_SET_ID + ") REFERENCES " + TABLE_FLASHCARD_SETS + "(" + COLUMN_SET_ID + "),"
                + "FOREIGN KEY(" + COLUMN_CARD_FK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_SETS_TABLE);
        db.execSQL(CREATE_FLASHCARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARD_SETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Signs up a new user.
     * @return User ID if successful, -1 if email exists, -2 if other error.
     */
    public long signUp(String fullName, String email, String username, String password) {
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return -2;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if email already exists
        if (checkEmailExists(email)) {
            return -1;
        }

        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_FULL_NAME, fullName);
            userValues.put(COLUMN_EMAIL, email);
            long userId = db.insert(TABLE_USERS, null, userValues);

            if (userId != -1) {
                ContentValues loginValues = new ContentValues();
                loginValues.put(COLUMN_USERNAME, username);
                loginValues.put(COLUMN_PASSWORD, hashPassword(password));
                loginValues.put(COLUMN_FK_USER_ID, userId);
                long loginId = db.insert(TABLE_LOGIN, null, loginValues);

                if (loginId != -1) {
                    db.setTransactionSuccessful();
                    return userId;
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error during sign up", e);
        } finally {
            db.endTransaction();
        }
        return -2;
    }

    /**
     * Validates user credentials using email.
     * @return true if credentials are valid, false otherwise.
     */
    public boolean loginWithEmail(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        String query = "SELECT l." + COLUMN_USERNAME + " FROM " + TABLE_LOGIN + " l"
                + " JOIN " + TABLE_USERS + " u ON l." + COLUMN_FK_USER_ID + " = u." + COLUMN_USER_ID
                + " WHERE u." + COLUMN_EMAIL + " = ? AND l." + COLUMN_PASSWORD + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{email, hashedPassword});
        boolean success = cursor.getCount() > 0;
        cursor.close();
        return success;
    }

    /**
     * Get user details by email
     */
    public Cursor getUserDetailsByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COLUMN_USER_ID + ", u." + COLUMN_FULL_NAME + ", u." + COLUMN_EMAIL + ", l." + COLUMN_USERNAME
                + " FROM " + TABLE_USERS + " u"
                + " INNER JOIN " + TABLE_LOGIN + " l ON u." + COLUMN_USER_ID + " = l." + COLUMN_FK_USER_ID
                + " WHERE u." + COLUMN_EMAIL + " = ?";
        return db.rawQuery(query, new String[]{email});
    }

    private boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check email in users table
        Cursor cursorEmail = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean emailExists = cursorEmail.getCount() > 0;
        cursorEmail.close();

        return emailExists;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("DatabaseHelper", "Hashing algorithm not found", e);
            return password;
        }
    }

    // Flashcard Methods

    public long addFlashcardSet(long userId, String title, String description, String colorHex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SET_FK_USER_ID, userId);
        values.put(COLUMN_SET_TITLE, title);
        values.put(COLUMN_SET_DESCRIPTION, description);
        values.put(COLUMN_SET_COLOR, colorHex);
        return db.insert(TABLE_FLASHCARD_SETS, null, values);
    }

    public long addFlashcard(long setId, long userId, String term, String definition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_FK_SET_ID, setId);
        values.put(COLUMN_CARD_FK_USER_ID, userId);
        values.put(COLUMN_CARD_TERM, term);
        values.put(COLUMN_CARD_DEFINITION, definition);
        return db.insert(TABLE_FLASHCARDS, null, values);
    }

    public int updateFlashcard(long cardId, String term, String definition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_TERM, term);
        values.put(COLUMN_CARD_DEFINITION, definition);
        return db.update(TABLE_FLASHCARDS, values, COLUMN_CARD_ID + "=?", new String[]{String.valueOf(cardId)});
    }

    public void deleteFlashcard(long cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLASHCARDS, COLUMN_CARD_ID + "=?", new String[]{String.valueOf(cardId)});
    }

    public Cursor getSetsByUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FLASHCARD_SETS, null, COLUMN_SET_FK_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Cursor getCardsBySet(long setId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FLASHCARDS, null, COLUMN_CARD_FK_SET_ID + "=?", new String[]{String.valueOf(setId)}, null, null, null);
    }
}
