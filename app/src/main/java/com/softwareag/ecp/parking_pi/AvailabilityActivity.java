package com.softwareag.ecp.parking_pi;

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

import com.softwareag.ecp.parking_pi.BeanClass.Locations;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvailabilityActivity extends AppCompatActivity {
    ArrayList<Locations> locationsArrayList;
    TimerTask timerTask;
    Timer timer;
    String locationName;
    ListView listView;
    Parking_pi_ArrayAdapter arrayAdapter;
    String branchName;
    String locationBasedDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        branchName = intent.getStringExtra("branchName");
        String address = intent.getStringExtra("address");
        locationName = branchName.replaceAll("%20"," ");
        locationBasedDatas = intent.getStringExtra("LocationBasedDatas");

        listView = (ListView)findViewById(R.id.listView);
        TextView textView = (TextView)findViewById(R.id.textView5);

        textView.setText(locationName+" "+address);
        Log.v("AvailabilityActivity ","branchName "+locationName);

        locationsArrayList = new ArrayList<Locations>();
        try {
            AvailabilityActivityJsonParser parser = new AvailabilityActivityJsonParser();
            locationsArrayList = parser.getAvailability(locationBasedDatas);
            arrayAdapter = new Parking_pi_ArrayAdapter(AvailabilityActivity.this, 0, locationsArrayList);
            listView.setAdapter(arrayAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        final Handler handler = new Handler();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UrlConnectionRunnable urlConnection = new UrlConnectionRunnable(AvailabilityActivity.this, branchName );
                            ExecutorService service = Executors.newFixedThreadPool(1);
                            service.execute(urlConnection);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AvailabilityActivity.this);
                        String newDatas = preferences.getString(branchName,null);
                        Log.v("AvailabilityActivity ", "timer task datas  " + newDatas);

                        if(newDatas != null){
                            refreshArrayAdapter(newDatas);
                        }

                    }
                });
            }

        };
        timer.schedule(timerTask, 2500, 3500);


    }

    @Override
    protected void onPause() {
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

        Log.v("AvailabilityActivity ", "paused ");
    }

    public void refreshArrayAdapter(String jsonString){
        try {
            AvailabilityActivityJsonParser parser = new AvailabilityActivityJsonParser();
            List<Locations> locationsList = parser.getAvailability(jsonString);

           /* if(locationsList.get(0).isActive()) {*/
                arrayAdapter.clear();
                arrayAdapter.addAll(locationsList);
                arrayAdapter.notifyDataSetChanged();
           /* }
            else {
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.12);

                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                if(!dialog.isShowing()) {
                    dialog.show();
                }

            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
