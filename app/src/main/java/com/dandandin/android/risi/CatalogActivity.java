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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dandandin.android.risi.data.RiceContract.RiceEntry;


/**
 * Displays list of risi that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the rice data
        ListView riceListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        riceListView.setEmptyView(emptyView);
    }

    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                RiceEntry._ID,
                RiceEntry.COLUMN_RICE_NAME,
                RiceEntry.COLUMN_BREED,
                RiceEntry.COLUMN_PACKAGING,
                RiceEntry.COLUMN_PRICE };

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link RicEntry#CONTENT_URI} to access the rice data.
        Cursor cursor = getContentResolver().query(
                RiceEntry.CONTENT_URI,  // The content URI of the words table
                projection,             // The columns to return for each row
                null,           // Selection criteria
                null,       // Selection criteria
                null);          // The sort order for the returned rows

        // Find the ListView which will be populated with the rice data
        ListView riceListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of rice data in the Cursor.
        RiceCursorAdapter adapter = new RiceCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        riceListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertDummyRices(){
        // Create a ContentValues object where column names are the keys,
        // and some rice attributes are the values.
        ContentValues values = new ContentValues();
        values.put(RiceEntry.COLUMN_RICE_NAME, "Conad Integrale");
        values.put(RiceEntry.COLUMN_BREED, "Parboiled Integrale");
        values.put(RiceEntry.COLUMN_PACKAGING, RiceEntry.PACK_VACUUMCARTON);
        values.put(RiceEntry.COLUMN_PRICE,240);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(RiceEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyRices();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
