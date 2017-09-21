package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Adpter.JournalListAdapter;
import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Journal;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class JournalListActivity extends AppCompatActivity implements View.OnClickListener{

    ListView listView;
    ArrayList<Journal> journalArrayList;
    JournalListAdapter listAdapter;
    FloatingActionButton fab;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth myFirebaseAuth;
    private String journalColor;
    private EditText etTitle;
    private EditText etCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        
        init();
    }

    //initialize stuff...
    private void init() {
        //just prevent being required to login everytime...
        myFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser myFireBaseUser = myFirebaseAuth.getCurrentUser();
        String userID = "";
        if (myFireBaseUser == null) {
            startActivity(new Intent(JournalListActivity.this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
            userID = myFireBaseUser.getUid();
        }

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

        //setting up things
        listView = (ListView) findViewById(R.id.gvJournalViewTest);

        //setting adapter
        journalArrayList = new ArrayList<>();
        DatabaseReference journalRef = FirebaseDatabase.getInstance().getReference();
        journalRef.child(Constants.Users_End_Point)
                .child(userID)
                .child(Constants.Journals_End_Point)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        journalArrayList.clear();
                        for (DataSnapshot journalDS : dataSnapshot.getChildren()) {
                            //Log checking
                            Log.d("Journal ", " ==>" + journalDS.toString());
                            //getting database data
                            Journal journal = journalDS.getValue(Journal.class);
                            journalArrayList.add(journal);
                        }
                        //add data to the view adapter
                        listAdapter = new JournalListAdapter(JournalListActivity.this, journalArrayList);
                        listView.setAdapter(listAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                JournalListActivity.this.startActivity(new Intent(JournalListActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                JournalListActivity.this.startActivity(new Intent(JournalListActivity.this, ResetPasswordActivity.class));
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

    //On stop method
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
        if (v == fab){
            //if click the floating add btn, a dialog pops up for user to create a new journal.

            //set up stuff
            AlertDialog.Builder ab = new AlertDialog.Builder(JournalListActivity.this);
            View myView = getLayoutInflater().inflate(R.layout.dialog_create_new_journal, null);

            TextView tvNewjournalLabel = (TextView) myView.findViewById(R.id.tvNewJournalLabel);
            etTitle = (EditText) myView.findViewById(R.id.etTitle);
            etCompany = (EditText) myView.findViewById(R.id.etCompanyName);
            Button cancelBtn = (Button) myView.findViewById(R.id.cancelBtn);
            Button okBtn = (Button) myView.findViewById(R.id.okBtn);

            //spinner
            Spinner colorDropDown = (Spinner) myView.findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.journalCoverColor, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            colorDropDown.setAdapter(adapter);
            //set on select listener
            colorDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //set the journalColor to the one selected
                    journalColor = adapterView.getItemAtPosition(i).toString().trim();
                    //If did not choose (select the default)
                    if (journalColor.equals(Constants.Select_Color)){
                        //make it default
                        journalColor = Constants.Default_Color;
                    }else {
                        Toast.makeText(JournalListActivity.this, journalColor, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            tvNewjournalLabel.setText(Constants.New_Journal);
            //show dialog
            ab.setView(myView);
            final AlertDialog dialog = ab.create();
            dialog.show();

            //if click on cancel btn
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

            //if click on OK btn
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createJournal();
                    dialog.cancel();
                }
            });

        }
    }

    private void createJournal() {
        //setting values
        FirebaseUser firebaseUser = myFirebaseAuth.getCurrentUser();
        DatabaseReference journalReference = FirebaseDatabase.getInstance().getReference();

        String journalName = etTitle.getText().toString().trim();
        String companyName = etCompany.getText().toString().trim();
        String userID = null;
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        String journalID = journalReference.push().getKey();

        //validation
        if (TextUtils.isEmpty(journalName)){
            etTitle.setError("Journal name must not be empty");
        } else if (TextUtils.isEmpty(companyName)){
            etCompany.setError("Company name must not be empty");
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
