package com.softwareag.ecp.parking_pi.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.Variables;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MOAB on 10-Jul-17.
 */

public class LocationAccessRunnable implements Runnable {

    private Context context;

    private final String MESSAGE_LOG = "PARKING_PI APP";

    public LocationAccessRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);

        try {
            HttpURLConnection connection = (HttpURLConnection)
                    (new URL(Variables.getAllLocationUrl(vmName))).openConnection();

            Log.v(MESSAGE_LOG, "LocationAccessRunnable -> Connection URL : " + connection);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-CentraSite-APIKey", Variables.getCentrasiteApikey());
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            inputStream.close();
            Log.v(MESSAGE_LOG, "forLocation Connection " + builder.toString());

            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("all_locations", builder.toString());
            editor.apply();
        } catch (Exception e) {
            Log.e(MESSAGE_LOG, "LocationAccessRunnable -> Exception : " + e.getMessage().toString());
        }
    }
}
