package com.dandandin.android.risi.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.dandandin.android.risi.data.RiceContract.RiceEntry;

public class RiceProvider extends ContentProvider {
    /** URI matcher code for the content URI for the rice table */
    private static final int RICES = 100;

    /** URI matcher code for the content URI for a single rice in the rices table */
    private static final int RICE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #RICES}. This URI is used to provide access to MULTIPLE rows
        // of the rice table.
        sUriMatcher.addURI(RiceContract.CONTENT_AUTHORITY, RiceContract.PATH_RICE, RICES);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #RICE_ID}. This URI is used to provide access to ONE single row
        // of the rice table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.dandandin.android.risi/rice/3" matches, but
        // "content://com.dandandin.android.risi/rice" (without a number at the end) doesn't match.
        sUriMatcher.addURI(RiceContract.CONTENT_AUTHORITY, RiceContract.PATH_RICE + "/#", RICE_ID);
    }

    /** Database helper object */
    private RiceDbHelper mDbHelper;

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        mDbHelper = new RiceDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RICES:
                // For the RICES code, query the risi table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the risi table.
                cursor = database.query(RiceEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case RICE_ID:
                // For the RICE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.dandandin.android.risi/rice/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = RiceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the risi table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(RiceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    //Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    //Delete the data at the given selection and selection arguments.
    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    //Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}