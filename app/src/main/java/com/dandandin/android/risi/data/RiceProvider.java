package com.dandandin.android.risi.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.dandandin.android.risi.data.RiceContract.RiceEntry;

public class RiceProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = RiceProvider.class.getSimpleName();

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
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RICES:
                return RiceEntry.CONTENT_LIST_TYPE;
            case RICE_ID:
                return RiceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RICES:
                return insertRice(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a rice into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertRice(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(RiceEntry.COLUMN_RICE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Rice requires a name");
        }

        // Check that the packaging is valid
        Integer pack = values.getAsInteger(RiceEntry.COLUMN_PACKAGING);
        if (pack == null || !RiceEntry.isValidPackaging(pack)) {
            throw new IllegalArgumentException("Rice requires valid packaging");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(RiceEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Rice requires valid price");
        }

        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new rice with the given values
        long id = database.insert(RiceEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RICES:
                // Delete all rows that match the selection and selection args
                return database.delete(RiceEntry.TABLE_NAME, selection, selectionArgs);
            case RICE_ID:
                // Delete a single row given by the ID in the URI
                selection = RiceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(RiceEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    //Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RICES:
                return updateRice(uri, contentValues, selection, selectionArgs);
            case RICE_ID:
                // For the RICE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = RiceEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRice(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update rice in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateRice(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(RiceEntry.COLUMN_RICE_NAME)) {
            String name = values.getAsString(RiceEntry.COLUMN_RICE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Rice requires a name");
            }
        }

        // If the packaging key is present, check that's valid.
        if (values.containsKey(RiceEntry.COLUMN_PACKAGING)) {
            Integer pack = values.getAsInteger(RiceEntry.COLUMN_PACKAGING);
            if (pack == null || !RiceEntry.isValidPackaging(pack)) {
                throw new IllegalArgumentException("Rice requires valid pack");
            }
        }

        // If the price key is present, check that is valid.
        if (values.containsKey(RiceEntry.COLUMN_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer price = values.getAsInteger(RiceEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Rice requires valid price");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(RiceEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}