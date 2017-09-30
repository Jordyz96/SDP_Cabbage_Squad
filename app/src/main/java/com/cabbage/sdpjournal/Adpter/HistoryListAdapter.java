package com.cabbage.sdpjournal.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Entry;
import com.cabbage.sdpjournal.R;

import java.util.ArrayList;

/**
 * Created by jamen on 30/9/17.
 */

public class HistoryListAdapter extends BaseAdapter {

    private Context c;
    private ArrayList<Entry> entries;

    public HistoryListAdapter(Context c, ArrayList<Entry> entries) {
        this.c = c;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(c).inflate(R.layout.style_history_list, viewGroup,false);
        }
        TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTimeLabel);
        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

        String content = "Responsibilities: " + entries.get(i).getEntryResponsibilities()
                + "\nDecision: " + entries.get(i).getEntryDecision() + "\nOutcome: "
                + entries.get(i).getEntryOutcome();

        tvDateTime.setText(entries.get(i).getDateTimeCreated());
        tvContent.setText(content);

        return view;
    }
}
