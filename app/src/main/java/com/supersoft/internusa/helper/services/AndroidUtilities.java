package com.supersoft.internusa.helper.services;

import com.supersoft.internusa.StartApp;

/**
 * Created by itclub21 on 11/2/2017.
 */

public class AndroidUtilities {

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            StartApp.applicationHandler.post(runnable);
        } else {
            StartApp.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        StartApp.applicationHandler.removeCallbacks(runnable);
    }
}
