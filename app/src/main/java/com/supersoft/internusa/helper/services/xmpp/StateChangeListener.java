package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;

public class StateChangeListener {

    public void newConnection(XMPPConnection connection) {}

    @SuppressWarnings("unused")
    public void connected(XMPPConnection connection) throws NotConnectedException {}

    /**
     * Invoked when we got disconnected from an active connection
     *
     * @param connection
     *            the connection that got disconnected, may be null
     */
    public void disconnected(XMPPConnection connection) {}

    /**
     * Invoked when get return to disconnected state, but there was never an
     * active connection. For example when something in the connection stage
     * went wrong
     *
     * @param reason
     */
    public void disconnected(String reason) {}

    // These callback methods don't get access to the connection instance
    // because they will be called in the middle of a state change

    public void connecting() {

    }

    public void disconnecting() {

    }

    public void waitingForNetwork() {

    }

    public void waitingForRetry(String reason) {

    }

}
