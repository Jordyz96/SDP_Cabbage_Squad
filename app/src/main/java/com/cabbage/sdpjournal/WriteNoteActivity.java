package com.cabbage.sdpjournal;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WriteNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private Button saveButton;
    private EditText etEntryName;
    private EditText etResponsibilities, etDecisions, etOutcome, etComment;
    private DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference noteReference;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        db = FirebaseDatabase.getInstance().getReference();

        saveButton = (Button) findViewById(R.id.saveButton);
        etEntryName = (EditText) findViewById(R.id.etEntryName);
        etResponsibilities = (EditText) findViewById(R.id.etResponsibilities);
        etDecisions = (EditText) findViewById(R.id.etDecision);
        etOutcome = (EditText) findViewById(R.id.etOutcome);
        etComment = (EditText) findViewById(R.id.etComment);

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            saveNoteToDatabase();
            backToEntryViewWithExtra();
            finish();
        }
    }

    private void backToEntryViewWithExtra(){
        //Must !!! put back the journalID to EntryView
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        Intent intent = new Intent(this, NoteBookListViewActivity.class);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
    }

    private void saveNoteToDatabase() {
        //setting up data.
        String entryName = etEntryName.getText().toString().trim();
        String entryResponsibilities = etResponsibilities.getText().toString().trim();
        String entryID = db.push().getKey();
        String entryDecision = etDecisions.getText().toString().trim();
        String entryOutcome = etOutcome.getText().toString().trim();
        String entryComment = etOutcome.getText().toString().trim();
        String status = "Normal";
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        String predecessorEntryID = "";
        String dataTimeCreated = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dataTimeCreated = simpleDateFormat.format(calendar.getTime());
        }

        //validation...
        if (TextUtils.isEmpty(entryName)) {
            etEntryName.setError("Entry name must not be empty");
        }
        if (TextUtils.isEmpty(entryResponsibilities)) {
            etResponsibilities.setError("Responsibilities must not be empty");
        }
        if (TextUtils.isEmpty(entryDecision)) {
            etDecisions.setError("Decision must not be empty");
        }
        if (TextUtils.isEmpty(entryOutcome)) {
            etOutcome.setError("Outcome must not be empty");
        }
        if (TextUtils.isEmpty(entryComment)) {
            etComment.setError("Comment must not be empty");
        }

        //if validates, store a new entry to database
        Entry entry = new Entry(entryID, entryName, entryResponsibilities, entryDecision, entryOutcome, entryComment
                ,dataTimeCreated, status, journalID, predecessorEntryID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        String userID = firebaseUser.getUid();

        noteReference = db.child(Constants.Users_End_Point).child(userID)
        .child("journals")
        .child(journalID).child("entries").child(entryID);
        noteReference.setValue(entry);
    }
}

