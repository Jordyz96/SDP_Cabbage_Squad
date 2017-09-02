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
import android.widget.Button;
import android.widget.EditText;

import com.cabbage.sdpjournal.NoteModel.Constants;
import com.cabbage.sdpjournal.NoteModel.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class NewEntryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button saveButton;
    private EditText etNoteTitle;
    private EditText etNoteContent;

    private boolean validated;

    private DatabaseReference db;
    private DatabaseReference noteReference;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;

    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseDatabase.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();

        saveButton = (Button) findViewById(R.id.saveButton);
        etNoteTitle = (EditText) findViewById(R.id.etTitle);
        etNoteContent = (EditText) findViewById(R.id.etContent);

        saveButton.setOnClickListener(this);
        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, AUTH_OUT);
                }
                // ...
            }
        };
    }

    //On start method
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
    public void onClick(View v) {
        if (v == saveButton) {
            saveNoteToDatabase();
            startActivity(new Intent(this, EntryListActivity.class));
            finish();
            return;
        }
    }

    private void saveNoteToDatabase() {
        //save data.
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();
        String noteId = db.push().getKey();

        //validation...
        if (TextUtils.isEmpty(title)) {
            etNoteTitle.setError("Title must not be empty");
        }
        if (TextUtils.isEmpty(content)) {
            etNoteContent.setError("Content must not be empty");
        }

        //if validates
        Note note = new Note(noteId, title, content);

        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        String userID = myFirebaseUser.getUid();

        noteReference = db.child(Constants.Users_End_Point).child(userID).child(Constants.Notes_End_Point).child(noteId);
        noteReference.setValue(note);
    }
}
