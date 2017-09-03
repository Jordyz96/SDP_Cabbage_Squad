package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Constants;

public class EntryViewActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_view);

        String entryName, responsibilities, decision, outcome, comment, dateTime;

        TextView tvEntryName = (TextView) findViewById(R.id.tvEntryName);
        TextView tvRes = (TextView) findViewById(R.id.tvResponsibilities);
        TextView tvDecisions = (TextView) findViewById(R.id.tvDecisions);
        TextView tvOutcome = (TextView) findViewById(R.id.tvOutcome);
        TextView tvComment = (TextView) findViewById(R.id.tvComment);
        TextView tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        Button backBtn = (Button) findViewById(R.id.backBtnEntryView);


        entryName = getIntent().getExtras().getString("entryName");
        responsibilities = getIntent().getExtras().getString("responsibilities");
        decision = getIntent().getExtras().getString("decision");
        outcome = getIntent().getExtras().getString("outcome");
        comment = getIntent().getExtras().getString("entryComment");
        dateTime = getIntent().getExtras().getString("dateTime");


        tvEntryName.setText(entryName);
        tvRes.setText(responsibilities);
        tvDecisions.setText(decision);
        tvOutcome.setText(outcome);
        tvComment.setText(comment);
        tvDateTime.setText(dateTime);

        //btn
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        Intent intent = new Intent(this, EntryListActivity.class);
        intent.putExtra(Constants.journalID, journalID);
        startActivity(intent);
    }
}
