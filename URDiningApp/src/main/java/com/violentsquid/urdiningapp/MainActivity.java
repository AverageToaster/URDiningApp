package com.violentsquid.urdiningapp;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    ListView list;
    TextView location;
    TextView name;
    TextView api;
    TextView time;
    Button Btngetdata;
    Button EventData;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    //URL to get JSON Array
    private static String url = "http://www.ryanpuffer.com/tj.json";

    //JSON Node Names
    private static final String TAG_FOOD = "dininghalls";
    private static final String TAG_LOC = "location";
    private static final String TAG_STATIONS = "stations";
    private static final String TAG_STATION_NAME = "name";
    private static final String TAG_STATION_FOOD = "food";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_EVENTS_TITLE = "title";
    private static final String TAG_EVENTS_LOCATION = "location";
    private static final String TAG_EVENTS_COST = "cost";


    JSONArray android = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        oslist = new ArrayList<HashMap<String, String>>();

        Btngetdata = (Button)findViewById(R.id.getfood);
        EventData = (Button)findViewById(R.id.getevents);
        Btngetdata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new JSONParse("food").execute();

            }
        });
        EventData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new JSONParse("events").execute();

            }
        });
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private String calling;

        public JSONParse(String callingButton){
            calling = callingButton;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            location = (TextView)findViewById(R.id.vers);
            name = (TextView)findViewById(R.id.name);
            api = (TextView)findViewById(R.id.api);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromURL(url);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                oslist.clear();
                // Getting JSON Array from URL
                if (calling.equals("food")){
                    Calendar cal = Calendar.getInstance();
                    int hr = cal.get(Calendar.HOUR_OF_DAY);
                    int min = cal.get(Calendar.MINUTE);

                    android = json.getJSONArray("dininghalls");
                    for(int i = 0; i < android.length(); i++){
                        JSONObject c = android.getJSONObject(i);
                        String loc = c.getString(TAG_LOC);
                        System.out.println(loc);
                        String food = "\n";
                        if (c.has("stations")){
                            food += "STATIONS:\n";
                            JSONArray stationArr = c.getJSONArray("stations");
                            for (int j = 0; j < stationArr.length(); j++){
                                JSONObject d = stationArr.getJSONObject(j);
                                food += d.getString(TAG_STATION_NAME) + ": \n";
                                if (!d.getString("food").equals("null")){
                                    JSONArray foodArr = d.getJSONArray(TAG_STATION_FOOD);
                                    for (int k = 0; k < foodArr.length(); k++){
                                        food += "\t" + foodArr.getString(k) + "\n";
                                    }
                                }
                                else{
                                    food += "\tNothing\n";
                                }
                            }
                            // Adding value HashMap key => value
                        }
                        String open = c.getString("open");
                        String close = c.getString("close");

                        int openHours = Integer.parseInt(open.substring(open.indexOf('T')+1,open.indexOf(':')));
                        int openMinutes = Integer.parseInt(open.substring(open.indexOf(':')+1));
                        int closeHours = Integer.parseInt(close.substring(close.indexOf('T')+1,close.indexOf(':')));
                        int closeMinutes = Integer.parseInt(close.substring(close.indexOf(':')+1));
                        String hours = "\n";
                        if (openHours > 12){
                            openHours %= 12;
                            if (openMinutes == 0)
                                hours += "Open " + openHours + ":00 PM";
                            else
                                hours += "Open " + openHours + ":" + openMinutes + " PM";
                        }
                        else{
                            if (openHours == 0)
                                openHours = 12;
                            if (openMinutes == 0)
                                hours += "Open " + openHours + ":00 AM";
                            else
                                hours += "Open " + openHours + ":" + openMinutes + " AM";
                        }
                        if (closeHours > 12){
                            closeHours %= 12;
                            if (closeMinutes == 0)
                                hours += " until " + closeHours + ":00 PM";
                            else
                                hours += " until " + closeHours + ":" + closeMinutes + " PM";
                        }
                        else{
                            if (closeHours == 0)
                                closeHours = 12;
                            if (closeMinutes == 0)
                                hours += " until " + closeHours + ":00 AM";
                            else
                                hours += " until " + closeHours + ":" + closeMinutes + " AM";
                        }
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_LOC, loc);
                        map.put(TAG_STATIONS, hours);
                        map.put(TAG_STATION_FOOD, food);

                        oslist.add(map);
                        list=(ListView)findViewById(R.id.list);

                        ListAdapter adapter = new SimpleAdapter(MainActivity.this, oslist,
                                R.layout.list_v,
                                new String[] {TAG_LOC, TAG_STATIONS, TAG_STATION_FOOD}, new int[] {
                                R.id.vers,R.id.name, R.id.api});

                        list.setAdapter(adapter);


                    }
                }
                else if (calling.equals("events")){
                    list = (ListView)findViewById(R.id.list);
                    list.setAdapter(null);
                    android = json.getJSONArray("events");
                    for(int i = 0; i < android.length(); i++){
                        JSONObject c = android.getJSONObject(i);

                        // Storing  JSON item in a Variable
                        String title = c.getString("title");
                        String loc = "LOCATION: "+c.getString("location");
                        String cost = "COST: $" +c.getString("cost");
                        // Adding value HashMap key => value

                        HashMap<String, String> map = new HashMap<String, String>();
                        String open = c.getString("open");
                        String close = c.getString("close");

                        int openHours = Integer.parseInt(open.substring(open.indexOf('T')+1,open.indexOf(':')));
                        int openMinutes = Integer.parseInt(open.substring(open.indexOf(':')+1));
                        int closeHours = Integer.parseInt(close.substring(close.indexOf('T')+1,close.indexOf(':')));
                        int closeMinutes = Integer.parseInt(close.substring(close.indexOf(':')+1));
                        String hours = "";
                        if (openHours > 12){
                            openHours %= 12;
                            if (openMinutes == 0)
                                hours += "" + openHours + ":00 PM";
                            else
                                hours += "" + openHours + ":" + openMinutes + " PM";
                        }
                        else{
                            if (openHours == 0)
                                openHours = 12;
                            if (openMinutes == 0)
                                hours += "" + openHours + ":00 AM";
                            else
                                hours += "" + openHours + ":" + openMinutes + " AM";
                        }
                        if (closeHours > 12){
                            closeHours %= 12;
                            if (closeMinutes == 0)
                                hours += " to " + closeHours + ":00 PM";
                            else
                                hours += " to " + closeHours + ":" + closeMinutes + " PM";
                        }
                        else{
                            if (closeHours == 0)
                                closeHours = 12;
                            if (closeMinutes == 0)
                                hours += " until " + closeHours + ":00 AM";
                            else
                                hours += " until " + closeHours + ":" + closeMinutes + " AM";
                        }
                        map.put(TAG_EVENTS_TITLE, title);
                        map.put(TAG_EVENTS_LOCATION, loc);
                        map.put("time", hours);
                        map.put(TAG_EVENTS_COST, cost);

                        oslist.add(map);
                        list=(ListView)findViewById(R.id.list);

                        ListAdapter adapter = new SimpleAdapter(MainActivity.this, oslist,
                                R.layout.list_v,
                                new String[] {TAG_EVENTS_TITLE, TAG_EVENTS_LOCATION,"time", TAG_EVENTS_COST}, new int[] {
                                R.id.vers,R.id.name, R.id.time, R.id.api});

                        list.setAdapter(adapter);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}



