package com.cabbage.sdpjournal.Model;

import android.net.Uri;

/**
 * Created by jamen on 30/9/17.
 */

public class Attachment {
    private String path, format, attachmentID, entryID, fileName;
    private long duration;

    public Attachment() {

    }

    public Attachment(String path, String format, String attachmentID, String entryID, long duration, String fileName) {
        this.path = path;
        this.format = format;
        this.attachmentID = attachmentID;
        this.entryID = entryID;
        this.duration = duration;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
    }
}
