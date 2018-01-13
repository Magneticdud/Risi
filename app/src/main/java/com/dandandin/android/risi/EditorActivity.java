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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
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

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mRiceHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true. */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mRiceHasChanged = true;
            return false;
        }
    };

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

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mPackagingSpinner.setOnTouchListener(mTouchListener);

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
     * Get user input from editor and save rice info into database.
     */
    private void saveRice() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        // Check if this is supposed to be a new rice and check if all the fields in the editor are blank
        if (mCurrentRiceUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                TextUtils.isEmpty(priceString) && mPackaging == RiceEntry.PACK_VACUUM) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(RiceEntry.COLUMN_RICE_NAME, nameString);
        values.put(RiceEntry.COLUMN_BREED, breedString);
        values.put(RiceEntry.COLUMN_PACKAGING, mPackaging);
        values.put(RiceEntry.COLUMN_PRICE, priceString);

        // If the price is not provided by the user, don't try to parse the string into an integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(RiceEntry.COLUMN_PRICE,price);

        //vediamo se è un nuovo riso o una modifica di uno esistente
        if (mCurrentRiceUri==null){
            // è nuovo! Insert a new rice into the provider, returning the content URI for the new rice.
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
        else {
            // Otherwise this is an EXISTING rice, so update it with content URI: mCurrentRiceUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentRiceUri will already identify the correct row in the database that we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentRiceUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error updating rice",Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Edited",Toast.LENGTH_SHORT).show();
            }
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
                saveRice();
                //esci dall'activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the rice hasn't changed, continue with navigating up to parent activity
                if (!mRiceHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //This method is called when the back button is pressed.
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mRiceHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have unsaved changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}