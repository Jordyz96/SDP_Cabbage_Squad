package com.cabbage.sdpjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cabbage.sdpjournal.Adpter.EntryListAdapter;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class EntryListActivity extends AppCompatActivity implements View.OnClickListener{

    private SwipeListView entriesListView;
    private ArrayList<Entry> entriesList;
    private EntryListActivity.ListAdapter listAdapter;

    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FloatingActionButton fab;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        entriesListView = (SwipeListView) findViewById(R.id.listView);

        entriesList = new ArrayList<>();

        entriesListView.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                //Click the entry, jump to entry view...
                //Grab data that entry view needs
                String entryName = entriesList.get(index).getEntryName();
                String responsibilities = entriesList.get(index).getEntryResponsibilities();
                String decision = entriesList.get(index).getEntryDecision();
                String outcome = entriesList.get(index).getEntryOutcome();
                String entryComment = entriesList.get(index).getEntryComment();
                String entryDateTime = entriesList.get(index).getDateTimeCreated();
                String entryID = entriesList.get(index).getEntryID();
                String preID = entriesList.get(index).getPredecessorEntryID();
                String journalID = getIntent().getExtras().getString(Constants.journalID);

                //put all data into entry view
                Intent intent = new Intent(EntryListActivity.this, EntryViewActivity.class);
                intent.putExtra("entryName", entryName);
                intent.putExtra("responsibilities", responsibilities);
                intent.putExtra("decision", decision);
                intent.putExtra("outcome", outcome);
                intent.putExtra("entryComment", entryComment);
                intent.putExtra("dateTime", entryDateTime);
                intent.putExtra("entryID", entryID);
                intent.putExtra("preID", preID);
                intent.putExtra(Constants.journalID, journalID);

                //transitioning
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View view, int index) {
                //long click entries -- popup dialog showing entry details
                String entryName = entriesList.get(index).getEntryName();
                String comment = entriesList.get(index).getEntryComment();

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
            public void OnControlClick(int rid, View view, final int index) {
                AlertDialog.Builder ab;
                switch (rid) {
                    //if click delete
                    case R.id.delete:
                        ab = new AlertDialog.Builder(EntryListActivity.this);
                        View myView = getLayoutInflater().inflate(R.layout.dialog_alert_yes_or_no, null);

                        TextView alertLabel = (TextView) myView.findViewById(R.id.tvAlertLabel);
                        Button noBtn = (Button) myView.findViewById(R.id.alertBtnNo);
                        Button yesBtn = (Button) myView.findViewById(R.id.alertBtnYes);

                        alertLabel.setText("Are you sure?");
                        ab.setView(myView);
                        final AlertDialog dialog = ab.create();
                        dialog.show();
                        noBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });
                        yesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //change the status to deleted.
                                changeStatus(index, Constants.Entry_Status_Deleted);
                                dialog.cancel();
                            }
                        });
                        break;
                    //if user hides an entry, change the entry status.
                    //if click hide
                    case R.id.hide:
                        //if user hides an entry, change the entry status.
                        changeStatus(index, Constants.Entry_Status_Hidden);
                }
            }
        }, new int[]{R.id.delete, R.id.hide});

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

    //Change the status of a chosen entry on the database
    public void changeStatus(int index, String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = null;
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        String journalID = getIntent().getExtras().getString(Constants.journalID);
        String entryID = entriesList.get(index).getEntryID();
        //setting pathentryID
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference entryStatusRef = db.child(Constants.Users_End_Point).child(userID).child(Constants.Journals_End_Point)
                .child(journalID).child(Constants.Entries_End_Point).child(entryID).child("status");
        //overwrite the old value
        entryStatusRef.setValue(status);
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
        DatabaseReference entryRef = db.child(Constants.Users_End_Point)
                .child(userID).child(Constants.Journals_End_Point).child(journalID)
                .child(Constants.Entries_End_Point);
        entryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                entriesList.clear();
                //loop through the reference given to search entries that match the reference
                for (DataSnapshot entryDS : dataSnapshot.getChildren()) {
                    //test log
                    Log.d("Journal ", " ==>" + entryDS.toString());

                    Entry entry = entryDS.getValue(Entry.class);
                    //Only add entry on the database that are not hidden and deleted to the list
                    if (entry.getStatus().equals(Constants.Entry_Status_Normal)) {
                        //if status is not hidden or deleted
                        entriesList.add(entry);
                    } else {
                        //entry is deleted or hidden, which shouldn't be added to the list, just leave them
                        //in the database instead.
                    }
                }
                listAdapter = new EntryListActivity.ListAdapter(entriesList);
                entriesListView.setAdapter(listAdapter);
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
        if (v == fab){
            String journalID = getIntent().getExtras().getString(Constants.journalID);
            Intent intent = new Intent(this, NewEntryActivity.class);
            intent.putExtra(Constants.journalID, journalID);
            EntryListActivity.this.startActivity(intent);
        }
    }

    /**
     * Creates the options menu on the action bar.
     *
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.menu_entry_list, menu);
        return true;
    }

    /**
     * Sets a listener that triggers when an option from the taskbar menu is selected.
     *
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch (item.getItemId()) {
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

            case R.id.action_search:
                AlertDialog.Builder searchAB = new AlertDialog.Builder(EntryListActivity.this);
                View searchView = getLayoutInflater().inflate(R.layout.dialog_search_entries, null);
                final EditText etKeyword = (EditText) searchView.findViewById(R.id.etKeyword);
                Button seachCancelBtn = (Button) searchView.findViewById(R.id.searchCancelBtn);
                Button searchOKBtn = (Button) searchView.findViewById(R.id.searchOkBtn);

                searchAB.setView(searchView);
                final AlertDialog searchDialog = searchAB.create();
                searchDialog.show();

                searchOKBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String keyword = etKeyword.getText().toString().trim();
                        if(!TextUtils.isEmpty(keyword)){
                            searchEntriesOnKeyword(keyword);
                            searchDialog.cancel();
                        }else {
                            etKeyword.setError("Keyword must not be empty");
                        }
                    }
                });

                seachCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchDialog.cancel();
                    }
                });
                return true;
            case R.id.action_filter:
                //pop up dialog to ask the user "normal" or "all (normal + hidden + deleted)"

                AlertDialog.Builder ab = new AlertDialog.Builder(EntryListActivity.this);
                View myView = getLayoutInflater().inflate(R.layout.dialog_entry_list_filter, null);

                final RadioButton rbActive = (RadioButton) myView.findViewById(R.id.radioBtnActive);
                final RadioButton rbHidden = (RadioButton) myView.findViewById(R.id.radioBtnHidden);
                final RadioButton rbDeleted = (RadioButton) myView.findViewById(R.id.radioBtnDeleted);
                final RadioButton rbAll = (RadioButton) myView.findViewById(R.id.radioBtnAll);
                Button cancelBtn = (Button) myView.findViewById(R.id.filterCancelBtn);
                Button okBtn = (Button) myView.findViewById(R.id.filterOkBtn);

                ab.setView(myView);
                final AlertDialog dialog = ab.create();
                dialog.show();

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if click ok
                        if (!rbActive.isChecked() && !rbAll.isChecked() && !rbHidden.isChecked() && !rbDeleted.isChecked()){
                            Toast.makeText(EntryListActivity.this, "Please choose one", Toast.LENGTH_SHORT).show();
                        }

                        if (rbActive.isChecked()){
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing active entries
                            Toast.makeText(EntryListActivity.this, "Test...Active", Toast.LENGTH_SHORT).show();
                            filterEntries("normal");
                            dialog.cancel();
                        }
                        if (rbHidden.isChecked()){
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing hidden entries
                            Toast.makeText(EntryListActivity.this, "Test...hidden", Toast.LENGTH_SHORT).show();
                            filterEntries("hidden");
                            dialog.cancel();
                        }
                        if (rbDeleted.isChecked()){
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing deleted entries
                            Toast.makeText(EntryListActivity.this, "Test...deleted", Toast.LENGTH_SHORT).show();
                            filterEntries("deleted");
                            dialog.cancel();
                        }
                        if (rbAll.isChecked()){
                            //Showing all entries including hidden... deleted...
                            //put your logical stuff here for showing all entries
                            Toast.makeText(EntryListActivity.this, "Test...All", Toast.LENGTH_SHORT).show();
                            filterEntries("All");
                            dialog.cancel();
                        }
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //click cancel
                        dialog.cancel();
                    }
                });

        }
        return super.onOptionsItemSelected(item);
    }

    public class ListAdapter extends EntryListAdapter {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            EntryListActivity.ViewHolder viewHolder = new EntryListActivity.ViewHolder();

            if (convertView == null) {
                convertView = View.inflate(getBaseContext(), R.layout.style_list, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvEntryTitleInStyle_list);
                viewHolder.dateTimeCreated = (TextView) convertView.findViewById(R.id.tvEntryDateTimeInStyle_list);
                viewHolder.hide = (Button) convertView.findViewById(R.id.hide);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (EntryListActivity.ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(listData.get(position).getEntryName());
            viewHolder.dateTimeCreated.setText(listData.get(position).getDateTimeCreated());
            return super.bindView(position, convertView);
        }

    }

    public void filterEntries (String filterSelected) {
        ArrayList<Entry> entriesMatchingFilter = new ArrayList<Entry>();
        for (Entry e : entriesList) {
            if (filterSelected.equals("All")) {
                entriesMatchingFilter.add(e);
            } else if (e.getStatus().equals(filterSelected)) {
                entriesMatchingFilter.add(e);
            }
        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingFilter);
        listAdapter.notifyDataSetChanged();
    }

    public void searchEntriesOnKeyword (String searchString) {
        ArrayList<Entry> entriesMatchingSearch = new ArrayList<Entry>();
        for (Entry e : entriesList) {
            if (e.getEntryName().contains(searchString)) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryResponsibilities().contains(searchString)) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryDecision().contains(searchString)) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryOutcome().contains(searchString)) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryComment().contains(searchString)) {
                entriesMatchingSearch.add(e);
            }
        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingSearch);
        listAdapter.notifyDataSetChanged();
    }

    public void searchEntriesBetweenDates(Date startDate, Date endDate) {
        ArrayList<Entry> entriesMatchingDates = new ArrayList<Entry>();
        Date entryDate;
        for (Entry e : entriesList) {
            String[] stringDate = e.getDateTimeCreated().split(" ");
            DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            try {
                entryDate = formatter.parse(stringDate[0]);
                System.out.println(entryDate);
                if ((startDate.before(entryDate) && endDate.after(entryDate)) || startDate.equals(entryDate) ||  endDate.equals(entryDate)) {
                    entriesMatchingDates.add(e);
                }
            } catch (ParseException exp) {
                exp.printStackTrace();
            }

        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingDates);
        listAdapter.notifyDataSetChanged();
    }

    public void searchEntriesOnDate(Date date) {
        ArrayList<Entry> entriesMatchingDates = new ArrayList<Entry>();
        Date entryDate;
        for (Entry e : entriesList) {
            String[] stringDate = e.getDateTimeCreated().split(" ");
            DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            try {
                entryDate = formatter.parse(stringDate[0]);
                System.out.println(entryDate);
                if (date.equals(entryDate)) {
                    entriesMatchingDates.add(e);
                }
            } catch (ParseException exp) {
                exp.printStackTrace();
            }

        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingDates);
        listAdapter.notifyDataSetChanged();
    }

}
