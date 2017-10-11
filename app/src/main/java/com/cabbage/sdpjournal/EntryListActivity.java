package com.cabbage.sdpjournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.content.ContentValues.TAG;

public class EntryListActivity extends AppCompatActivity implements View.OnClickListener {

    private SwipeListView entriesListView;
    private ArrayList<Entry> entriesList, hiddenList, deletedList, allEntryList;
    private int countVersion;
    private ArrayList<Integer> countVersionList;
    private EntryListActivity.ListAdapter listAdapter;
    private EditText searchText;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FloatingActionButton fab;
    //    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private DatePickerDialog.OnDateSetListener mFromDateSetListener;
    private DatePickerDialog.OnDateSetListener mToDateSetListener;
    private String s = null;

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

//        getSupportActionBar().setTitle("Material Search");
//        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

//         searchView=(MaterialSearchView) findViewById(R.id.search_view);
//
//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, entriesList);
//
//        entriesListView = (SwipeListView) findViewById(R.id.listView);
//
//
//
//        entriesListView.setAdapter(adapter);
//
//        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//
//                //If closed search view, listview will return default
//                ArrayAdapter adapter = new ArrayAdapter(EntryListActivity.this, android.R.layout.simple_list_item_1, entriesList);
//                entriesListView.setAdapter(adapter);
//            }
//        });
//
//
//        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(newText != null && !newText.isEmpty()){
//                    ArrayList lstFound = new ArrayList<>();
//                    for (Entry item:entriesList){
//                        lstFound.add(item);
//                    }
//
//                    ArrayAdapter adapter = new ArrayAdapter(EntryListActivity.this, android.R.layout.simple_list_item_1, lstFound);
//                    entriesListView.setAdapter(adapter);
//
//
//                }
//                else{
//                    //if search text is null
//                    //return default
//                    ArrayAdapter adapter = new ArrayAdapter(EntryListActivity.this, android.R.layout.simple_list_item_1, entriesList);
//                    entriesListView.setAdapter(adapter);
//                }
//                return true;
//            }
//        });
        init();

