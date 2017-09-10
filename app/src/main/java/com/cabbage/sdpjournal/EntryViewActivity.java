package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class EntryViewActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        //setting var
        String entryName, responsibilities, decision, outcome, comment, dateTime;

        //setting items
        TextView tvEntryName = (TextView) findViewById(R.id.tvEntryName);
        TextView tvRes = (TextView) findViewById(R.id.tvResponsibilities);
        TextView tvDecisions = (TextView) findViewById(R.id.tvDecisions);
        TextView tvOutcome = (TextView) findViewById(R.id.tvOutcome);
        TextView tvComment = (TextView) findViewById(R.id.tvComment);
        TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);

        //getting Extras from EntryListActivity
        entryName = getIntent().getExtras().getString("entryName");
        responsibilities = getIntent().getExtras().getString("responsibilities");
        decision = getIntent().getExtras().getString("decision");
        outcome = getIntent().getExtras().getString("outcome");
        comment = getIntent().getExtras().getString("entryComment");
        dateTime = getIntent().getExtras().getString("dateTime");

        //put extras into items
        tvEntryName.setText(entryName);
        tvRes.setText(responsibilities);
        tvDecisions.setText(decision);
        tvOutcome.setText(outcome);
        tvComment.setText(comment);
        tvDateTime.setText(dateTime);

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
     *
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.menu_entry_view_edit, menu);
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
                firebaseAuth.signOut();
                EntryViewActivity.this.startActivity(new Intent(EntryViewActivity.this, LoginActivity.class));
                return true;
            //If item is reset password
            case R.id.action_reset_password:
                EntryViewActivity.this.startActivity(new Intent(EntryViewActivity.this, ResetPasswordActivity.class));
                return true;
            case R.id.action_edit:
                Toast.makeText(this, "u are gonna edit this entry", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        firebaseAuth.addAuthStateListener(mAuthListener);
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
