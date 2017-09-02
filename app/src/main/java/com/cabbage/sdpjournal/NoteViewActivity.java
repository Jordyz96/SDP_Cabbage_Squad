package com.cabbage.sdpjournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Constants;

public class NoteViewActivity extends AppCompatActivity implements View.OnClickListener{


    private Button backBtn;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        String entryName, entryResponsibilities, entryDecision, entryOutcome, entryComment, entryDateTime;

        TextView tvEntryName = (TextView) findViewById(R.id.tvEntryName);
        TextView tvResponsibilities = (TextView) findViewById(R.id.tvResponsibilities);
        TextView tvDecision = (TextView) findViewById(R.id.tvDecisions);
        TextView tvOutcome = (TextView) findViewById(R.id.tvOutcome);
        TextView tvComment = (TextView) findViewById(R.id.tvComment);
        TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);

        backBtn = (Button) findViewById(R.id.noteViewBackBtn);
        backBtn.setOnClickListener(this);

        entryName = getIntent().getExtras().getString("entryName");
        entryResponsibilities = getIntent().getExtras().getString("responsibilities");
        entryDecision = getIntent().getExtras().getString("decision");
        entryOutcome = getIntent().getExtras().getString("outcome");
        entryComment = getIntent().getExtras().getString("entryComment");
        entryDateTime = getIntent().getExtras().getString("dateTime");

        tvEntryName.setText("Name: "+entryName);
        tvResponsibilities.setText("Resonsibilities: "+entryResponsibilities);
        tvDecision.setText("Decision: "+entryDecision);
        tvOutcome.setText("Outcome: "+entryOutcome);
        tvComment.setText("Comment: "+entryComment);
        tvDateTime.setText("Recorded: "+entryDateTime);

    }

    @Override
    public void onClick(View view) {
        if (view == backBtn){
            backToEntryViewWithExtra();
            finish();
        }
    }
    private void backToEntryViewWithExtra(){
        //Must !!! put back the journalID to EntryView
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        Intent intent = new Intent(this, NoteBookListViewActivity.class);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
    }
}
