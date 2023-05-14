package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Priyam Shah
 * @version 1.0
 * @since 2023-02-07
 *
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final List<NoteDAO> listOfNotes = new ArrayList<NoteDAO>();
    private RecyclerView recyclerView;
    private NoteDAO noteDao;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private adapterNote adapterNote;
    private int index;
    int noteNew = 1;
    int noteEdit = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reading data from the JSON File
        readDataFromJSON();
        //Setting up the Application Name :- Doing because we need to update the count with no of Notes
        String app_name = getResources().getString(R.string.app_name);
        // Title Name with the Notes List Size
        setTitle(app_name + " (" + listOfNotes.size() + ")");
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        adapterNote = new adapterNote(this, listOfNotes);
        recyclerView.setAdapter(adapterNote);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Functionality for getting result from another activity
        // Calling handleMainResult() for handling response
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleMainResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflating Menu on the current Activity :- About and Add Note menu
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            // opening About Activity when about icon is clicked
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.addnoteNew) {
            // opening Add Note Activity when + is clicked
            Intent intent = new Intent(this, AddNoteActivity.class);
            activityResultLauncher.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        // getting item and returning index of the item
        index = recyclerView.getChildAdapterPosition(view);
        noteDao = listOfNotes.get(index);
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("noteEdit", noteDao);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        index = recyclerView.getChildAdapterPosition(view);
        noteDao = listOfNotes.get(index);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert) //set icon
                .setMessage("Delete Note '" + noteDao.getTitle() + "' ")//set message
                .setPositiveButton(R.string.dialog_box_yes, new DialogInterface.OnClickListener() { // Positive Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listOfNotes.remove(index);

                        Collections.sort(listOfNotes);
                        // Notifying Adapter data has changed
                        adapterNote.notifyDataSetChanged();
                        // Changing application name and Title with updated count of list
                        String app_name = getResources().getString(R.string.app_name);
                        setTitle(app_name + " (" + listOfNotes.size() + ")");
                        return;
                    }
                })
                .setNegativeButton(R.string.dialog_box_no, new DialogInterface.OnClickListener() { // Negative Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .show();
        return true;
    }

    public void handleMainResult(ActivityResult activityResult) {
        // Edit Note logic
        if (activityResult.getResultCode() == noteEdit) { // 2
            Intent resultData = activityResult.getData();
            if (resultData == null) {

            } else {
                noteDao = (NoteDAO) resultData.getSerializableExtra("noteEdit");
                if(noteDao == null){
                    Toast.makeText(this, "Error in Parsing noteEdit Data",Toast.LENGTH_SHORT ).show();
                } else{
                    // getting index and setting updated data
                    listOfNotes.get(index).setTitle(noteDao.getTitle());
                    listOfNotes.get(index).setText(noteDao.getText());
                    // Updating last update date and time
                    listOfNotes.get(index).setlastUpdate(System.currentTimeMillis());
                    // Sorting list of Note to show the latest updated note at top
                    Collections.sort(listOfNotes);
                    // Notifying Adapter data has changed
                    adapterNote.notifyDataSetChanged();
                }
            }
        }
        // New Notes logic
        if (activityResult.getResultCode() == noteNew) { // 1
            Intent resultData = activityResult.getData();
            if (resultData != null) {
                // Extracting data
                noteDao = (NoteDAO) resultData.getSerializableExtra("noteNew");
                if (noteDao != null) {
                    // Adding newly created note in the list
                    listOfNotes.add(noteDao);
                    // Sorting list of Note to show the latest updated note at top
                    Collections.sort(listOfNotes);
                    // Notifying Adapter data has changed
                    adapterNote.notifyDataSetChanged();
                    // Changing application name and Title with updated count of list
                    String app_name = getResources().getString(R.string.app_name);
                    setTitle(app_name + " (" + listOfNotes.size() + ")");
                }
            }
        }
    }

    // Reading data from JSON File
    private void readDataFromJSON() {
        Log.d(TAG, "Loading data from JSON File");
        try {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.notesJson));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String mainTitle = jsonObject.getString("mainTitle");
                String mainText = jsonObject.getString("mainText");
                long lastDateUpdate = jsonObject.getLong("lastNoteUpdateDate");
                NoteDAO note = new NoteDAO(mainTitle, mainText);
                note.setlastUpdate(lastDateUpdate);
                listOfNotes.add(note);
            }
        } catch (Exception e) {
            Log.d(TAG, "READ DATA FROM JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeDataToJSONFile() {
        Log.d(TAG, "Writing Data into the JSON File");
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getResources().getString(R.string.notesJson), Context.MODE_PRIVATE);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();
            // iterating to every note and getting notes data and creating JSON
            for (NoteDAO note : listOfNotes) {
                jsonWriter.beginObject();
                jsonWriter.name("mainTitle").value(note.getTitle());
                jsonWriter.name("mainText").value(note.getText());
                jsonWriter.name("lastNoteUpdateDate").value(note.getlastUpdate().getTime());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        writeDataToJSONFile();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}