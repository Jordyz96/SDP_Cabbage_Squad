package com.cabbage.sdpjournal.SwipeListView;

/**
 * Created by Junwen on 26/8/17.
 */
import android.view.View;

public interface OnSwipeListItemClickListener {
    public void OnClick(View view, int index);
    public boolean OnLongClick(View view, int index);
    public void OnControlClick(int rid,View view,int index);
}