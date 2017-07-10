package com.softwareag.ecp.parking_pi.Service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.Variables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MOAB on 07-Jul-17.
 */

public class URLLocationAccess extends AsyncTask<String, String, String> {

    private Activity activity;
    private String MESSAGE_LOG = "PARKING_PI APP";

    public URLLocationAccess(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String vmName = preferences.getString("VMName", null);
        StringBuilder builder = new StringBuilder();
        InputStream inputStream;
        BufferedReader br;
        String line;
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) new URL(Variables.getAllLocationUrl(vmName)).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-CentraSite-APIKey", Variables.getCentrasiteApikey());
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.connect();

            inputStream = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            inputStream.close();
            connection.disconnect();
        } catch (IOException e) {
            Log.e("URLLocationAccess", "Error in URLLocationAccess " + e.getMessage());
        }
        return builder.toString();
    }


}
