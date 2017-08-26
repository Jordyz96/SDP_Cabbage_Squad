package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cabbage.sdpjournal.NoteModel.Note;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WriteNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private Button saveButton;
    private EditText etNoteTitle;
    private EditText etNoteContent;
    private boolean validated;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        db = FirebaseDatabase.getInstance().getReference();

        saveButton = (Button) findViewById(R.id.saveButton);
        etNoteTitle = (EditText) findViewById(R.id.etTitle);
        etNoteContent = (EditText) findViewById(R.id.etContent);

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            saveNoteToDatabase();
            startActivity(new Intent(this, NoteBookListViewActivity.class));
            finish();
            return;
        }
    }

    private void saveNoteToDatabase() {
        //save data.
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();
        String id = db.push().getKey();

        //validation...
        if (TextUtils.isEmpty(title)) {
            etNoteTitle.setError("Title must not be empty");
        }
        if (TextUtils.isEmpty(content)) {
            etNoteContent.setError("Content must not be empty");
        }

        //if validates
        Note note = new Note(id, title, content);
        //save the user input to the database under the root named "notes",
        //and the subroot is the note's id
        //the root named "notes" should be changed to user's email account or something unique
        db.child("notes").child(id).setValue(note);
    }
}
