package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.NoteModel.Note;
import com.cabbage.sdpjournal.SwipeListView.OnSwipeListItemClickListener;
import com.cabbage.sdpjournal.SwipeListView.SwipeListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoteBookListViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addNoteButton;
    private DatabaseReference db;

    private SwipeListView listViewNote;
    private ArrayList<Note> noteList;

    private ListAdapter listAdapter;
//    private ArrayList<Note> listData = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_list_view);

        addNoteButton = (Button) findViewById(R.id.addButton);
        addNoteButton.setOnClickListener(this);

        db = FirebaseDatabase.getInstance().getReference();
        listViewNote = (SwipeListView) findViewById(R.id.listView);
        noteList = new ArrayList<>();


        listViewNote.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                String title = noteList.get(index).getNoteTitle();
                String content = noteList.get(index).getNoteContent();

                Intent intent = new Intent(NoteBookListViewActivity.this, NoteViewActivity.class);
                intent.putExtra("titleKey", title);
                intent.putExtra("contentKey", content);
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View view, int index) {

                String title = noteList.get(index).getNoteTitle();
                String content = noteList.get(index).getNoteContent();

                AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                View myview = getLayoutInflater().inflate(R.layout.dialog_entry_detail, null);

                TextView detailTitle = (TextView) myview.findViewById(R.id.tvEntryDetailsTitle);
                TextView detailContent = (TextView) myview.findViewById(R.id.tvEntryDetailsContent);
                Button closeButton = (Button) myview.findViewById(R.id.bCloseEntryDetails);

                detailTitle.setText(title);
                detailContent.setText(content);
                ab.setView(myview);
                final AlertDialog dialog = ab.create();
                dialog.show();

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //close
                        dialog.cancel();
                    }
                });
                return false;
            }

            @Override
            public void OnControlClick(int rid, View view, int index) {
                AlertDialog.Builder ab;
                switch (rid) {
                    case R.id.modify:
                        ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                        ab.setTitle("Modify");
                        ab.setMessage("You will modify item " + index);
                        ab.create().show();
                        break;
                    case R.id.delete:
                        ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                        ab.setTitle("Delete");
                        ab.setMessage("You will delete item " + index);
                        ab.create().show();
                        break;
                    case R.id.hide:
                        ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                        ab.setTitle("Hide");
                        ab.setMessage("You will hide item " + index);
                        ab.create().show();
                        break;
                }
            }
        }, new int[]{R.id.modify, R.id.delete, R.id.hide});
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                noteList.clear();
                for (DataSnapshot noteDS : dataSnapshot.getChildren()) {
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

    class ViewHolder {
        public TextView title;
        public TextView content;
        public Button hide;
        public Button modify;
        public Button delete;
    }

    @Override
    public void onClick(View v) {
        if (v == addNoteButton) {
            startActivity(new Intent(this, WriteNoteActivity.class));
            finish();
            return;
        }
    }

    //Adapter implementation below including swipeItems' onclick activities.
    class ListAdapter extends com.cabbage.sdpjournal.Adpter.SwipeListAdpter {
        private ArrayList<Note> listData;

        public ListAdapter(ArrayList<Note> listData) {
            this.listData = (ArrayList<Note>) listData.clone();
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

            if (convertView == null) {
                convertView = View.inflate(getBaseContext(), R.layout.style_list, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvNoteTitleInStyle_list);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tvNoteContentInStyle_list);
                viewHolder.hide = (Button) convertView.findViewById(R.id.hide);
                viewHolder.modify = (Button) convertView.findViewById(R.id.modify);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(listData.get(position).getNoteTitle());
            viewHolder.content.setText(listData.get(position).getNoteContent());
            final String title = listData.get(position).getNoteTitle();
            final String content = listData.get(position).getNoteContent();
            final int id = position;
            return super.bindView(position, convertView);
        }

    }

}
