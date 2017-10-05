package com.cabbage.sdpjournal.Model;

/**
 * Created by Junwen on 31/8/17.
 */

public class Journal {
    private String journalID;
    private String userID;
    private String journalName;
    private String companyName;
    private String journalColor;

    public Journal() {
    }

    public Journal(String journalID, String userID, String journalName, String companyName, String journalColor) {
        this.journalID = journalID;
        this.userID = userID;
        this.journalName = journalName;
        this.companyName = companyName;
        this.journalColor = journalColor;
    }

    public String getJournalID() {
        return journalID;
    }

    public void setJournalID(String journalID) {
        this.journalID = journalID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJournalColor() {
        return journalColor;
    }

    public void setJournalColor(String journalColor) {
        this.journalColor = journalColor;
    }
}
