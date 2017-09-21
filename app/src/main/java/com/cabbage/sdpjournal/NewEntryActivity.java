package com.cabbage.sdpjournal;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class NewEntryActivity extends AppCompatActivity implements View.OnClickListener{

    private Button saveButton;
    private EditText etEntryName;
    private EditText etResponsibilities, etDecisions, etOutcome, etComment;
    private DatabaseReference db;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myFirebaseAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        db = FirebaseDatabase.getInstance().getReference();

        saveButton = (Button) findViewById(R.id.saveButton);
        etEntryName = (EditText) findViewById(R.id.etEntryName);
        etResponsibilities = (EditText) findViewById(R.id.etResponsibilities);
        etDecisions = (EditText) findViewById(R.id.etDecision);
        etOutcome = (EditText) findViewById(R.id.etOutcome);
        etComment = (EditText) findViewById(R.id.etComment);

        saveButton.setOnClickListener(this);

        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, Constants.AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, Constants.AUTH_OUT);
                }
                // ...
            }
        };
    }

    /**
     * Creates the options menu on the action bar.
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets a listener that triggers when an option from the taskbar menu is selected.
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch(item.getItemId()){
            //If item is logout
            case R.id.action_logout:
                //Sign out of the authenticator and return to login activity.
                myFirebaseAuth.signOut();
                NewEntryActivity.this.startActivity(new Intent(NewEntryActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                NewEntryActivity.this.startActivity(new Intent(NewEntryActivity.this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        myFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            myFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            saveEntryAndGoBackToEntryList();
        }
    }

    private void backToEntryListWithExtra() {
        //Must !!! put back the journalID to EntryList
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        Intent intent = new Intent(this, EntryListActivity.class);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
    }

    private void saveEntryAndGoBackToEntryList() {
        //setting up data.
        String entryName = etEntryName.getText().toString().trim();
        String entryResponsibilities = etResponsibilities.getText().toString().trim();
        String entryID = db.push().getKey();
        String entryDecision = etDecisions.getText().toString().trim();
        String entryOutcome = etOutcome.getText().toString().trim();
        String entryComment = etComment.getText().toString().trim();
        String status = Constants.Entry_Status_Normal;
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        String predecessorEntryID = "";
        String dataTimeCreated = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dataTimeCreated = simpleDateFormat.format(calendar.getTime());
        }

        //if validates, store a new entry to database
        if (validationPassed(entryName, entryResponsibilities, entryDecision, entryOutcome)) {
            Entry entry = new Entry(entryID, entryName
                    , entryResponsibilities, entryDecision, entryOutcome, entryComment
                    , dataTimeCreated, status, journalID, predecessorEntryID);

            if (TextUtils.isEmpty(entryComment)) {
                entry.setEntryComment("You did not leave any comment on it");
            }

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            db = FirebaseDatabase.getInstance().getReference();
            String userID = firebaseUser.getUid();

            DatabaseReference noteReference = db.child(Constants.Users_End_Point).child(userID)
                    .child(Constants.Journals_End_Point)
                    .child(journalID).child(Constants.Entries_End_Point).child(entryID);
            noteReference.setValue(entry);

            //entry has been successfully added to the database, now go back to the entry list
            backToEntryListWithExtra();
            finish();
        }
    }

    private boolean validationPassed(String name, String res, String decision, String outCome){
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            etEntryName.setError("Entry name must not be empty");
            isValid=false;
        }
        if (TextUtils.isEmpty(res)){
            etResponsibilities.setError("Responsibilities must not be empty");
            isValid=false;
        }
        if (TextUtils.isEmpty(decision)){
            etDecisions.setError("Decision must not be empty");
            isValid=false;
        }
        if (TextUtils.isEmpty(outCome)){
            etOutcome.setError("Outcome must not be empty");
            isValid=false;
        }

        return isValid;
    }
}
