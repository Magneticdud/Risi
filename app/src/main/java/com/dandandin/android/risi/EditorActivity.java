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

import com.dandandin.android.risi.data.RiceContract.RiceEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the rice name */
    private EditText mNameEditText;

    /** EditText field to enter the rice breed */
    private EditText mBreedEditText;

    /** EditText field to enter the rice weight */
    private EditText mWeightEditText;

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

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_rice_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_rice_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_rice_weight);
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
                // Do nothing for now
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
}