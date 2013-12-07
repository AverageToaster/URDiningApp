package com.violentsquid.urdiningapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by TJ Stein on 12/7/13.
 */
public class NewAdapter extends BaseExpandableListAdapter {
    public ArrayList<String> groupItem, tempChild;
    public ArrayList<Object> childItem = new ArrayList<Object>();
    public LayoutInflater minflater;
    public Activity activity;

    public NewAdapter(ArrayList<String> grList, ArrayList<Object> childItem){
        groupItem = grList;
        this.childItem = childItem;
    }

    public void setInflater(LayoutInflater mInflater, Activity act){
        this.minflater = mInflater;
        activity = act;
    }

    public Object getChild(int groupPos, int childPos){
        return null;
    }
    public long getChildId(int groupPos, int childPos){
        return 0;
    }
    public View getChildView (int groupPos,
                              final int childPos,
                              boolean isLastChild,
                              View convertView,
                              ViewGroup parent){
        tempChild = (ArrayList<String>)childItem.get(groupPos);
        TextView text = null;
        if (convertView == null){
            convertView = minflater.inflate(R.layout.child_row, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(tempChild.get(childPos));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, tempChild.get(childPos),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public int getChildrenCount(int groupPos){
        return ((ArrayList<String>) childItem.get(groupPos)).size();
    }

    public Object getGroup(int groupPos){
        return null;
    }
    public int getGroupCount(){
        return groupItem.size();
    }
    public void onGroupCollapsed(int groupPos){
        super.onGroupCollapsed(groupPos);
    }
    public void onGroupExpanded(int groupPos){
        super.onGroupExpanded(groupPos);
    }
    public long getGroupId(int groupPos){
        return 0;
    }
    public View getGroupView(int groupPos,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent){
        if (convertView == null){
            convertView = minflater.inflate(R.layout.group_row, null);
        }
        ((CheckedTextView) convertView).setText(groupItem.get(groupPos));
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }
    public boolean hasStableIds(){
        return false;
    }
    public boolean isChildSelectable(int groupPos, int childPos){
        return false;
    }
}
