package com.cabbage.sdpjournal;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteBookListViewActivity extends AppCompatActivity {

    private ListView listView;
    private ListAdapter listAdapter;
    private ArrayList<Info> listData = new ArrayList<Info>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book_list_view);

        //Hard coding stuff below...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!for testing
        listView = (ListView) findViewById(R.id.listView);
        for(int a=0;a<59;a++){
            Info info = new Info();
            info.name = "Title here "+a;
            info.desc = "descriptions ";
            info.timeCreated = "5:"+a+"pm";
            listData.add(info);
        }
        listAdapter = new ListAdapter(listData);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class Info{
        public String name="";
        public String desc="";
        public String timeCreated="";
    }

    class ViewHolder{
        public TextView name;
        public TextView desc;
        public TextView timeCreated;
        public Button hide;
        public Button modify;
        public Button delete;
    }
    //Hard coding stuff above...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! for testing

    //Adapter implementation below
    class ListAdapter extends BaseAdapter {
        private ArrayList<Info> listData;
        public ListAdapter(ArrayList<Info> listData){
            this.listData= (ArrayList<Info>) listData.clone();
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
            ViewHolder viewHolder = new ViewHolder();

            if(convertView == null){
                convertView = View.inflate(getBaseContext(),R.layout.style_list,null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
                viewHolder.timeCreated = (TextView) convertView.findViewById(R.id.textViewTimeCreated);
                viewHolder.hide = (Button) convertView.findViewById(R.id.hide);
                viewHolder.modify = (Button) convertView.findViewById(R.id.modify);
                viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(listData.get(position).name);
            viewHolder.desc.setText(listData.get(position).desc);
            final int id = position;
            viewHolder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Hide");
                    ab.setMessage("You will hide list "+id);
                    ab.create().show();
                }
            });
            viewHolder.modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Modify");
                    ab.setMessage("You will modify list "+id);
                    ab.create().show();
                }
            });
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(NoteBookListViewActivity.this);
                    ab.setTitle("Delete");
                    ab.setMessage("You will delete list "+id);
                    ab.create().show();
                }
            });

            return convertView;
        }
    }
}
