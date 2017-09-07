package com.cabbage.sdpjournal.Adpter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Constants;
import com.cabbage.sdpjournal.Model.Journal;
import com.cabbage.sdpjournal.EntryListActivity;
import com.cabbage.sdpjournal.R;

import java.util.ArrayList;

/**
 * Created by Junwen on 31/8/17.
 */

public class JournalListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Journal> journals;

    public JournalListAdapter(Context context, ArrayList<Journal> journals) {
        this.context = context;
        this.journals = journals;
    }

    @Override
    public int getCount() {
        return journals.size();
    }

    @Override
    public Object getItem(int i) {
        return journals.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.style_journal_list_item, viewGroup,false);
        }
        TextView tvJournalName = (TextView) view.findViewById(R.id.tvJournalName);
        TextView tvCompanyName = (TextView) view.findViewById(R.id.tvCompanyName);
        ImageView journalImage = (ImageView) view.findViewById(R.id.ivJournalImage);
        final Journal journal = (Journal) this.getItem(i);

        tvJournalName.setText(journals.get(i).getJournalName());
        tvCompanyName.setText("Company: "+journals.get(i).getCompanyName());

        if (journals.get(i).getJournalColor().equals("Red")){
            journalImage.setImageResource(R.drawable.red_journal);
        }
        if (journals.get(i).getJournalColor().equals("Green")){
            journalImage.setImageResource(R.drawable.green_journal);
        }
        if (journals.get(i).getJournalColor().equals("Blue")){
            journalImage.setImageResource(R.drawable.blue_journal);
        }
        if (journals.get(i).getJournalColor().equals("Yellow")){
            journalImage.setImageResource(R.drawable.yellow_journal);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String journalID = journal.getJournalID();
                Intent intent = new Intent(context, EntryListActivity.class);
                intent.putExtra(Constants.journalID, journalID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
