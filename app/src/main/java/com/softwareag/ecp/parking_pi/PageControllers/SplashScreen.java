package com.softwareag.ecp.parking_pi.PageControllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.softwareag.ecp.parking_pi.R;

public class SplashScreen extends AppCompatActivity {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MESSAGE_LOG, "onCreate() SplashScreen");

        setContentView(R.layout.activity_splash_screen);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if(actionBar!= null){
            Log.i(MESSAGE_LOG, "actionBar!=NULL SplashScreen");
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(MESSAGE_LOG, "postDelayed() SplashScreen");
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
            }
        },1500);
    }
}
