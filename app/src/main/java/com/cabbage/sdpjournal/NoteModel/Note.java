package com.cabbage.sdpjournal.NoteModel;

/**
 * Created by Junwen on 25/8/17.
 */

public class Note {
    private String noteID;
    private String noteTitle;
    private String noteContent;


    public Note() {
    }

    public Note(String noteID, String noteTitle, String noteContent) {
        this.noteID = noteID;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;

    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
