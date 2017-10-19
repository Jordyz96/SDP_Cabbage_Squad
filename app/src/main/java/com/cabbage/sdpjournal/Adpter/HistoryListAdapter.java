package com.cabbage.sdpjournal.Adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cabbage.sdpjournal.Model.Attachment;
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(c).inflate(R.layout.style_history_list, viewGroup,false);
        }
        TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
        TextView tvRes = (TextView) view.findViewById(R.id.tvRes);
        TextView tvDe = (TextView) view.findViewById(R.id.tvDecisions);
        TextView tvOut = (TextView) view.findViewById(R.id.tvOutcome);
        TextView tvCount = (TextView) view.findViewById(R.id.tvCount);

        String date = entries.get(i).getDateTimeCreated();
        String res = entries.get(i).getEntryResponsibilities();
        String de = entries.get(i).getEntryDecision();
        String out = entries.get(i).getEntryOutcome();
        int count = entries.get(i).getCountAttachment();

        tvDateTime.setText(date);
        tvRes.setText(res);
        tvDe.setText(de);
        tvOut.setText(out);
        tvCount.setText(count + "");

        return view;
    }
}
