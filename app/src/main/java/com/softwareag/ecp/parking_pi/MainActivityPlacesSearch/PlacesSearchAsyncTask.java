package com.softwareag.ecp.parking_pi.MainActivityPlacesSearch;

import android.os.AsyncTask;
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
public class PlacesSearchAsyncTask extends AsyncTask<String, String, String> {
    private String data = null;
    private final String MESSAGE_LOG = "PARKING_PI APP";

    // Invoked by execute() method of this object
    @Override
    protected String doInBackground(String... url) {
        try {
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d(MESSAGE_LOG, "PlacesSearchAsyncTask -> Background Task" + e.toString());
        }
        return data;
    }

    private String downloadUrl(String strUrl) throws IOException {
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
            Log.d(MESSAGE_LOG, "PlacesSearchAsyncTask -> JSON Data" + data);

        } catch (Exception e) {
            Log.d(MESSAGE_LOG, "PlacesSearchAsyncTask downloading url" + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
