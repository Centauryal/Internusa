package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.OnNetworkActiveListener;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import org.jivesoftware.smack.tcp.BundleAndDefer;
import org.jivesoftware.smack.tcp.BundleAndDeferCallback;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.concurrent.atomic.AtomicInteger;

public class XMPPBundleAndDefer {

    /**
     * How long Smack defers outgoing stanzas if the current network is in high power (active)
     * state.
     */
    private static final int ACTIVE_STATE_DEFER_MILLIS = 150;

    /**
     * How long Smack defers outgoing stanzas if the current network is not in high power (inactive)
     * state.
     */
    private static final int INACTIVE_STATE_DEFER_MILLIS = 23 * 1000;


    /**
     * Integer value indication when not to BAD (BundleAndDefer). If its value is greater zero, then
     * bundle and
     * defer while not take place.
     */
    private static final AtomicInteger sDoNotBadInt = new AtomicInteger();

    /**
     * The current BundleAndDefer instance, which can be used to stop the current bundle and defer
     * process by Smack. Once it's stopped, the bundled stanzas so far will be send immediately.
     */
    private static BundleAndDefer currentBundleAndDefer;

    @TargetApi(21)
    public static void initialize(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        BundleAndDeferCallback bundleAndDeferCallback = new BundleAndDeferCallback() {
            @Override
            public int getBundleAndDeferMillis(BundleAndDefer bundleAndDefer) {
                if (sDoNotBadInt.get() > 0) {
                    return 0;
                }
                XMPPBundleAndDefer.currentBundleAndDefer = bundleAndDefer;
                String networkState = "unknown (needs Android >= 5.0)";
                boolean networkActive = false;
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    if (connectivityManager.isDefaultNetworkActive()) {
                        networkState = "active";
                        networkActive = true;
                    } else {
                        networkState = "incative";
                    }
                }
                boolean isPlugged = isPlugged(context);
                final int deferMillis;
                if (isPlugged || networkActive) {
                    deferMillis = ACTIVE_STATE_DEFER_MILLIS;
                } else {
                    deferMillis = INACTIVE_STATE_DEFER_MILLIS;
                }
                Log.d("XMPPBundleDefer","Returning " + deferMillis
                        + "ms in getBundleAndDeferMillis(). Network is "
                        + networkState + ", batteryPlugged: " + isPlugged);
                return deferMillis;
            }
        };
        XMPPTCPConnection.setDefaultBundleAndDeferCallback(bundleAndDeferCallback);

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            connectivityManager.addDefaultNetworkActiveListener(new OnNetworkActiveListener() {
                @Override
                public void onNetworkActive() {
                    stopCurrentBundleAndDefer();
                }
            });
        }
    }

    public static void stopCurrentBundleAndDefer() {
        final BundleAndDefer localCurrentbundleAndDefer = currentBundleAndDefer;
        if (localCurrentbundleAndDefer == null) {
            return;
        }
        Log.d("XMPPBundleDefer","stopCurrentBundleAndDefer() invoked and currentbundleAndDefer not null, calling stopCurrentBundleAndDefer()");
        localCurrentbundleAndDefer.stopCurrentBundleAndDefer();
    }

    /**
     * Disables bundle and defer until {@link #enableBundleAndDefer()} is called.
     */
    public static void disableBundleAndDefer() {
        sDoNotBadInt.incrementAndGet();
        stopCurrentBundleAndDefer();
    }

    /**
     * Re-enables bundle and defer. {@link #disableBundleAndDefer()} must be called prior calling
     * this.
     */
    public static void enableBundleAndDefer() {
        sDoNotBadInt.decrementAndGet();
    }

    private static final IntentFilter BATTERY_CHANGED_INTENT_FILTER = new IntentFilter(
            Intent.ACTION_BATTERY_CHANGED);

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean isPlugged(Context context) {
        // BATTERY_CHANGED_INTENT is a sticky broadcast intent
        final Intent intent = context.registerReceiver(null, BATTERY_CHANGED_INTENT_FILTER);
        final int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isPlugged;
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
            case BatteryManager.BATTERY_PLUGGED_USB:
                isPlugged = true;
                break;
            default:
                isPlugged = false;
                break;
        }
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            isPlugged |= plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }

        return isPlugged;
    }
}
