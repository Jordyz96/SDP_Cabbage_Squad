package com.cabbage.sdpjournal;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Model.Attachment;
import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class EditEntryActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button confirmBtn;
    private EditText etEntryName;
    private EditText etResponsibilities, etDecisions, etOutcome, etComment;
    DatabaseReference db;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    StorageReference storageReference;
    StorageReference filePath;
    Uri imageUri, audioUri, attURI;
    String lastPath, attFileName, audioFileName, newEntryID, originalEntryID;
    ArrayList<String> lastPathArray, fileNameList;
    ArrayList<Uri> uriList;
    ArrayList<Long> audioDurationList;
    ArrayList<Uri> audioUriList;

    int newCount;
    MediaRecorder mediaRecorder;
    long duration;
    long lastDown;
    long audioDuration;
    int k;
    int fileNameCount;

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

        storageReference = FirebaseStorage.getInstance().getReference();

        uriList = new ArrayList<>();
        audioUriList = new ArrayList<>();
        lastPathArray = new ArrayList<>();
        audioDurationList = new ArrayList<>();
        fileNameList = new ArrayList<>();

        audioDurationList.clear();
        audioUriList.clear();
        fileNameList.clear();
        k = 0;
        newCount = 0;
        fileNameCount = 0;

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
        String entryName = getIntent().getExtras().getString("entryName");
        etEntryName.setText(entryName);
        etResponsibilities.setText(getIntent().getExtras().getString("responsibilities"));
        etDecisions.setText(getIntent().getExtras().getString("decision"));
        etOutcome.setText(getIntent().getExtras().getString("outcome"));
        etComment.setText(getIntent().getExtras().getString("entryComment"));
        //set clicking listener
        confirmBtn.setOnClickListener(this);
        setTitle(getTitle() + " - " + entryName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.Gallery_Request && resultCode == RESULT_OK){
            imageUri = data.getData();
            lastPath = imageUri.getLastPathSegment();
            uriList.add(imageUri);
            lastPathArray.add(lastPath);
            Toast.makeText(this, "Photo Added", Toast.LENGTH_SHORT).show();
            newCount++;
        }
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
        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
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
            case R.id.action_image:
                chooseImage();
                return true;
            case R.id.action_audio:
                recordAudioDialog();
                return true;
            case R.id.action_video:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chooseImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Constants.Gallery_Request);
    }

    private void recordAudioDialog(){
        AlertDialog.Builder ab = new AlertDialog.Builder(EditEntryActivity.this);
        View myView = getLayoutInflater().inflate(R.layout.dialog_record_audio, null);

        final TextView label = (TextView) myView.findViewById(R.id.tvRecordAudioLabel);
        Button recordBtn = (Button) myView.findViewById(R.id.recordAudioBtn);
        Button backBtn = (Button) myView.findViewById(R.id.backBtn);

        label.setText("Record Audio");
        ab.setView(myView);
        final AlertDialog dialog = ab.create();
        dialog.show();

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    label.setText("Recording...");
                    lastDown = System.currentTimeMillis();
                    audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                    audioFileName += "/audio" + fileNameCount + ".3gp";
                    startRecording();
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    label.setText("Recorded");
                    duration = System.currentTimeMillis() - lastDown;
                    stopRecording();
                    if (duration >= 1000){
                        Uri uri = Uri.fromFile(new File(audioFileName));
                        fileNameCount++;
                        audioUriList.add(uri);
                        attFileName = audioFileName;
                        fileNameList.add(attFileName);
                        audioDurationList.add(duration);
                        Toast.makeText(EditEntryActivity.this, "Audio recorded", Toast.LENGTH_SHORT).show();
                        newCount++;
                    }else {
                        Toast.makeText(EditEntryActivity.this, "Message too short", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void startRecording(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        }catch (IOException e){
            Log.e(TAG, "prepare() failed");
        }

        mediaRecorder.start();
    }

    private void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
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
        final String journalID = getIntent().getExtras().getString(Constants.journalID);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        final String userID = firebaseUser.getUid();
        //set up all attributes
        String entryName = etEntryName.getText().toString().trim();
        String entryResponsibilities = etResponsibilities.getText().toString().trim();
        newEntryID = db.push().getKey();
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
                    , dataTimeCreated, status, journalID, predecessorEntryID, newCount);

            final String name = getIntent().getExtras().getString("entryName");
            String responsibilities = getIntent().getExtras().getString("responsibilities");
            String decision = getIntent().getExtras().getString("decision");
            String outcome = getIntent().getExtras().getString("outcome");
            String comment = getIntent().getExtras().getString("entryComment");
            int oldCount = getIntent().getExtras().getInt("count");
            originalEntryID = getIntent().getExtras().getString("entryID");
            String dateTime = getIntent().getExtras().getString("dateTime");
            int count = getIntent().getExtras().getInt("count");
            String oldStatus = "replacedByModified";
            predecessorEntryID = "original";

            if (!(preID == null)) {
                Entry originalEntry = new Entry(originalEntryID, name
                        , responsibilities, decision, outcome, comment
                        , dateTime, oldStatus, journalID, preID, oldCount);

                originalNoteReference.setValue(originalEntry);
            }else{
                Entry originalEntry = new Entry(originalEntryID, name
                        , responsibilities, decision, outcome, comment
                        , dateTime, oldStatus, journalID, predecessorEntryID, oldCount);

                originalNoteReference.setValue(originalEntry);
            }

            if (TextUtils.isEmpty(entryComment)) {
                entry.setEntryComment("You did not leave any comment on it");
            }
            noteReference.setValue(entry);

            //deal with media stuff
            if (uriList.size() != 0) {
                for (int i = 0; i < uriList.size(); i++) {
                    lastPath = lastPathArray.get(i);
                    imageUri = uriList.get(i);
                    filePath = storageReference.child(Constants.Users_End_Point).child(userID).child(Constants.Journals_End_Point)
                            .child(journalID).child(Constants.Entries_End_Point).child(newEntryID)
                            .child("Attachment").child(lastPath);
                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri attachmentPath = taskSnapshot.getDownloadUrl();
                            String format = "image";
                            String attachmentID = db.push().getKey();
                            long duration = 0L;
                            Attachment att = new Attachment(attachmentPath.toString(), format, attachmentID,
                                    newEntryID, duration, null);

                            DatabaseReference attachRef = db.child(Constants.Users_End_Point).child(userID)
                                    .child(Constants.Journals_End_Point)
                                    .child(journalID).child(Constants.Entries_End_Point).child(newEntryID)
                                    .child(Constants.Attachments_End_Point).child(attachmentID);

                            attachRef.setValue(att);

                        }
                    });
                }

            }
            //audio
            if (audioUriList.size() != 0){
                for (int i = 0; i < audioUriList.size(); i++){
                    audioUri = audioUriList.get(i);
                    filePath = storageReference.child(Constants.Users_End_Point).child(userID).child(Constants.Journals_End_Point)
                            .child(journalID).child(Constants.Entries_End_Point).child(newEntryID)
                            .child("Attachment").child("new_audio_" + i + ".3gp");
                    filePath.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String format = "audio";
                            Uri attachmentPath = taskSnapshot.getDownloadUrl();
                            String attachmentID = db.push().getKey();
                            audioDuration = audioDurationList.get(k);
                            attURI = audioUriList.get(k);
                            attFileName = fileNameList.get(k);
                            k++;
                            Attachment attachment = new Attachment(attachmentPath.toString(), format, attachmentID,
                                    newEntryID, audioDuration, attFileName);
                            DatabaseReference attachRef = db.child(Constants.Users_End_Point).child(userID)
                                    .child(Constants.Journals_End_Point)
                                    .child(journalID).child(Constants.Entries_End_Point).child(newEntryID)
                                    .child(Constants.Attachments_End_Point).child(attachmentID);
                            attachRef.setValue(attachment);
                        }
                    });
                }
            }
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
