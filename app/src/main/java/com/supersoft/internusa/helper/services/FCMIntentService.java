package com.supersoft.internusa.helper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by itclub21 on 10/26/2017.
 */

public class FCMIntentService extends IntentService {

    private static final String TAG = "FCMIntentService.class.getSimpleName()";

    public FCMIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
