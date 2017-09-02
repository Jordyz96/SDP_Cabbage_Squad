package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Journal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateJournalActivity extends AppCompatActivity implements View.OnClickListener{

    private Button createBtn;
    private EditText etJournalName;
    private EditText etCompanyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);

        etJournalName = (EditText) findViewById(R.id.etJournalName);
        etCompanyName = (EditText) findViewById(R.id.etCompanyName);
        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == createBtn){
            createJournal();
            startActivity(new Intent(CreateJournalActivity.this, MainActivity.class));
            finish();
        }
    }

    private void createJournal() {
        //setting values
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference journalReference = FirebaseDatabase.getInstance().getReference();

        String journalName = etJournalName.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();
        String journalColor = "blankForNow";
        String userID = null;
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        String journalID = journalReference.push().getKey();

        //validation
        if (TextUtils.isEmpty(journalName)){
            etJournalName.setError("Journal name must not be empty");
        } else if (TextUtils.isEmpty(companyName)){
            etCompanyName.setError("Company name must not be empty");
        } else {
            //storing data to the database...
            Journal journal = new Journal(journalID, userID, journalName, companyName, journalColor);
            journalReference.child(Constants.Users_End_Point)
                    .child(userID)
                    .child(Constants.Journals_End_Point)
                    .child(journalID).setValue(journal);
        }
    }
}
