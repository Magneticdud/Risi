package com.dandandin.android.risi.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

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
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
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