package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class JournalListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lnikToNoteActivity;
    private Button setting;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseUser myFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //just prevent being required to login everytime...
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        if (myFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
        }

        //just a link to the listView for testing...
        lnikToNoteActivity = (TextView) findViewById(R.id.tvLinkToListView);
        lnikToNoteActivity.setOnClickListener(this);

        setting = (Button) findViewById(R.id.bSettings);
        setting.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == lnikToNoteActivity) {
            //move to next Scene by clicking the TEXTVIEW
            startActivity(new Intent(this, EntryListActivity.class));
        }
        if (v == setting){
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
    }
}
