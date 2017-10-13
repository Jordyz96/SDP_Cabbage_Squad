package com.cabbage.sdpjournal;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class EntryViewActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String entryName, responsibilities, decision, outcome, comment,
            preID, dateTime, journalID, entryID;

    int count, countVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        init();
        requestPermissions();
    }

    private void init() {
        //setting var


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
        entryID = getIntent().getExtras().getString("entryID");
        dateTime = getIntent().getExtras().getString("dateTime");
        preID = getIntent().getExtras().getString("preID");
        journalID = getIntent().getExtras().getString(Constants.journalID);
        count = getIntent().getExtras().getInt("count");
        countVersion = getIntent().getExtras().getInt("countVersion");
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

        setTitle(getTitle() + " - " + entryName);
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
                transitionToEditEntry();
                return true;
            case R.id.action_history:
                transitionToHistoryView();
                return true;
            case R.id.action_attachment:
                transitionToAttachmentView();
                return true;
            case android.R.id.home:
                returnToList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void transitionToEditEntry() {
        //put extra
        Intent intent = new Intent(this, EditEntryActivity.class);
        intent.putExtra("entryName", entryName);
        intent.putExtra("responsibilities", responsibilities);
        intent.putExtra("decision", decision);
        intent.putExtra("outcome", outcome);
        intent.putExtra("entryComment", comment);
        intent.putExtra("entryID", entryID);
        intent.putExtra("dateTime", dateTime);
        intent.putExtra("preID", preID);
        intent.putExtra("count", count);
        intent.putExtra("countVersion", countVersion);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
        finish();
    }

    private void transitionToHistoryView() {
        Intent intent = new Intent(this, HistoryViewActivity.class);
        intent.putExtra("preID", preID);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
        finish();
    }

    private void transitionToAttachmentView() {
        Intent intent = new Intent(this, AttachmentViewActivity.class);
        intent.putExtra("entryID", entryID);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
        finish();
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

    @Override
    public void onClick(View view) {

    }

    public void returnToList() {
        Intent i = new Intent();
        i.putExtra("filter", getIntent().getStringExtra("filter"));
        i.putExtra("search", getIntent().getStringExtra("search"));
        setResult(RESULT_OK, i);
    }

    public void requestPermissions() {
        boolean requestAudio = false;
        boolean requestWriteExternalStorage = false;
        if (ContextCompat.checkSelfPermission(EntryViewActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestAudio = true;
        }

        if (ContextCompat.checkSelfPermission(EntryViewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestWriteExternalStorage = true;
        }

        if (requestAudio && requestWriteExternalStorage) {
            ActivityCompat.requestPermissions(EntryViewActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else if (requestAudio) {
            ActivityCompat.requestPermissions(EntryViewActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        } else if (requestWriteExternalStorage) {
            ActivityCompat.requestPermissions(EntryViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length == 0) {
                    // disable the attachment function
                } else {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            // permission was granted, yay!
                        } else {
                            // disable the attachment function
                        }
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
        returnToList();
        finish();
    }

}
