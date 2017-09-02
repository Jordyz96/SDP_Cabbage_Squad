package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cabbage.sdpjournal.NoteModel.Constants;
import com.cabbage.sdpjournal.NoteModel.Note;
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

import static android.content.ContentValues.TAG;

public class EntryListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addNoteButton;
    private DatabaseReference db;

    private SwipeListView listViewNote;
    private ArrayList<Note> noteList;

    private ListAdapter listAdapter;

    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addNoteButton = (Button) findViewById(R.id.addButton);
        addNoteButton.setOnClickListener(this);

        listViewNote = (SwipeListView) findViewById(R.id.listView);
        noteList = new ArrayList<>();


        listViewNote.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                String title = noteList.get(index).getNoteTitle();
                String content = noteList.get(index).getNoteContent();

                Intent intent = new Intent(EntryListActivity.this, EntryViewActivity.class);
                intent.putExtra("titleKey", title);
                intent.putExtra("contentKey", content);
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View view, int index) {

                String title = noteList.get(index).getNoteTitle();
                String content = noteList.get(index).getNoteContent();

                AlertDialog.Builder ab = new AlertDialog.Builder(EntryListActivity.this);
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
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        ab.setTitle("Modify");
                        ab.setMessage("You will modify item " + index);
                        ab.create().show();
                        break;
                    case R.id.delete:
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        ab.setTitle("Delete");
                        ab.setMessage("You will delete item " + index);
                        ab.create().show();
                        break;
                    case R.id.hide:
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        ab.setTitle("Hide");
                        ab.setMessage("You will hide item " + index);
                        ab.create().show();
                        break;
                }
            }
        }, new int[]{R.id.modify, R.id.delete, R.id.hide});

        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, AUTH_OUT);
                }
                // ...
            }
        };
    }

    //On start method
    @Override
    public void onStart() {
        super.onStart();

        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        String userID = null;
        if (myFirebaseUser != null) {
            userID = myFirebaseUser.getUid();
        }
        db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference noteRef = db.child(Constants.Users_End_Point).child(userID).child(Constants.Notes_End_Point);
        noteRef.addValueEventListener(new ValueEventListener() {
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

        //Sets a listener to catch when the user is signing in.
        myFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            myFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /**
     * Creates the options menu on the action bar.
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets a listener that triggers when an option from the taskbar menu is selected.
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch(item.getItemId()){
            //If item is logout
            case R.id.action_logout:
                //Sign out of the authenticator and return to login activity.
                myFirebaseAuth.signOut();
                EntryListActivity.this.startActivity(new Intent(EntryListActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                EntryListActivity.this.startActivity(new Intent(EntryListActivity.this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            startActivity(new Intent(this, NewEntryActivity.class));
            finish();
        }
    }

    //Adapter implementation below including swipeItems' onclick activities.
    private class ListAdapter extends com.cabbage.sdpjournal.Adpter.SwipeListAdpter {
        private ArrayList<Note> listData;

        ListAdapter(ArrayList<Note> listData) {
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
            return super.bindView(position, convertView);
        }

    }

}
