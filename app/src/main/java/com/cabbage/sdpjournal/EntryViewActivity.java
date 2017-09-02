package com.cabbage.sdpjournal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class EntryViewActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_view);

        String title, content;

        tvContent = (TextView) findViewById(R.id.tvContentInNoteView);
        tvTitle = (TextView) findViewById(R.id.tvTitleInNoteView);

        title = getIntent().getExtras().getString("titleKey");
        content = getIntent().getExtras().getString("contentKey");

        tvTitle.setText(title);
        tvContent.setText(content);
    }
}
