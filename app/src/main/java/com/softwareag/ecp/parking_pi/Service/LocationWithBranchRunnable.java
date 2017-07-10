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

public class LocationWithBranchRunnable implements Runnable {

    private final String MESSAGE_LOG = "PARKING_PI APP";
    private Context context;
    private String branchName;

    public LocationWithBranchRunnable(Context context, String branchName) {
        this.context = context;
        this.branchName = branchName;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)
                    (new URL(Variables.getBranchLocationUrl(vmName, branchName)))
                            .openConnection();

            Log.v(MESSAGE_LOG, "Connection LocationWithBranchRunnable URLConnection" + connection);
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
            Log.i(MESSAGE_LOG, "Connection LocationWithBranchRunnable inputStream" + builder.toString());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(branchName, builder.toString());
            editor.apply();
        } catch (Exception e) {
            Log.e(MESSAGE_LOG, "Connection LocationWithBranchRunnable -> Exception: " + e.getMessage().toString());
        }
    }
}
