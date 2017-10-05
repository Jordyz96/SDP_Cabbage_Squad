package com.cabbage.sdpjournal.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cabbage.sdpjournal.Model.Attachment;
import com.cabbage.sdpjournal.R;

import java.util.ArrayList;

/**
 * Created by jamen on 30/9/17.
 */

public class AttachmentImageListAdapter extends BaseAdapter {

    private Context c;
    private ArrayList<Attachment> attachments;

    public AttachmentImageListAdapter(Context c, ArrayList<Attachment> attachments) {
        this.c = c;
        this.attachments = attachments;
    }

    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public Object getItem(int i) {
        return attachments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(c).inflate(R.layout.style_image_grid, viewGroup,false);
        }
        ImageView image = (ImageView) view.findViewById(R.id.imageGrid);
        Glide.with(c).load(attachments.get(i).getPath()).into(image);
        return view;
    }
}
