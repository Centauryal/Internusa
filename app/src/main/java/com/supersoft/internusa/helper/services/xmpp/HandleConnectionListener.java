package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */
import android.content.Intent;
import android.util.Log;

import com.supersoft.internusa.helper.services.ConnectionService;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class HandleConnectionListener extends StateChangeListener {

    private final XMPPService mXMPPService;

    private ConnectionListener mConnectionListener;

    public HandleConnectionListener(XMPPService xmppService) {
        mXMPPService = xmppService;
    }

    @Override
    public void connected(XMPPConnection connection) {
        mConnectionListener = new AbstractConnectionListener() {

            @Override
            public void connectionClosedOnError(Exception arg0) {
                Log.d("hconnlistener","connectionClosedOnError(): Issuing ACTION_START_SERVICE intent");
                // We don't call scheduleReconnect() here, because this method is usually be called
                // from Smack's PacketReader or PacketWriter thread, which will not have a Looper
                // (and shouldn't get one) and therefore is unable to use the reconnect handler.
                // Instead we send an START_SERVICE intent for the transport service
                Intent intent = new Intent(mXMPPService.getContext(), ConnectionService.class);
                intent.setAction(ConnectionService.ACTION_CONNECTION_CLOSED_ON_ERROR);
                mXMPPService.getContext().startService(intent);
            }

            @Override
            public void reconnectingIn(int arg0) {
                throw new IllegalStateException("Reconnection Manager is running");
            }

            @Override
            public void reconnectionFailed(Exception arg0) {
                throw new IllegalStateException("Reconnection Manager is running");
            }

            @Override
            public void reconnectionSuccessful() {
                throw new IllegalStateException("Reconnection Manager is running");
            }

        };
        connection.addConnectionListener(mConnectionListener);
    }

    @Override
    public void disconnected(XMPPConnection connection) {
        connection.removeConnectionListener(mConnectionListener);
        mConnectionListener = null;
    }
}
