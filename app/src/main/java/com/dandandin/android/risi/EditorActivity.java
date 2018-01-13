/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dandandin.android.risi;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dandandin.android.risi.data.RiceContract.RiceEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /** Identifier for the pet data loader */
    private static final int EXISTING_RICE_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentRiceUri;

    /** EditText field to enter the rice name */
    private EditText mNameEditText;

    /** EditText field to enter the rice breed */
    private EditText mBreedEditText;

    /** EditText field to enter the rice price, in cents */
    private EditText mPriceEditText;

    /** EditText field to enter the rice packaging */
    private Spinner mPackagingSpinner;

    /**
     * Packaging of the rice. The possible values are:
     * 0 for vacuum, 1 for carton, 2 for vacuum in carton, 3 for bag.
     */
    private int mPackaging = RiceEntry.PACK_VACUUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentRiceUri = intent.getData();
        // If the intent DOES NOT contain a pet content URI, then we know that we are creating a new rice.
        if (mCurrentRiceUri == null) {
            // This is a new rice, so change the app bar to say "Add a Rice"
            setTitle(getString(R.string.editor_activity_title_new_rice));
        } else {
            // Otherwise this is an existing rice, so change app bar to say "Edit Rice"
            setTitle(getString(R.string.editor_activity_title_edit_rice));
            // Initialize a loader to read the pet data from the database and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_RICE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_rice_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_rice_breed);
        mPriceEditText = (EditText) findViewById(R.id.edit_rice_weight);
        mPackagingSpinner = (Spinner) findViewById(R.id.spinner_packaging);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the packaging of the rice.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter packagingSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        packagingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPackagingSpinner.setAdapter(packagingSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mPackagingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.package_vacuum))) {
                        mPackaging = RiceEntry.PACK_VACUUM; // Vacuum
                    } else if (selection.equals(getString(R.string.package_carton))) {
                        mPackaging = RiceEntry.PACK_CARTON; // Carton
                    } else if (selection.equals(getString(R.string.package_vacuumcarton))) {
                        mPackaging = RiceEntry.PACK_VACUUMCARTON; // Vacuum and Carton
                    } else {
                        mPackaging = RiceEntry.PACK_BAG; // Bag
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPackaging = RiceEntry.PACK_VACUUM; // Vacuum
            }
        });
    }

    /**
     * Get user input from editor and save new rice info into database.
     */
    private void insertRice() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mPriceEditText.getText().toString().trim();
        int weight = Integer.parseInt(weightString);

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(RiceEntry.COLUMN_RICE_NAME, nameString);
        values.put(RiceEntry.COLUMN_BREED, breedString);
        values.put(RiceEntry.COLUMN_PACKAGING, mPackaging);
        values.put(RiceEntry.COLUMN_PRICE, weight);

        // Insert a new rice into the provider, returning the content URI for the new rice.
        Uri newUri = getContentResolver().insert(RiceEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the row ID is null, then there was an error with insertion.
            Toast.makeText(this, "Error with saving rice", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Rice saved", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //salva i dati
                insertRice();
                //esci dall'activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all rice attributes, define a projection that contains all columns from the rice table
        String[] projection = {
                RiceEntry._ID,
                RiceEntry.COLUMN_RICE_NAME,
                RiceEntry.COLUMN_BREED,
                RiceEntry.COLUMN_PACKAGING,
                RiceEntry.COLUMN_PRICE };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentRiceUri,         // Query the content URI for the current rice
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(RiceEntry.COLUMN_RICE_NAME);
            int breedColumnIndex = cursor.getColumnIndex(RiceEntry.COLUMN_BREED);
            int packColumnIndex = cursor.getColumnIndex(RiceEntry.COLUMN_PACKAGING);
            int priceColumnIndex = cursor.getColumnIndex(RiceEntry.COLUMN_PRICE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int pack = cursor.getInt(packColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mPriceEditText.setText(Integer.toString(price));

            // Packaging is a dropdown spinner, so map the constant value from the database into one of the dropdown options
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (pack) {
                case RiceEntry.PACK_VACUUM:
                    mPackagingSpinner.setSelection(0);
                    break;
                case RiceEntry.PACK_CARTON:
                    mPackagingSpinner.setSelection(1);
                    break;
                case RiceEntry.PACK_VACUUMCARTON:
                    mPackagingSpinner.setSelection(2);
                    break;
                case RiceEntry.PACK_BAG:
                    mPackagingSpinner.setSelection(3);
                    break;
                default:
                    mPackagingSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mPriceEditText.setText("");
        mPackagingSpinner.setSelection(0); // Select "Vacuum" packaging
    }
}