package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cabbage.sdpjournal.NoteModel.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteBookListViewActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addNoteButton;
    private DatabaseReference db;

    private ListView listViewNote;
    private ArrayList<Note> noteList;

    private ListAdapter listAdapter;
    private ArrayList<Note> listData = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_list_view);

        addNoteButton = (Button) findViewById(R.id.addButton);
        addNoteButton.setOnClickListener(this);

        db = FirebaseDatabase.getInstance().getReference();
        listViewNote = (ListView) findViewById(R.id.listView);
        noteList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                noteList.clear();
                for (DataSnapshot noteDS : dataSnapshot.getChildren()){
                    Note note = noteDS.getValue(Note.class);
                    noteList.add(note);
                }
                listAdapter = new ListAdapter(noteList);
                listViewNote.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class ViewHolder{
        public TextView title;
        public TextView content;
        public Button hide;
        public Button modify;
        public Button delete;
    }

    @Override
    public void onClick(View v) {
        if (v == addNoteButton){
            startActivity(new Intent(this, WriteNoteActivity.class));
            finish();
            return;
        }
    }

    //Adapter implementation below including swipeItems' onclick activities.
    class ListAdapter extends BaseAdapter {
        private ArrayList<Note> listData;
        public ListAdapter(ArrayList<Note> listData){
            this.listData= (ArrayList<Note>) listData.clone();
        }
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();

            if(convertView == null){
                convertView = View.inflate(getBaseContext(),R.layout.style_list,null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvNoteTitleInStyle_list);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvNoteContentInStyle_list);
                viewHolder.hide = (Button) convertView.findViewById(R.id.hide);
                viewHolder.modify = (Button) convertView.findViewById(R.id.modify);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(listData.get(position).getNoteTitle());
            viewHolder.content.setText(listData.get(position).getNoteContent());
            final String title = listData.get(position).getNoteTitle();
            final int id = position;
            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Title");
                    ab.setMessage("You are clicking "+title);
                    ab.create().show();
                }
            });
            viewHolder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Content");
                    ab.setMessage("You are clicking "+title);
                    ab.create().show();
                }
            });
            viewHolder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Hide");
                    ab.setMessage("You will hide "+title);
                    ab.create().show();
                }
            });
            viewHolder.modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Modify");
                    ab.setMessage("You will modify "+title);
                    ab.create().show();
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Delete");
                    ab.setMessage("You will delete "+title);
                    ab.create().show();
                }
            });
            return convertView;
        }

    }

}
