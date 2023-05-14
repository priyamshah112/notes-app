package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class NoteDAO implements Serializable, Comparable<NoteDAO> {

    private String title = "", text = "";
    private Date lastUpdate;

    public NoteDAO(String title, String text){
        this.title = title;
        this.text = text;
        this.lastUpdate = new Date();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getlastUpdate() {
        return lastUpdate;
    }

    public void setlastUpdate(long lastUpdate) {
        this.lastUpdate = new Date(lastUpdate);
    }

    @Override
    public int compareTo(NoteDAO noteDAO) {
        if(lastUpdate.before(noteDAO.lastUpdate)){
            return 1;
        } else if(lastUpdate.after(noteDAO.lastUpdate)){
            return -1;
        }
        return 0;
    }
}