        // entriesListView = (ListView) findViewById(R.id.listView);

    }


    private void init() {
        entriesListView = (SwipeListView) findViewById(R.id.listView);

        entriesList = new ArrayList<>();
        hiddenList = new ArrayList<>();
        deletedList = new ArrayList<>();
        allEntryList = new ArrayList<>();

        entriesListView.setListener(new OnSwipeListItemClickListener() {
            @Override
            public void OnClick(View view, int index) {
                //Click the entry, jump to entry view...
                //Grab data that entry view needs
                int test = index;
//                String entryName = entriesList.get(index).getEntryName();
//                String responsibilities = entriesList.get(index).getEntryResponsibilities();
//                String decision = entriesList.get(index).getEntryDecision();
//                String outcome = entriesList.get(index).getEntryOutcome();
//                String entryComment = entriesList.get(index).getEntryComment();
//                String entryDateTime = entriesList.get(index).getDateTimeCreated();
//                String entryID = entriesList.get(index).getEntryID();
//                String preID = entriesList.get(index).getPredecessorEntryID();

                Entry e = (Entry) entriesListView.getAdapter().getItem(index);
                String entryName = e.getEntryName();
                String responsibilities = e.getEntryResponsibilities();
                String decision = e.getEntryDecision();
                String outcome = e.getEntryOutcome();
                String entryComment = e.getEntryComment();
                String entryDateTime = e.getDateTimeCreated();
                String entryID = e.getEntryID();
                String preID = e.getPredecessorEntryID();
                int count = entriesList.get(index).getCountAttachment();
                int countVersion = entriesList.get(index).getCountVersion();
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
                intent.putExtra("count", count);
                intent.putExtra("countVersion", countVersion);
                intent.putExtra(Constants.journalID, journalID);

                //transitioning
                startActivity(intent);
            }


            @Override
            public boolean OnLongClick(View view, int index) {
                //long click entries -- popup dialog showing entry details
                Entry e = (Entry) entriesListView.getAdapter().getItem(index);

                String entryName = e.getEntryName();
                int countAttachment = e.getCountAttachment();
                int countVersion = e.getCountVersion();

                AlertDialog.Builder ab = new AlertDialog.Builder(EntryListActivity.this);
                View myView = getLayoutInflater().inflate(R.layout.dialog_entry_detail, null);

                TextView detailTitle = (TextView) myView.findViewById(R.id.tvEntryDetailsTitle);
                TextView tvNumOfVersions = (TextView) myView.findViewById(R.id.tvNumOfVersions);
                TextView tvNumOfAttachment = (TextView) myView.findViewById(R.id.tvNumOfAttachment);

                Button closeButton = (Button) myView.findViewById(R.id.bCloseEntryDetails);

                detailTitle.setText(entryName);

                if (countVersion == 0){
                    tvNumOfVersions.setText("This is an original entry");
                }else {
                    tvNumOfVersions.setText("This entry has " + countVersion + " Previous versions");
                }

                tvNumOfAttachment.setText("This entry has " + countAttachment + " Attachments");
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
                Entry e = (Entry) entriesListView.getAdapter().getItem(index);
                String status = e.getStatus();
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
                        if (status.equals(Constants.Entry_Status_Hidden)) {
                            changeStatus(index, Constants.Entry_Status_Normal);
                        }
                        if (status.equals(Constants.Entry_Status_Normal)){
                            changeStatus(index, Constants.Entry_Status_Hidden);
                        }
                        if (status.equals(Constants.Entry_Status_Deleted)){
                            Toast.makeText(EntryListActivity.this, "This entry has been deleted", Toast.LENGTH_SHORT).show();
                        }
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
        String entryID = (String) ((Entry) entriesListView.getAdapter().getItem(index)).getEntryID();
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
                hiddenList.clear();
                deletedList.clear();
                allEntryList.clear();
                //loop through the reference given to search entries that match the reference
                for (DataSnapshot entryDS : dataSnapshot.getChildren()) {
                    //test log
                    Log.d("Journal Entry", " ==>" + entryDS.toString());
                    Entry entry = entryDS.getValue(Entry.class);
                    //if status is not hidden or deleted
                    if (entry.getStatus().equals(Constants.Entry_Status_Normal)) {
                        //normal list
                        entriesList.add(entry);
                        allEntryList.add(entry);
                    }
                    if (entry.getStatus().equals(Constants.Entry_Status_Hidden)){
                        //hidden list
                        hiddenList.add(entry);
                        allEntryList.add(entry);
                    }
                    if (entry.getStatus().equals(Constants.Entry_Status_Deleted)){
                        //deleted list
                        deletedList.add(entry);
                        allEntryList.add(entry);
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
        if (v == fab) {
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

            case R.id.action_search_menu:
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
                        if (!TextUtils.isEmpty(keyword)) {
                            searchEntriesOnKeyword(keyword);
                            searchDialog.cancel();
                        } else {
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

                //eric coming lol**************************************** search on a day
                final TextView mDisplayDate = (TextView) myView.findViewById(R.id.tvDate);

                mDisplayDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(
                                EntryListActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                mDateSetListener,
                                year, month, day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });

                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;

                        Log.d(TAG, "onDateSet: dd/mm/yyy: " + dayOfMonth + "-" + String.format("%02d", month) + "-" + year);

                        String date = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", month) + "-" + year;
                        mDisplayDate.setText(date);
                    }

                };

                ///eric endhere*********************************


                //////// between two date start
                final TextView mDisplayFromDate = (TextView) myView.findViewById(R.id.fromDate);
                mDisplayFromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal1 = Calendar.getInstance();
                        int year1 = cal1.get(Calendar.YEAR);
                        int month1 = cal1.get(Calendar.MONTH);
                        int day1 = cal1.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog1 = new DatePickerDialog(
                                EntryListActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                mFromDateSetListener,
                                year1, month1, day1);
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog1.show();
                    }
                });
                mFromDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth1) {
                        month1 = month1 + 1;

                        Log.d(TAG, "onDateSet: dd/mm/yyy: " + dayOfMonth1 + "-" + String.format("%02d", month1) + "-" + year1);

                        String date1 = String.format("%02d", dayOfMonth1) + "-" + String.format("%02d", month1) + "-" + year1;
                        mDisplayFromDate.setText(date1);
                    }

                };
                ////////end1
                final TextView mDisplayToDate = (TextView) myView.findViewById(R.id.toDate);
                mDisplayToDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal2 = Calendar.getInstance();
                        int year1 = cal2.get(Calendar.YEAR);
                        int month1 = cal2.get(Calendar.MONTH);
                        int day1 = cal2.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog2 = new DatePickerDialog(
                                EntryListActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                mToDateSetListener,
                                year1, month1, day1);
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.show();
                    }
                });
                mToDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth2) {
                        month2 = month2 + 1;

                        Log.d(TAG, "onDateSet: dd/mm/yyy: " + dayOfMonth2 + "-" + String.format("%02d", month2) + "-" + year2);

                        String date2 = String.format("%02d", dayOfMonth2) + "-" + String.format("%02d", month2) + "-" + year2;
                        mDisplayToDate.setText(date2);

                    }

                };


                ////////between two date end

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if click ok
                        if (!rbActive.isChecked() && !rbAll.isChecked() && !rbHidden.isChecked() && !rbDeleted.isChecked() && mDisplayDate.getText().equals("")
                                && mDisplayFromDate.getText().equals("") && mDisplayToDate.getText().equals("")) {
                            Toast.makeText(EntryListActivity.this, "Please choose one", Toast.LENGTH_SHORT).show();
                        }

                        if (rbActive.isChecked()) {
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing active entries
                            Toast.makeText(EntryListActivity.this, "Test...Active", Toast.LENGTH_SHORT).show();
                            filterEntries("normal");
                            dialog.cancel();
                        }
                        if (rbHidden.isChecked()) {
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing hidden entries
                            Toast.makeText(EntryListActivity.this, "Test...hidden", Toast.LENGTH_SHORT).show();
                            filterEntries("hidden");
                            dialog.cancel();
                        }
                        if (rbDeleted.isChecked()) {
                            //Filter... Only shows active entries
                            //put your logical stuff here for showing deleted entries
                            Toast.makeText(EntryListActivity.this, "Test...deleted", Toast.LENGTH_SHORT).show();
                            filterEntries("deleted");
                            dialog.cancel();
                        }
                        if (rbAll.isChecked()) {
                            //Showing all entries including hidden... deleted...
                            //put your logical stuff here for showing all entries
                            Toast.makeText(EntryListActivity.this, "Test...All", Toast.LENGTH_SHORT).show();
                            filterEntries("All");
                            dialog.cancel();
                        }

                        //  Calendar cal = Calendar.getInstance();
                        // String s = null;

                        if (!mDisplayDate.getText().equals("")) {
                            //showing all entries on a day
                            s = mDisplayDate.getText().toString();
                            filterEntriesOnDate(mDisplayDate.getText().toString());
                            dialog.cancel();
                        }
                        if (!mDisplayFromDate.getText().equals("") && !mDisplayToDate.getText().equals(""))

                            searchEntriesBetweenDates(convert(mDisplayFromDate.getText().toString()), convert(mDisplayToDate.getText().toString()));
                        dialog.cancel();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //click cancel
                        dialog.cancel();
                    }
                });
                return true;

            case R.id.action_search:
                //pop up a dialog ask the author to enter a key word.

                AlertDialog.Builder sb = new AlertDialog.Builder(EntryListActivity.this);
                View sView = getLayoutInflater().inflate(R.layout.dialog_entry_list_search, null);

                searchText = (EditText) sView.findViewById(R.id.searchText);
                Button cancel2Btn = (Button) sView.findViewById(R.id.searchCancelBtn);
                Button searchBtn = (Button) sView.findViewById(R.id.searchBtn);
                sb.setView(sView);
                final AlertDialog dialog2 = sb.create();
                dialog2.show();

                searchBtn.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        //if click searchBtn
                        searchEntriesOnKeyword(searchText.getText().toString());
                        dialog2.cancel();
                    }

                });


                cancel2Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //click cancel
                        dialog2.cancel();
                    }
                });
                return true;


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
                String status = listData.get(position).getStatus();
                if (status.equals(Constants.Entry_Status_Hidden)){
                    viewHolder.hide.setText("Unhide");
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (EntryListActivity.ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(listData.get(position).getEntryName());
            viewHolder.dateTimeCreated.setText(listData.get(position).getDateTimeCreated());
            return super.bindView(position, convertView);
        }
    }

    public void filterEntries(String filterSelected) {
        listAdapter.notifyDataSetChanged();
        listAdapter.listData.clear();
        if (filterSelected.equals("All")) {
            listAdapter = new EntryListActivity.ListAdapter(allEntryList);
            entriesListView.setAdapter(listAdapter);
        }
        if (filterSelected.equals("normal")) {
            listAdapter = new EntryListActivity.ListAdapter(entriesList);
            entriesListView.setAdapter(listAdapter);
        }
        if (filterSelected.equals("hidden")) {
            listAdapter = new EntryListActivity.ListAdapter(hiddenList);
            entriesListView.setAdapter(listAdapter);
        }
        if (filterSelected.equals("deleted")) {
            listAdapter = new EntryListActivity.ListAdapter(deletedList);
            entriesListView.setAdapter(listAdapter);
        }
        listAdapter.notifyDataSetChanged();
    }

    public void searchEntriesOnKeyword(String searchString) {
        ArrayList<Entry> entriesMatchingSearch = new ArrayList<Entry>();
        for (Entry e : entriesList) {
            if (e.getEntryName().toLowerCase().contains(searchString.toLowerCase())) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryResponsibilities().toLowerCase().contains(searchString.toLowerCase())) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryDecision().toLowerCase().contains(searchString.toLowerCase())) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryOutcome().toLowerCase().contains(searchString.toLowerCase())) {
                entriesMatchingSearch.add(e);
            } else if (e.getEntryComment().toLowerCase().contains(searchString.toLowerCase())) {
                entriesMatchingSearch.add(e);
            }
        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingSearch);
        listAdapter.notifyDataSetChanged();
    }

    public void searchEntriesBetweenDates(Date startDate, Date endDate) {
        ArrayList<Entry> entriesMatchingDates = new ArrayList<Entry>();
        for (Entry e : entriesList) {
            String[] stringDate = e.getDateTimeCreated().split(" ");
            String entryDateString = stringDate[0];
            Date entryDate = convert(e.getDateTimeCreated());
            if ((startDate.before(entryDate) && endDate.after(entryDate)) || startDate.equals(entryDate) || endDate.equals(entryDate)) {
                entriesMatchingDates.add(e);
            }
        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingDates);
        listAdapter.notifyDataSetChanged();
    }

