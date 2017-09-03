package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.cabbage.sdpjournal.Adpter.GridViewAdapter;
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

public class JournalListActivity extends AppCompatActivity implements View.OnClickListener {

    GridView gridView;
    ArrayList<Journal> journalArrayList;
    GridViewAdapter gridViewAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Toolbar toolbar;

    private FirebaseAuth myFireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //just prevent being required to login everytime...
        myFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseUser myFireBaseUser = myFireBaseAuth.getCurrentUser();
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
        gridView = (GridView) findViewById(R.id.gvJournalView);

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
                        gridViewAdapter = new GridViewAdapter(JournalListActivity.this, journalArrayList);
                        gridView.setAdapter(gridViewAdapter);
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
                myFireBaseAuth.signOut();
                JournalListActivity.this.startActivity(new Intent(JournalListActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                JournalListActivity.this.startActivity(new Intent(JournalListActivity.this, ResetPasswordActivity.class));
                return true;

            case R.id.action_add:
                JournalListActivity.this.startActivity(new Intent(JournalListActivity.this, NewJournalActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.

        myFireBaseAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            myFireBaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {
        //.
    }
}