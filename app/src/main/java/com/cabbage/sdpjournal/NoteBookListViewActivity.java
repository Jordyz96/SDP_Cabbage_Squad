package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Entry;
import com.cabbage.sdpjournal.SwipeListView.OnSwipeListItemClickListener;
import com.cabbage.sdpjournal.SwipeListView.SwipeListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ArrayList<Entry> noteList;

    private ListAdapter listAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    class ViewHolder {
        public TextView title;
        public TextView dateTimeCreated;
        public Button hide;
        public Button modify;
        public Button delete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_list_view);

        addNoteButton = (Button) findViewById(R.id.addButton);
        addNoteButton.setOnClickListener(this);

        listViewNote = (SwipeListView) findViewById(R.id.listView);

        noteList = new ArrayList<>();


        listViewNote.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                String entryName = noteList.get(index).getEntryName();
                String responsibilities = noteList.get(index).getEntryResponsibilities();
                String decision = noteList.get(index).getEntryDecision();
                String outcome = noteList.get(index).getEntryOutcome();
                String entryComment = noteList.get(index).getEntryComment();


                Intent intent = new Intent(NoteBookListViewActivity.this, NoteViewActivity.class);
                intent.putExtra("entryName", entryName);
                intent.putExtra("responsibilities", responsibilities);
                intent.putExtra("decision", decision);
                intent.putExtra("outcome", outcome);
                intent.putExtra("entryComment", entryComment);
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View view, int index) {

                String entryName = noteList.get(index).getEntryName();
                String comment = noteList.get(index).getEntryComment();

                AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                View myview = getLayoutInflater().inflate(R.layout.dialog_entry_detail, null);

                TextView detailTitle = (TextView) myview.findViewById(R.id.tvEntryDetailsTitle);
                TextView detailContent = (TextView) myview.findViewById(R.id.tvEntryDetailsContent);
                Button closeButton = (Button) myview.findViewById(R.id.bCloseEntryDetails);

                detailTitle.setText(entryName);
                detailContent.setText(comment);
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
                    //if click modify from the swipe list view
                    case R.id.modify:
                        ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                        ab.setTitle("Modify");
                        ab.setMessage("You will modify item " + index);
                        ab.create().show();
                        break;
                    //if click delete
                    case R.id.delete:
                        ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                        ab.setTitle("Delete");
                        ab.setMessage("You will delete item " + index);
                        ab.create().show();
                        break;
                    //if click hide
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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = "";
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference noteRef = db.child(Constants.Users_End_Point)
                .child(userID).child("journals").child(journalID)
                .child("entries");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                noteList.clear();
                for (DataSnapshot noteDS : dataSnapshot.getChildren()) {
                    Log.d("Journal "," ==>"+noteDS.toString());
                    Entry note = noteDS.getValue(Entry.class);
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



    @Override
    public void onClick(View v) {
        if (v == addNoteButton) {
            String journalID = getIntent().getExtras().getString(Constants.journalID);
            Intent intent = new Intent(this, WriteNoteActivity.class);
            intent.putExtra("journalID", journalID);
            startActivity(intent);
            finish();
        }
    }

    //Adapter implementation below including swipeItems' onclick activities.
    private class ListAdapter extends com.cabbage.sdpjournal.Adpter.SwipeListAdpter {
        private ArrayList<Entry> listData;

        ListAdapter(ArrayList<Entry> listData) {
            this.listData = (ArrayList<Entry>) listData.clone();
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
                viewHolder.dateTimeCreated = (TextView) convertView.findViewById(R.id.tvNoteContentInStyle_list);
                viewHolder.hide = (Button) convertView.findViewById(R.id.hide);
                viewHolder.modify = (Button) convertView.findViewById(R.id.modify);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(listData.get(position).getEntryName());
            viewHolder.dateTimeCreated.setText(listData.get(position).getDateTimeCreated());
            return super.bindView(position, convertView);
        }

    }

}
