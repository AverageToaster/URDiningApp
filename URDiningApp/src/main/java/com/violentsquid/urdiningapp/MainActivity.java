package com.violentsquid.urdiningapp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ExpandableListActivity implements ExpandableListView.OnChildClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExpandableListView expandbleLis = getExpandableListView();
        expandbleLis.setDividerHeight(2);
        expandbleLis.setGroupIndicator(null);
        expandbleLis.setClickable(true);

        setGroupData();
        setChildGroupData();

        NewAdapter mNewAdapter = new NewAdapter(groupItem, childItem);
        mNewAdapter.setInflater(
                (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                this);
        getExpandableListView().setAdapter(mNewAdapter);
        expandbleLis.setOnChildClickListener(this);

    }

    public void setGroupData(){
        groupItem.add("Douglas");
        groupItem.add("Danforth");
        groupItem.add("Wilson Commons");
        groupItem.add("Connections");
        groupItem.add("Hillside");
        groupItem.add("Starbucks");
        groupItem.add("Pura Vida");
    }

    ArrayList<String> groupItem = new ArrayList<String>();
    ArrayList<Object> childItem = new ArrayList<Object>();

    public void setChildGroupData(){
        ArrayList<String> child = new ArrayList<String>();
        child.add("Food");
        child.add("Food");
        child.add("Food");
        child.add("Food");
        childItem.add(child);

        child = new ArrayList<String>();
        child.add("Food");
        child.add("Food");
        child.add("Food");
        child.add("Food");
        childItem.add(child);
        /**
         * Add Data For Manufacture
         */
        for (int i = 2; i < groupItem.size(); i++){
            child = new ArrayList<String>();
            childItem.add(child);
        }
    }

    public boolean onChildClick(ExpandableListView parent,
                                View v,
                                int groupPosition,
                                int childPosition,
                                long id){
        Toast.makeText(MainActivity.this, "Clicked on Child",
                Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
