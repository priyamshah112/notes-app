package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";
    private EditText titleEditText;
    private EditText EditTextnote;
    private String titleOld;
    private String textOld;
    private long DateTime_old;
    private NoteDAO Note_old;
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEditText = findViewById(R.id.title);
        EditTextnote = findViewById(R.id.NotesText);
        // Getting intent
        Intent intent = getIntent();
        if (intent.hasExtra("noteEdit")) {
            Note_old = (NoteDAO) intent.getSerializableExtra("noteEdit");
            if (Note_old != null) {
                titleOld = Note_old.getTitle();
                textOld = Note_old.getText();
                DateTime_old = intent.getLongExtra("Time", 0);
                titleEditText.setText(titleOld);
                EditTextnote.setText(textOld);
            }
            isNew = false;
        } else {
            isNew = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_save) {
            addSaveActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSaveActivity() {

        String mainTitle = titleEditText.getText().toString();
        String mainText = EditTextnote.getText().toString();
        // Edit Old Note
        if (isNew == false) {
            if (mainTitle.trim().isEmpty()) {
                showTitleDialogBox();
                return;
            }
            Note_old.setTitle(mainTitle);
            Note_old.setText(mainText);
            Note_old.setlastUpdate(DateTime_old);
            Intent intent = new Intent();
            intent.putExtra("noteEdit", Note_old);
            setResult(2, intent);
            finish();
        } else { // Add New Note
            if (mainTitle.trim().isEmpty()) {
                showTitleDialogBox();
                return;
            }
            // Creating new Note
            NoteDAO noteNew = new NoteDAO(mainTitle, mainText);
            Intent intent = new Intent();
            intent.putExtra("noteNew", noteNew);
            setResult(1, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        String mainTitle = titleEditText.getText().toString();
        String mainText = EditTextnote.getText().toString();

        if (isNew && (!mainTitle.trim().isEmpty())) {
            showSaveDialogBox();
        } else if (isNew && (!mainText.trim().isEmpty())) {
            showSaveDialogBox();
        } else if (!(Note_old == null) && !(mainTitle.equals(Note_old.getTitle()))) {
            showSaveDialogBox();
        } else if (!(Note_old == null) && !(mainText.equals(Note_old.getText()))) {
            showSaveDialogBox();
        } else {
            AddNoteActivity.super.onBackPressed();
            return;
        }
    }

    public void showTitleDialogBox() {
        Toast.makeText(this, "Note cannot be saved without title", Toast.LENGTH_LONG).show();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert) //set icon
                .setTitle(R.string.no_title)//set title
                .setMessage(R.string.no_title_message)//set message
                .setPositiveButton(R.string.dialog_box_yes, new DialogInterface.OnClickListener() { // Positive Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddNoteActivity.super.onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_box_no, new DialogInterface.OnClickListener() { // Negative Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .show();
    }

    public void showSaveDialogBox() {
        String mainTitle = titleEditText.getText().toString();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)//set icon
                .setTitle(R.string.save_note_dialogbox_title)//set title
                .setMessage(getString(R.string.save_note_dialogbox_message) + mainTitle + "' ?")//set message
                .setPositiveButton(R.string.dialog_box_yes, new DialogInterface.OnClickListener() {// Positive Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addSaveActivity();
                        finish();
                    }
                })
                .setNegativeButton(R.string.dialog_box_no, new DialogInterface.OnClickListener() {// Negative Button Action
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddNoteActivity.super.onBackPressed();
                        finish();
                    }
                })
                .show();
    }
}


