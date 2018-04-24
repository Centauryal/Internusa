package com.supersoft.internusa.helper.services;

/**
 * Created by itclub21 on 11/2/2017.
 */

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.supersoft.internusa.StartApp;

public class NotificationsService extends Service {

    @Override
    public void onCreate() {
        Log.e("NotificationsService","service started");
        StartApp.postInitApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {


        super.onTaskRemoved(rootIntent);

    }

    public void onDestroy() {
        Log.e("NotificationsService","service destroyed");
        SharedPreferences preferences = StartApp.applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);
        Intent intent = new Intent("com.supersoft.internusa.StartApp");
        sendBroadcast(intent);
    }
}
