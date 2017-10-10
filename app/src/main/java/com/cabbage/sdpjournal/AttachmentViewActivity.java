package com.cabbage.sdpjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.cabbage.sdpjournal.Adpter.AttachmentImageListAdapter;
import com.cabbage.sdpjournal.Model.Attachment;
import com.cabbage.sdpjournal.Model.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AttachmentViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference attachmentRef;
    FirebaseAuth.AuthStateListener mAuthListener;
    String userID = "";
    String entryID, journalID;

    AttachmentImageListAdapter listAdapter;
    AttachmentAudioListAdapter audioListAdapter;
    ListView lv;
    GridView gv;

    String fileName;
    private MediaPlayer mPlayer = null;
    boolean mStartPlaying;
    boolean isStopped;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseSetup();

        gv = (GridView) findViewById(R.id.gvImageView);
        lv = (ListView) findViewById(R.id.lvAudioListView);
        mStartPlaying = true;

    }


    private void firebaseSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        entryID = getIntent().getExtras().getString("entryID");
        journalID = getIntent().getExtras().getString(Constants.journalID);

        attachmentRef = databaseReference.child(Constants.Users_End_Point)
                .child(userID).child(Constants.Journals_End_Point).child(journalID)
                .child(Constants.Entries_End_Point).child(entryID).child(Constants.Attachments_End_Point);


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
                firebaseAuth.signOut();
                AttachmentViewActivity.this.startActivity(new Intent(AttachmentViewActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                AttachmentViewActivity.this.startActivity(new Intent(AttachmentViewActivity.this, ResetPasswordActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        firebaseAuth.addAuthStateListener(mAuthListener);

        //Loading media
        final ArrayList<Attachment> imageList = new ArrayList<>();
        final ArrayList<Attachment> audioList = new ArrayList<>();
        attachmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageList.clear();
                audioList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Attachment attachment = ds.getValue(Attachment.class);
                    //loading images
                    if (!attachment.getFormat().equals("audio")) {
                        Log.d("Image ", " ==>" + ds.toString());
                        imageList.add(attachment);
                    }
                    //loading audio
                    if (attachment.getFormat().equals("audio")){
                        Log.d("Audio ", " ==>" + ds.toString());
                        audioList.add(attachment);
                    }

                    listAdapter = new AttachmentImageListAdapter(AttachmentViewActivity.this, imageList);
                    gv.setAdapter(listAdapter);
                    audioListAdapter = new AttachmentAudioListAdapter(AttachmentViewActivity.this, audioList);
                    lv.setAdapter(audioListAdapter);

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
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        isStopped = true;
    }

    //adapter for audio list
    public class AttachmentAudioListAdapter extends BaseAdapter {
        private Context c;
        private ArrayList<Attachment> attachments;
        Drawable stop, start;

        AttachmentAudioListAdapter(Context c, ArrayList<Attachment> attachments) {
            this.c = c;
            this.attachments = attachments;
        }

        @Override
        public int getCount() {
            return attachments.size();
        }

        @Override
        public Object getItem(int i) {
            return attachments.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.style_audio_list, viewGroup, false);
            }

            final Button playBtn = (Button) view.findViewById(R.id.playBtn);
            TextView tvDur = (TextView) view.findViewById(R.id.durationLabel);
            Constants con = new Constants();
            long duration = attachments.get(i).getDuration();
            long dur = con.removeLastNDigits(duration, 3);
            String durText = dur + "sec";
            tvDur.setText(durText);
            fileName = attachments.get(i).getFileName();
            stop = view.getResources().getDrawable(android.R.drawable.ic_media_pause);
            start = view.getResources().getDrawable(android.R.drawable.ic_media_play);

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mStartPlaying) {
                        playBtn.setBackground(stop);
                        onPlay(mStartPlaying);
                    } else {
                        playBtn.setBackground(start);
                        stopPlaying();
                        mStartPlaying = true;
                    }
                    mStartPlaying = !mStartPlaying;

                    if (isStopped){
                        playBtn.setBackground(start);
                        mStartPlaying = true;
                    }
                }
            });
            return view;
        }
    }



}
