package com.softwareag.ecp.parking_pi.Service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KAVI on 21-06-2016.
 */
public class HttpUrlConnectionAsyncTask extends AsyncTask<String, String, String> {
    private Activity context;
    private String branchName;

    public HttpUrlConnectionAsyncTask(Activity context, String branchName){
        this.context = context;
        this.branchName = branchName;

    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);
        StringBuilder builder = new StringBuilder();
        InputStream inputStream;
        BufferedReader br;
        String line;

        try {
            HttpURLConnection connection;
            if (branchName == null || branchName.isEmpty()) {
                connection = (HttpURLConnection) (new URL("http://" + vmName + "/parkingmgmt/locations")).openConnection();
            } else {
                connection = (HttpURLConnection) (new URL("http://" + vmName + "/parkingmgmt/locations/" + branchName)).openConnection();
            }
            Log.i("CONNECTION ", "URL " + connection);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-CentraSite-APIKey", "63ca8580-4517-11e6-bbcf-af100b5ea29c");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.connect();

            inputStream = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            Log.i("Http URL ", "Conection datas " + connection.getResponseCode());
            br.close();
            inputStream.close();
            Log.i("Http URL ", "Conection datas " + builder.toString());

        } catch (Exception e) {
            Log.e("HttpUrlConnectionAsync", "Error in HttpUrlConnectionAsyncTask " + e.getMessage());
            return null;
        }
        return builder.toString();
    }
}
