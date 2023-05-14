package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AndroidNotesViewHolder extends RecyclerView.ViewHolder{
    TextView notesTitle, noteDate , mainText;

    public AndroidNotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notesTitle = itemView.findViewById(R.id.notesTitle);
        mainText = itemView.findViewById(R.id.mainText);
        noteDate = itemView.findViewById(R.id.noteDate);
    }
}
