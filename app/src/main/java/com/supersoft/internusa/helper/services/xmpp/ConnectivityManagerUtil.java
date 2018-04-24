package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityManagerUtil {

    public static final boolean hasDataConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null) return false;
        return activeNetwork.isConnected();
    }
}
