package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.cabbage.sdpjournal.Adpter.HistoryListAdapter;
import com.cabbage.sdpjournal.Model.Attachment;
import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HistoryViewActivity extends AppCompatActivity {

    HistoryListAdapter listAdapter;
    ListView lv;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference entryRef;
    DatabaseReference attachmentRef;
    FirebaseAuth.AuthStateListener mAuthListener;
    String preID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseSetup();

        lv = (ListView) findViewById(R.id.lvHistoryList);

    }

    private void firebaseSetup(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        preID = getIntent().getExtras().getString("preID");
        String userID = "";
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        entryRef = databaseReference.child(Constants.Users_End_Point)
                .child(userID).child(Constants.Journals_End_Point).child(journalID)
                .child(Constants.Entries_End_Point);

        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, Constants.AUTH_IN + firebaseUser.getUid());
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
                firebaseAuth.signOut();
                HistoryViewActivity.this.startActivity(new Intent(HistoryViewActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                HistoryViewActivity.this.startActivity(new Intent(HistoryViewActivity.this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        firebaseAuth.addAuthStateListener(mAuthListener);

        final ArrayList<Entry> entriesList = new ArrayList<>();
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                entriesList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Entry entry = ds.getValue(Entry.class);
                    if (entry.getStatus().equals("replacedByModified") && entry.getPredecessorEntryID().equals(preID) || entry.getEntryID().equals(preID)){
                        entriesList.add(entry);
                    }
                    listAdapter = new HistoryListAdapter(HistoryViewActivity.this, entriesList);
                    lv.setAdapter(listAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
