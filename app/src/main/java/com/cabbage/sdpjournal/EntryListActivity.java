
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

import static android.content.ContentValues.TAG;

public class EntryListActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    private SwipeListView listViewNote;
    private ArrayList<Entry> noteList;
    private ListAdapter listAdapter;

    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;


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
        setContentView(R.layout.activity_entry_list);

        firebaseAuth = FirebaseAuth.getInstance();

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                String entryDateTime = noteList.get(index).getDateTimeCreated();
                String journalID = getIntent().getExtras().getString(Constants.journalID);

                Intent intent = new Intent(EntryListActivity.this, EntryViewActivity.class);
                intent.putExtra("entryName", entryName);
                intent.putExtra("responsibilities", responsibilities);
                intent.putExtra("decision", decision);
                intent.putExtra("outcome", outcome);
                intent.putExtra("entryComment", entryComment);
                intent.putExtra("dateTime", entryDateTime);
                intent.putExtra(Constants.journalID, journalID);
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View view, int index) {
                //long click entries -- popup dialog
                String entryName = noteList.get(index).getEntryName();
                String comment = noteList.get(index).getEntryComment();

                AlertDialog.Builder ab = new AlertDialog.Builder(EntryListActivity.this);
                View myView = getLayoutInflater().inflate(R.layout.dialog_entry_detail, null);

                TextView detailTitle = (TextView) myView.findViewById(R.id.tvEntryDetailsTitle);
                TextView detailContent = (TextView) myView.findViewById(R.id.tvEntryDetailsContent);
                Button closeButton = (Button) myView.findViewById(R.id.bCloseEntryDetails);

                detailTitle.setText(entryName);
                detailContent.setText(comment);
                ab.setView(myView);
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
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        ab.setTitle("Modify");
                        ab.setMessage("You will modify item " + index);
                        ab.create().show();
                        break;
                    //if click delete
                    case R.id.delete:
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        ab.setTitle("Delete");
                        ab.setMessage("You will delete item " + index);
                        ab.create().show();
                        break;
                    //if click hide
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
                    Log.d(TAG, Constants.AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, Constants.AUTH_OUT);
                }
                // ...
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        firebaseAuth.addAuthStateListener(mAuthListener);
        //setting up fireBase stuff
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //get userID
        String userID = "";
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        //get back the journalID
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        //setting up reference
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference noteRef = db.child(Constants.Users_End_Point)
                .child(userID).child(Constants.Journals_End_Point).child(journalID)
                .child(Constants.Entries_End_Point);
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                noteList.clear();
                for (DataSnapshot noteDS : dataSnapshot.getChildren()) {
                    Log.d("Journal ", " ==>" + noteDS.toString());
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


    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {
        //.
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
                firebaseAuth.signOut();
                EntryListActivity.this.startActivity(new Intent(EntryListActivity.this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                EntryListActivity.this.startActivity(new Intent(EntryListActivity.this, ResetPasswordActivity.class));
                return true;

            case R.id.action_add:
                String journalID = getIntent().getExtras().getString(Constants.journalID);
                Intent intent = new Intent(this, NewEntryActivity.class);
                intent.putExtra(Constants.journalID, journalID);
                EntryListActivity.this.startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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