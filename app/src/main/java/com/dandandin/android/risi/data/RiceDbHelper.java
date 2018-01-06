package com.dandandin.android.risi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dandandin.android.risi.data.RiceContract.RiceEntry;

public class RiceDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = RiceDbHelper.class.getSimpleName();
    /** Name of the database file */
    private static final String DATABASE_NAME = "risi.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link RiceDbHelper}.
     *
     * @param context of the app
     */
    public RiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_RICE_TABLE =  "CREATE TABLE " + RiceEntry.TABLE_NAME + " ("
                + RiceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RiceEntry.COLUMN_RICE_NAME + " TEXT NOT NULL, "
                + RiceEntry.COLUMN_BREED + " TEXT, "
                + RiceEntry.COLUMN_PACKAGING + " INTEGER NOT NULL, "
                + RiceEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_RICE_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
