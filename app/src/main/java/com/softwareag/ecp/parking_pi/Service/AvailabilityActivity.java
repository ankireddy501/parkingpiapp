package com.softwareag.ecp.parking_pi.Service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.softwareag.ecp.parking_pi.BeanClass.Location;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.AvailabilityActivityJsonParser;
import com.softwareag.ecp.parking_pi.PageControllers.LayoutView1;
import com.softwareag.ecp.parking_pi.PageControllers.Parking_pi_ArrayAdapter;
import com.softwareag.ecp.parking_pi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvailabilityActivity extends AppCompatActivity {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    private TimerTask timerTask;
    private Timer timer;
    private LayoutView1 layoutViewArrayAdapter;
    private Parking_pi_ArrayAdapter parkingpiArrayAdapter;
    private String branchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(MESSAGE_LOG, "AvailabilityActivity -> conCreate()");

        AvailabilityActivityJsonParser parser = new AvailabilityActivityJsonParser();
        ArrayList<Location> locationsArrayList;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.i(MESSAGE_LOG, "AvailabilityActivity -> actionBar!=NULL");
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        branchName = intent.getStringExtra("branchName");
        String address = intent.getStringExtra("address");
        String locationName = branchName.replaceAll("%20", " ");
        String locationBasedData = intent.getStringExtra("LocationBasedDatas");

        ListView listView = (ListView) findViewById(R.id.listView);
        TextView textView = (TextView) findViewById(R.id.addressBar);

        textView.setText(locationName + " " + address);
        Log.i("AvailabilityActivity ", "branchName " + locationName);
        try {
            if (locationBasedData == null) {
                Log.i(MESSAGE_LOG, "AvailabilityActivity -> locationBasedData == null");
                //Do what has to be done if it is null
                return;
            }
            locationsArrayList = parser.getAvailability(locationBasedData);

            JSONObject json = new JSONObject(locationBasedData);

            String layout =  json.getString("layout");

            if (layout.equals("LISTVIEW")){
                layoutViewArrayAdapter = new LayoutView1(AvailabilityActivity.this, 0, locationsArrayList);
                listView.setAdapter(layoutViewArrayAdapter);
            }else {
                parkingpiArrayAdapter = new Parking_pi_ArrayAdapter(AvailabilityActivity.this, 0, locationsArrayList);
                listView.setAdapter(parkingpiArrayAdapter);
            }

        } catch (JSONException e) {
            Log.e(MESSAGE_LOG, "AvailabilityActivity -> JSONException" + e.getMessage().toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(MESSAGE_LOG, "AvailabilityActivity -> onStart()");
        final Handler handler = new Handler();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LocationWithBranchRunnable runnableConnection = new LocationWithBranchRunnable(
                                AvailabilityActivity.this, branchName);
                        ExecutorService service = Executors.newFixedThreadPool(1);
                        service.execute(runnableConnection);

                        SharedPreferences preferences = PreferenceManager.
                                getDefaultSharedPreferences(AvailabilityActivity.this);
                        String newData = preferences.getString(branchName, null);
                        Log.i(MESSAGE_LOG, "AvailabilityActivity -> timer task datas" + newData);

                        if (newData != null) {
                            Log.i(MESSAGE_LOG, "AvailabilityActivity -> timer task datas  " + newData);
                            refreshArrayAdapter(newData);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 2500, 3500);
    }

    @Override
    protected void onPause() {
        Log.i(MESSAGE_LOG, "AvailabilityActivity -> onPause()");
        super.onPause();
        timer.cancel();
        timerTask.cancel();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        /* locationName from the shared preference is cleared to avoid duplicate datas.
         When the user clicks on the marker in the google map  AvailabilityActivity will get rendered
         with the desired search results, when the user is in the same page (AvailabilityActivity page)
         the connection will get refreshed for every 3.5 sec and the new datas will be stored in the shared
         preference when the user goes back to main activity (map) the timer task in the AvailabilityActivity will
         be  stopped. Again when he comes to the availability activity old datas will still be available in the shared
         preference this will render wrong datas in the array adapter, to avoid this ambiguity location name is removed
         from the shared preference */
        editor.remove(branchName);
        editor.apply();
    }

    public void refreshArrayAdapter(String jsonString) {
        Log.i(MESSAGE_LOG, "AvailabilityActivity -> refreshArrayAdapter()");
        try {
            AvailabilityActivityJsonParser parser = new AvailabilityActivityJsonParser();
            List<Location> locationsList = parser.getAvailability(jsonString);

            JSONObject json = new JSONObject(jsonString);
            String layout =  json.getString("layout");
            if(layout.equals("LISTVIEW")){
                layoutViewArrayAdapter.clear();
                layoutViewArrayAdapter.addAll(locationsList);
                layoutViewArrayAdapter.notifyDataSetChanged();
            }else{
                parkingpiArrayAdapter.clear();
                parkingpiArrayAdapter.addAll(locationsList);
                parkingpiArrayAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e(MESSAGE_LOG, "AvailabilityActivity -> JSONException" + e.getMessage().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(MESSAGE_LOG, "AvailabilityActivity -> onOptionsItemSelected()");

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        Log.i(MESSAGE_LOG, "AvailabilityActivity -> onOptionsItemSelected() -> " + super.onOptionsItemSelected(item));
        return super.onOptionsItemSelected(item);
    }
}
