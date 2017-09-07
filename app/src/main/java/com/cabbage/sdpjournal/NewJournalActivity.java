package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Journal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class NewJournalActivity extends AppCompatActivity implements View.OnClickListener{

    private Button createBtn;
    private EditText etJournalName;
    private EditText etCompanyName;
    private String journalColor;
    Toolbar toolbar;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);
        init();

    }

    private void init(){

        myFirebaseAuth = FirebaseAuth.getInstance();
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etJournalName = (EditText) findViewById(R.id.etJournalName);
        etCompanyName = (EditText) findViewById(R.id.etCompanyName);
        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(this);

        //spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.journalCoverColor, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                journalColor = adapterView.getItemAtPosition(i).toString().trim();
                //If did not choose (select the default)
                if (journalColor.equals(Constants.Select_Color)){
                    //make it default
                    journalColor = Constants.Default_Color;
                }else {
                    Toast.makeText(NewJournalActivity.this, journalColor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                NewJournalActivity.this.startActivity(new Intent(NewJournalActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                NewJournalActivity.this.startActivity(new Intent(NewJournalActivity.this, ResetPasswordActivity.class));
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
        if (v == createBtn){
            createJournal();
            startActivity(new Intent(NewJournalActivity.this, JournalListActivity.class));
            finish();
        }
    }

    private void createJournal() {
        //setting values
        FirebaseUser firebaseUser = myFirebaseAuth.getCurrentUser();
        DatabaseReference journalReference = FirebaseDatabase.getInstance().getReference();

        String journalName = etJournalName.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();
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
