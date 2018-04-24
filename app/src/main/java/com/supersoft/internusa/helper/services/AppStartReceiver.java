package com.supersoft.internusa.helper.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.supersoft.internusa.StartApp;

/**
 * Created by itclub21 on 11/2/2017.
 */

public class AppStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.e("AppStartReceiver","android.intent.action.BOOT_COMPLETED");
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                StartApp.postInitApplication();
            }
        });
    }
}
