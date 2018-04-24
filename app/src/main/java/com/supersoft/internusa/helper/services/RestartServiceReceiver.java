package com.supersoft.internusa.helper.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by itclub21 on 10/11/2017.
 */

public class RestartServiceReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Intent mIntentForService = new Intent(context, ConnectionService.class);
        if(intent.getAction() != null)
        {
            mIntentForService.setAction(intent.getAction());
        }
        else
        {
            mIntentForService.setAction("com.supersoft.internusa.StartApp");
        }
        final String action = intent.getAction();

        context.startService(mIntentForService);

    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                //Log.e("serviceClassName",services.get(i).service.getClassName() + " = " +serviceClass.getName());
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
