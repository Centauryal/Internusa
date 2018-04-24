package com.supersoft.internusa.helper.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.supersoft.internusa.helper.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itclub21 on 11/12/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    private ConnectivityManager mManager;
    private List<NetworkStateReceiverListener> mListeners;
    private boolean mConnected;
    private Context mContext;


    public NetworkStateReceiver(Context context) {
        mContext = context;
        mListeners = new ArrayList<NetworkStateReceiverListener>();
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkStateChanged();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        if (checkStateChanged()) notifyStateToAll();
    }

    private boolean checkStateChanged() {
        boolean prev = mConnected;
        NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
        mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != mConnected;
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (listener != null) {
            if (mConnected) listener.onNetworkAvailable();
            else listener.onNetworkUnavailable();

            Intent intent = new Intent(mContext, ConnectionService.class);
            if (mConnected) {
                intent.setAction(Constant.ACTION_NETWORK_CONNECTED);
            } else {
                intent.setAction(Constant.ACTION_NETWORK_DISCONNECTED);
            }

            mContext.startService(intent);
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        mListeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        mListeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        void onNetworkAvailable();

        void onNetworkUnavailable();
    }
}
