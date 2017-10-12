package com.cabbage.sdpjournal.Model;

/**
 * Created by Junwen on 25/8/17.
 */

public class Entry {
    private String entryID, entryName, entryResponsibilities, entryDecision;
    private String entryOutcome, entryComment, dateTimeCreated, status, journalID, predecessorEntryID;

    private int countAttachment, countVersion;


    public Entry() {
    }

    public Entry(String entryID, String entryName, String entryResponsibilities, String entryDecision, String entryOutcome, String entryComment,
                 String dateTimeCreated, String status, String journalID, String predecessorEntryID, int countAttachment, int countVersion) {
        this.entryID = entryID;
        this.entryName = entryName;
        this.entryResponsibilities = entryResponsibilities;
        this.entryDecision = entryDecision;
        this.entryOutcome = entryOutcome;
        this.entryComment = entryComment;
        this.dateTimeCreated = dateTimeCreated;
        this.status = status;
        this.journalID = journalID;
        this.predecessorEntryID = predecessorEntryID;
        this.countAttachment = countAttachment;
        this.countVersion = countVersion;
    }

    public int getCountAttachment() {
        return countAttachment;
    }

    public void setCountAttachment(int countAttachment) {
        this.countAttachment = countAttachment;
    }

    public int getCountVersion() {
        return countVersion;
    }

    public void setCountVersion(int countVersion) {
        this.countVersion = countVersion;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryResponsibilities() {
        return entryResponsibilities;
    }

    public void setEntryResponsibilities(String entryResponsibilities) {
        this.entryResponsibilities = entryResponsibilities;
    }

    public String getEntryDecision() {
        return entryDecision;
    }

    public void setEntryDecision(String entryDecision) {
        this.entryDecision = entryDecision;
    }

    public String getEntryOutcome() {
        return entryOutcome;
    }

    public void setEntryOutcome(String entryOutcome) {
        this.entryOutcome = entryOutcome;
    }

    public String getEntryComment() {
        return entryComment;
    }

    public void setEntryComment(String entryComment) {
        this.entryComment = entryComment;
    }

    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(String dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJournalID() {
        return journalID;
    }

    public void setJournalID(String journalID) {
        this.journalID = journalID;
    }

    public String getPredecessorEntryID() {
        return predecessorEntryID;
    }

    public void setPredecessorEntryID(String predecessorEntryID) {
        this.predecessorEntryID = predecessorEntryID;
    }
}
