package com.softwareag.ecp.parking_pi.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.softwareag.ecp.parking_pi.UrlConnectionRunnable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllLocationSearchService extends Service {
    public AllLocationSearchService() {

        Timer timer = new Timer();
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UrlConnectionRunnable urlConnectionRunnable = new UrlConnectionRunnable(AllLocationSearchService.this,"");
                        ExecutorService service = Executors.newFixedThreadPool(1);
                        service.execute(urlConnectionRunnable);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AllLocationSearchService.this);
                        String newDatas = preferences.getString("All locations", null);

                        Log.v("AllLocationSearch", "Service timer task" + newDatas);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 3500);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {


        throw new UnsupportedOperationException("Not yet implemented");
    }
}
