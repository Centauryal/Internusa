package com.supersoft.internusa.helper.services;

/**
 * Created by itclub21 on 10/24/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.supersoft.internusa.StartApp;


public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e("ScreenReceiver","screen off");
            StartApp.isScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("ScreenReceiver", "screen on");
            StartApp.isScreenOn = true;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenStateChanged);
        Intent intentt = new Intent(context, RestartServiceReceiver.class);
        intent.setAction(intent.getAction());
        context.sendBroadcast(intentt);
    }
}