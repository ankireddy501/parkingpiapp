package com.softwareag.ecp.parking_pi.MainActivityPlacesSearch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KAVI on 07-07-2016.
 */
public class PlacesSearchRunnable implements Runnable {
    private final String MESSAGE_LOG = "PARKING_PI APP";
    private String url;
    private String data = null;
    private Activity context;

    public PlacesSearchRunnable(Activity context, String url){
        this.url = url;
        this.context = context;
    }

    @Override
    public void run() {
        Log.i(MESSAGE_LOG,"PlacesSearchRunnable -> run()");
        try {
            data = downloadUrl(url);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("places", data);
            editor.apply();

        } catch (Exception e) {
            Log.e(MESSAGE_LOG,"PlacesSearchRunnable -> run() -> Exception" + e.getMessage().toString());
        }

    }

    private String downloadUrl(String strUrl) throws IOException {
        Log.i(MESSAGE_LOG,"PlacesSearchRunnable -> downloadUrl()");
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while ", "downloading url" + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }



        return data;
    }
}