//    public void searchEntriesBetweenDates(Date startDate, Date endDate) {
//        ArrayList<Entry> entriesMatchingDates = new ArrayList<Entry>();
//        Date entryDate;
//        for (Entry e : entriesList) {
//            String[] stringDate = e.getDateTimeCreated().split(" ");
//            DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
//            try {
//                entryDate = formatter.parse(stringDate[0]);
//                System.out.println(entryDate);
//                if ((startDate.before(entryDate) && endDate.after(entryDate)) || startDate.equals(entryDate) ||  endDate.equals(entryDate)) {
//                    entriesMatchingDates.add(e);
//                }
//            } catch (ParseException exp) {
//                exp.printStackTrace();
//            }
//
//        }
//        listAdapter.listData.clear();
//        listAdapter.listData.addAll(entriesMatchingDates);
//        listAdapter.notifyDataSetChanged();
//    }

    public void filterEntriesOnDate(String s2) {
        ArrayList<Entry> entriesMatchingDates = new ArrayList<Entry>();
//        Date entryDate;
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        String s2 = formatter.format(date2);
        for (Entry e : entriesList) {
            String[] stringDate = e.getDateTimeCreated().split(" ");
            String s1 = stringDate[0];
            if (s1.equals(s2)) {
                entriesMatchingDates.add(e);
            }
        }
        listAdapter.listData.clear();
        listAdapter.listData.addAll(entriesMatchingDates);
        listAdapter.notifyDataSetChanged();

    }
//    public Date convert(String s){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//        Date convertedDate = new Date();
//        try {
//            convertedDate = dateFormat.parse(s);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return convertedDate;
//    }


    public Date convert(String s) {
        DateFormat formatter;
        Date date = null;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = (Date) formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //System.out.println("Today is " +date.getTime());
        return date;
    }
}