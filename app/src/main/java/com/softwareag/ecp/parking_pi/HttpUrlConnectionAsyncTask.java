package com.softwareag.ecp.parking_pi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
 * Created by KAVI on 21-06-2016.
 */
public class HttpUrlConnectionAsyncTask extends AsyncTask<String, String, String> {
    private Activity context;
    private String branchName;
    ProgressDialog progress;
    HttpURLConnection connection;

    public HttpUrlConnectionAsyncTask(Activity context, String branchName){
        this.context = context;
        this.branchName = branchName;

    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String vmName = preferences.getString("VMName", null);

        try {

            if(branchName.isEmpty()){
                connection = (HttpURLConnection)(new URL("http://"+vmName+"/parkingmgmt/locations")).openConnection();
            }
            else {
                connection = (HttpURLConnection)(new URL("http://"+vmName+"/parkingmgmt/locations/"+branchName)).openConnection();
            }
            Log.v("CONNECTION ","URL "+connection);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-Gateway-APIKey", "63ca8580-4517-11e6-bbcf-af100b5ea29c");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.connect();

            StringBuilder builder = new StringBuilder();
            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = br.readLine())!= null){
                builder.append(line);
            }
            Log.v("Http URL ","Conection datas "+connection.getResponseCode());
            br.close();
            inputStream.close();
            Log.v("Http URL ","Conection datas "+builder.toString());

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Toast toast = Toast.makeText(context, "No datas available", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0, 0);
            toast.show();
        }
        return null;
    }

   /* @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if(String.valueOf(connection.getResponseCode()).equals("200")){
                progress.dismiss();
            }else{
                Toast.makeText(context, "Couldnot connect to the server ", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}
