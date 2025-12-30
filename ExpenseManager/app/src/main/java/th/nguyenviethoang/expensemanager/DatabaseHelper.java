package th.nguyenviethoang.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_manager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRANSACTION = "transactions";
    private static final String COL_ID = "id";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_CATEGORY = "category";
    private static final String COL_TYPE = "type";
    private static final String COL_NOTE = "note";
    private static final String COL_DATE = "date";

    // ✅ SINGLETON PATTERN
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TRANSACTION + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_AMOUNT + " REAL, "
                + COL_CATEGORY + " TEXT, "
                + COL_TYPE + " TEXT, "
                + COL_NOTE + " TEXT, "
                + COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }

    // ===== INSERT =====
    public long addTransaction(double amount, String category, String type, String note, String date) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_AMOUNT, amount);
            values.put(COL_CATEGORY, category);
            values.put(COL_TYPE, type);
            values.put(COL_NOTE, note);
            values.put(COL_DATE, date);
            return db.insert(TABLE_TRANSACTION, null, values);
        } finally {
            if (db != null) db.close();
        }
    }

    // ===== GET ALL TRANSACTIONS =====
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " ORDER BY " + COL_ID + " DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    Transaction t = new Transaction();
                    t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    t.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)));
                    t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                    t.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                    t.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE)));
                    t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                    list.add(t);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return list;
    }

    // ===== GET TRANSACTION BY ID =====
    public Transaction getTransactionById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Transaction t = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_TRANSACTION,
                    null,
                    COL_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                t = new Transaction();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                t.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)));
                t.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                t.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                t.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE)));
                t.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
            }
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return t;
    }

    // ===== DELETE =====
    public void deleteTransaction(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_TRANSACTION, COL_ID + "=?", new String[]{String.valueOf(id)});
        } finally {
            if (db != null) db.close();
        }
    }

    // ===== TOTAL BY TYPE =====
    public double getTotalByType(String type) {
        double total = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTION + " WHERE " + COL_TYPE + "=?",
                    new String[]{type});
            if (cursor.moveToFirst()) total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return total;
    }

    // ===== TOTAL BY TYPE AND DATE LIKE =====
    public double getTotalByTypeAndDateLike(String type, String date) {
        double total = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTION +
                            " WHERE " + COL_TYPE + "=? AND " + COL_DATE + " LIKE ?",
                    new String[]{type, date + "%"});
            if (cursor.moveToFirst()) total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return total;
    }
}