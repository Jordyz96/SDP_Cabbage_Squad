package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button setting;
    private Button addJournalBtn;
    GridView gridView;
    ArrayList<Journal> journalArrayList;
    GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //just prevent being required to login everytime...
        FirebaseAuth myFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
        String userID = "";
        if (myFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        } else {
            //User is logged in;
            userID = myFirebaseUser.getUid();
        }


        //setting up things
        addJournalBtn = (Button) findViewById(R.id.addJournalBtn);
        setting = (Button) findViewById(R.id.bSettings);
        gridView = (GridView) findViewById(R.id.gvJournalView);

        setting.setOnClickListener(this);
        addJournalBtn.setOnClickListener(this);


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
                            Log.d("Journal ", " ==>" + journalDS.toString());
                            Journal journal = journalDS.getValue(Journal.class);
                            journalArrayList.add(journal);
                        }
                        gridViewAdapter = new GridViewAdapter(MainActivity.this, journalArrayList);
                        gridView.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == addJournalBtn) {
            //move to next Scene by clicking the TEXTVIEW
            startActivity(new Intent(this, CreateJournalActivity.class));
            finish();
            return;
        }
        if (v == setting) {
            startActivity(new Intent(this, SettingActivity.class));
            finish();
        }
    }
}
