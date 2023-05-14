package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class adapterNote extends RecyclerView.Adapter<AndroidNotesViewHolder>{

    private final MainActivity mainActivity;
    private final List<NoteDAO> noteList;

    public adapterNote(MainActivity mainActivity, List<NoteDAO> noteList) {
        this.mainActivity = mainActivity;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public AndroidNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View items = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_layout, parent, false);
        items.setOnClickListener(mainActivity);
        items.setOnLongClickListener(mainActivity);
        return new AndroidNotesViewHolder(items);

    }

    @Override
    public void onBindViewHolder(@NonNull AndroidNotesViewHolder holder, int position) {
        NoteDAO noteDAO = noteList.get(position);
        String title = noteDAO.getTitle();
        // Note Title limit functionality
        if(title.length() > 80){
            title = title.substring(0, 80);
            title = title+ "...";
        }
        holder.notesTitle.setText(title);
        // Note Text limit functionality
        String mainText = noteDAO.getText();
        if(mainText.length() > 80){
            mainText = mainText.substring(0, 80);
            mainText = mainText+ "...";
        }
        holder.mainText.setText(mainText);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MM d, hh:mm a");
        String formatDate = formatter.format(noteDAO.getlastUpdate());
        holder.noteDate.setText(formatDate);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
