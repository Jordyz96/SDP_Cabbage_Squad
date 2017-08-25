package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView lnikToNoteActivity;
    private FirebaseAuth myFirebaseAuth;
    private FirebaseUser myFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //just prevent being required to login everytime...
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        if(myFirebaseUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }else {
            //User is logged in;
        }

        //just a link to the listView for testing...
        lnikToNoteActivity = (TextView) findViewById(R.id.tvLinkToListView);
        lnikToNoteActivity.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == lnikToNoteActivity){
            //move to next Scene by clicking the TEXTVIEW
            startActivity(new Intent(this, NoteBookListViewActivity.class));
            finish();
            return;
        }
    }
}
