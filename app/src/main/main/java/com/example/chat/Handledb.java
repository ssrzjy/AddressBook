package com.example.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Handledb extends SQLiteOpenHelper {
    // TABLE INFORMATTION
    static final String DB_NAME = "CONTRACT_INFORMATION.DB";
    public final String TABLE_KEY_VALUE = "key_value_pairs";
    public final String KEY = "keyname";
    public final String NAME = "name";
    public final String NUMBER = "phone"; // number

    public final String SORT = "sort";
    public final String EMAIL = "email";

    public final String NOTES = "notes";

    public final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_KEY_VALUE;

    //
    public Handledb(Context context) {
        super(context, DB_NAME, null, 1);
    }

//    public Handledb(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        createKeyValueTable(db);
    }

    private void createKeyValueTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + TABLE_KEY_VALUE + " (" + KEY
                    + " TEXT, " + NAME + " TEXT, " + NUMBER + " TEXT, " + SORT + " TEXT, " + EMAIL + " TEXT, " + NOTES + " TEXT)");
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //createKeyValueTable(db);
        // SQL statements to change the database schema
    }

    private void handleError(SQLiteDatabase db, Exception e) {
        String errorMsg = e.getMessage();
        if (errorMsg.contains("no such table")) {
            if (errorMsg.contains(TABLE_KEY_VALUE)) {
                createKeyValueTable(db);
            }
        }
    }

    public Cursor execute(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e) {
            //e.printStackTrace();
            handleError(db, e);
            res = db.rawQuery(query, null);
        }
        return res;
    }

    public Boolean insertKeyValue(String key, String name,String number,String sort, String email,  String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY, key);
        cv.put(NAME, name);
        cv.put(NUMBER, number);
        cv.put(SORT, sort);
        cv.put(EMAIL, email);
        cv.put(NOTES, notes);
        long result;
        try {
            result = db.insert(TABLE_KEY_VALUE, null, cv);
        } catch (Exception e) {
            handleError(db, e);
            result = db.insert(TABLE_KEY_VALUE, null, cv);
        }
        return result != -1;
    }

    public Cursor getAllKeyValues() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_KEY_VALUE, null);
        return res;
    }

    public boolean updateValueByKey(String key, String name,String number,String sort, String email,  String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY, key);
        cv.put(NAME, name);
        cv.put(NUMBER, number);
        cv.put(SORT, sort);
        cv.put(EMAIL, email);
        cv.put(NOTES, notes);
        int nr = 0;
        try {
            nr = db.update(TABLE_KEY_VALUE, cv, KEY + "=?",
                    new String[]{key});
        } catch (Exception e) {
            handleError(db, e);
            try {
                nr = db.update(TABLE_KEY_VALUE, cv, KEY + "=?",
                        new String[]{key});
            } catch (Exception ex) {
            }
        }
        if (nr == 0) {
            insertKeyValue(key, name,number,sort, email, notes);
        }
        return true;
    }

    public Integer deleteDataByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int isDeleted = 0;
        try {
            isDeleted = db.delete(TABLE_KEY_VALUE, NAME + " = ?", new String[]{name});
        } catch (Exception e) {
            handleError(db, e);
            try {
                isDeleted = db.delete(TABLE_KEY_VALUE, KEY + " = ?", new String[]{name});
            } catch (Exception ex) {
            }
        }
        return isDeleted;
    }

    public Integer deleteDataByKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        int isDeleted = 0;
        try {
            isDeleted = db.delete(TABLE_KEY_VALUE, KEY + " = ?", new String[]{key});
        } catch (Exception e) {
            handleError(db, e);
            try {
                isDeleted = db.delete(TABLE_KEY_VALUE, KEY + " = ?", new String[]{key});
            } catch (Exception ex) {
            }
        }
        return isDeleted;
    }

    public Cursor getValueByKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_KEY_VALUE + " WHERE "
                    + KEY + "='" + key + "'", null);
        } catch (Exception e) {
            handleError(db, e);
            res = db.rawQuery("SELECT * FROM " + TABLE_KEY_VALUE + " WHERE "
                    + KEY + "='" + key + "'", null);
        }
        return res;
    }

    //
    public void deleteQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        try {
            db.execSQL(query);
        } catch (Exception e) {
            // e.printStackTrace();
            handleError(db, e);
            db.execSQL(query);
        }
    }
}
