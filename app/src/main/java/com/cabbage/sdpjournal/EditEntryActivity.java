package com.cabbage.sdpjournal;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class EditEntryActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button confirmBtn;
    private EditText etEntryName;
    private EditText etResponsibilities, etDecisions, etOutcome, etComment;
    DatabaseReference db;
    String originalEntryID;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myFirebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        db = FirebaseDatabase.getInstance().getReference();
        init();
    }

    private void init(){
        //init
        confirmBtn = (Button) findViewById(R.id.confirmButton);
        etEntryName = (EditText) findViewById(R.id.etEntryName);
        etResponsibilities = (EditText) findViewById(R.id.etResponsibilities);
        etDecisions = (EditText) findViewById(R.id.etDecision);
        etOutcome = (EditText) findViewById(R.id.etOutcome);
        etComment = (EditText) findViewById(R.id.etComment);
        //set text...
        etEntryName.setText(getIntent().getExtras().getString("entryName"));
        etResponsibilities.setText(getIntent().getExtras().getString("responsibilities"));
        etDecisions.setText(getIntent().getExtras().getString("decision"));
        etOutcome.setText(getIntent().getExtras().getString("outcome"));
        etComment.setText(getIntent().getExtras().getString("entryComment"));
        //set clicking listener
        confirmBtn.setOnClickListener(this);
    }

    /**
     * Creates the options menu on the action bar.
     *
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
     *
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch (item.getItemId()) {
            //If item is logout
            case R.id.action_logout:
                //Sign out of the authenticator and return to login activity.
                myFirebaseAuth.signOut();
                EditEntryActivity.this.startActivity(new Intent(EditEntryActivity.this, LoginActivity.class));
                return true;
            case R.id.action_reset_password:
                EditEntryActivity.this.startActivity(new Intent(EditEntryActivity.this, ResetPasswordActivity.class));
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
    public void onClick(View view) {
        if (view == confirmBtn){
            editEntry();
        }
    }

    private void editEntry() {
        createEditedEntry();
    }

    private void backToEntryListWithExtra() {
        //Must !!! put the journalID back to EntryList
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        Intent intent = new Intent(this, EntryListActivity.class);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
    }

    //change original entry's status so that it will no longer be displayed in entry list,
    //instead, it is now only displayed in the history.
    //And, create a new entry (modified)
    private void createEditedEntry() {
        originalEntryID = getIntent().getExtras().getString("entryID");
        String preID = getIntent().getExtras().getString("preID");
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        String userID = firebaseUser.getUid();
        //set up all attributes
        String entryName = etEntryName.getText().toString().trim();
        String entryResponsibilities = etResponsibilities.getText().toString().trim();
        String newEntryID = db.push().getKey();
        String entryDecision = etDecisions.getText().toString().trim();
        String entryOutcome = etOutcome.getText().toString().trim();
        String entryComment = etComment.getText().toString().trim();
        String status = Constants.Entry_Status_Normal;
        String predecessorEntryID = originalEntryID;
        if (!preID.equals("")){
            predecessorEntryID = preID;
        }
        String dataTimeCreated = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dataTimeCreated = simpleDateFormat.format(calendar.getTime());
        }

        //..

        if (validationPassed(entryName, entryResponsibilities, entryDecision, entryOutcome)) {

            //new path
            DatabaseReference noteReference = db.child(Constants.Users_End_Point).child(userID)
                    .child(Constants.Journals_End_Point)
                    .child(journalID).child(Constants.Entries_End_Point).child(newEntryID);
            //original path
            DatabaseReference originalNoteReference = db.child(Constants.Users_End_Point).child(userID)
                    .child(Constants.Journals_End_Point)
                    .child(journalID).child(Constants.Entries_End_Point).child(originalEntryID);

            Entry entry = new Entry(newEntryID, entryName
                    , entryResponsibilities, entryDecision, entryOutcome, entryComment
                    , dataTimeCreated, status, journalID, predecessorEntryID);

            String name = getIntent().getExtras().getString("entryName");
            String responsibilities = getIntent().getExtras().getString("responsibilities");
            String decision = getIntent().getExtras().getString("decision");
            String outcome = getIntent().getExtras().getString("outcome");
            String comment = getIntent().getExtras().getString("entryComment");
            originalEntryID = getIntent().getExtras().getString("entryID");
            String dateTime = getIntent().getExtras().getString("dateTime");
            String oldStatus = "replacedByModified";
            predecessorEntryID = "original";

            if (!(preID == null)) {
                Entry originalEntry = new Entry(originalEntryID, name
                        , responsibilities, decision, outcome, comment
                        , dateTime, oldStatus, journalID, preID);

                originalNoteReference.setValue(originalEntry);
            }else{
                Entry originalEntry = new Entry(originalEntryID, name
                        , responsibilities, decision, outcome, comment
                        , dateTime, oldStatus, journalID, predecessorEntryID);

                originalNoteReference.setValue(originalEntry);
            }

            if (TextUtils.isEmpty(entryComment)) {
                entry.setEntryComment("You did not leave any comment on it");
            }
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
