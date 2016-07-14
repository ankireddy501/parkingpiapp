package com.softwareag.ecp.parking_pi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KAVI on 29-06-2016.
 */
public class UrlConnectionRunnable implements Runnable {
    private Context context;
    private String branchName;
    ProgressDialog progress;
    HttpURLConnection connection;


    public UrlConnectionRunnable(Context context, String branchName){
        this.context = context;
        this.branchName = branchName;

    }

    @Override
    public void run() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);

        try {
            if(branchName.isEmpty()){
                connection = (HttpURLConnection)(new URL("http://"+vmName+"/parkingmgmt/locations")).openConnection();
            }
            else {
                connection = (HttpURLConnection)(new URL("http://"+vmName+"/parkingmgmt/locations/"+branchName)).openConnection();
            }
            Log.v("CONNECTION ", "URL " + connection);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-CentraSite-APIKey", "63ca8580-4517-11e6-bbcf-af100b5ea29c");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.connect();

            StringBuilder builder = new StringBuilder();
            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = br.readLine())!= null){
                builder.append(line);
            }
            br.close();
            inputStream.close();
            Log.v("URL ", "Conection " + builder.toString());

            SharedPreferences.Editor editor = preferences.edit();

            if(branchName.isEmpty()){

                editor.putString("All locations",builder.toString());
                editor.apply();
            }
            else{
                editor.putString(branchName ,builder.toString());
                editor.apply();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast toast = Toast.makeText(context, "No datas available", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0, 0);
            toast.show();
        }

    }
}
