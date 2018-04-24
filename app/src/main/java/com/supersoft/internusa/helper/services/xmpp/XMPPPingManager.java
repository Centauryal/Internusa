package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */
import android.util.Log;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;

public class XMPPPingManager extends StateChangeListener implements PingFailedListener {

    public static final int PING_INTERVAL_SECONDS = 60 * 30; // 30 minutes

    static {
        PingManager.setDefaultPingInterval(PING_INTERVAL_SECONDS);
    }


    private final XMPPService mXMPPService;

    protected XMPPPingManager(XMPPService service) {
        mXMPPService = service;
    }

    @Override
    public void connected(XMPPConnection connection) {
        PingManager.getInstanceFor(connection).registerPingFailedListener(this);
    }

    @Override
    public void disconnected(XMPPConnection connection) {
        PingManager.getInstanceFor(connection).unregisterPingFailedListener(this);
    }

    @Override
    public void pingFailed() {
        Log.d("XMPPPingManager","ping failed: issuing reconnect");
        mXMPPService.reconnect();
    }

}
