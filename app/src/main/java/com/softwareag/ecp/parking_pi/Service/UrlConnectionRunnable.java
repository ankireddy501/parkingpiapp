package com.softwareag.ecp.parking_pi.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionRunnable implements Runnable {
    private Context context;
    private String branchName;

    private final String MESSAGE_LOG = "PARKING_PI APP";

    public UrlConnectionRunnable(Context context, String branchName) {
        this.context = context;
        this.branchName = branchName;
    }

    @Override
    public void run() {
        try {
            if (branchName.isEmpty()) {
                forLocation();
            } else {
                forLocationsWithBranch();
            }
        } catch (Exception e) {
            Log.i(MESSAGE_LOG, "URL Connection" + e.toString());
        }
    }

    private void forLocation() throws Exception {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);
        HttpURLConnection connection = null;

        connection = (HttpURLConnection)
                (new URL("http://" + vmName + "/parkingmgmt/locations")).openConnection();

        Log.v(MESSAGE_LOG, "UrlConnectionRunnable -> Connection URL " + connection);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-CentraSite-APIKey", "63ca8580-4517-11e6-bbcf-af100b5ea29c");
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

        editor.putString("All locations", builder.toString());
        editor.apply();
    }

    private void forLocationsWithBranch() throws Exception {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);
        HttpURLConnection connection = null;

        connection = (HttpURLConnection)
                (new URL("http://" + vmName + "/parkingmgmt/locations/" + branchName))
                        .openConnection();

        Log.v(MESSAGE_LOG, "Connection forLocationWithBranch URLConnection" + connection);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-CentraSite-APIKey", "63ca8580-4517-11e6-bbcf-af100b5ea29c");
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
        Log.i(MESSAGE_LOG, "Connection forLocationWithBranch inputStream" + builder.toString());

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(branchName, builder.toString());
        editor.apply();
    }

}
