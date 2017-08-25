package com.cabbage.sdpjournal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NoteViewActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        String title, content;

        tvContent = (TextView) findViewById(R.id.tvContentInNoteView);
        tvTitle = (TextView) findViewById(R.id.tvTitleInNoteView);

        title = getIntent().getExtras().getString("Title");
        content = getIntent().getExtras().getString("Content");

        tvTitle.setText(title);
        tvContent.setText(content);
    }
}
