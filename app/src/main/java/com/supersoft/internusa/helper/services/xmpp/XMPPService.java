package com.supersoft.internusa.helper.services.xmpp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.model.InfoItem;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.model.MessageBroadcast;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smackx.address.packet.MultipleAddresses;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by itclub21 on 1/22/2018.
 */

public class XMPPService {
    private static XMPPService sXMPPService;

    private final Set<StateChangeListener> mStateChangeListeners = new CopyOnWriteArraySet<StateChangeListener>();
    private final Settings mSettings;
    private final Context mContext;
    private State mState = State.Disconnected;
    private final HandleTransportStatus mHandleTransportStatus;


    private final Runnable mReconnectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("xmppservice","scheduleReconnect: calling tryToConnect");
            tryToConnect();
        }
    };


    private boolean mConnected = false;

    private XMPPTCPConnectionConfiguration mConnectionConfiguration;
    private XMPPTCPConnection mConnection;
    private Handler mReconnectHandler;

    private int mReconnectionAttemptCount;

    public static synchronized XMPPService getInstance(Context context) {
        if (sXMPPService == null) sXMPPService = new XMPPService(context);
        return sXMPPService;
    }

    private XMPPService(Context context) {
        XMPPBundleAndDefer.initialize(context);

        mContext = context;
        mSettings = Settings.getInstance(context);

        addListener(new SendStanzaDatabaseHandler(this));
        addListener(new HandleChatPacketListener(this));
        //addListener(new HandleMessagesListener(this));
        addListener(new HandleConnectionListener(this));
        addListener(new XMPPPingManager(this));

        mHandleTransportStatus = new HandleTransportStatus(context);
        addListener(mHandleTransportStatus);
    }

    public enum State {
        Connected, Connecting, Disconnecting, Disconnected, InstantDisconnected, WaitingForNetwork, WaitingForRetry
    }

    public State getCurrentState() {
        return mState;
    }

    public boolean isConnected() {
        return (getCurrentState() == State.Connected);
    }

    public HandleTransportStatus getHandleTransportStatus() {
        return mHandleTransportStatus;
    }

    public void addListener(StateChangeListener listener) {
        mStateChangeListeners.add(listener);
    }

    public void removeListener(StateChangeListener listener) {
        synchronized (mStateChangeListeners) {
            mStateChangeListeners.remove(listener);
        }
    }

    public void connect() {
        changeState(XMPPService.State.Connected);
    }

    public void disconnect() {
        changeState(XMPPService.State.Disconnected);
    }

    public void instantDisconnect() {
        changeState(XMPPService.State.InstantDisconnected);
    }

    public void reconnect() {
        disconnect();
        connect();
    }


    //public void setStatus(CurrentStatus status) {
    //    mXMPPStatus.setStatus(status);
    //}


    public void networkDisconnected() {
        changeState(State.WaitingForNetwork);
    }

    public XMPPConnection getConnection() {
        return mConnection;
    }

    public boolean fastPingServer() {
        if (mConnection == null) return false;
        PingManager pingManager = PingManager.getInstanceFor(mConnection);
        XMPPBundleAndDefer.disableBundleAndDefer();
        try {
            return pingManager.pingMyServer(false, 1500);
        } catch (InterruptedException | SmackException.NotConnectedException e) {
            return false;
        } finally {
            XMPPBundleAndDefer.enableBundleAndDefer();
        }
    }

    Context getContext() {
        return mContext;
    }


    public void send(Jid to, String body) {
        switch (mState) {
            case Disconnected:
            case Disconnecting:
                Log.e("xmppservice","Transport is disconnected, not going to send message to " + to);
                return;
            default:
                break;
        }
        if (!shouldUseXmppConnection()) {
            Log.e("xmppservice","Connection is not connected and no resumption possible, not going to send message to "
                    + to);
            return;
        }
        Message message = new Message();
        message.setTo(to);
        message.setBody(body);
        try {
            mConnection.sendStanza(message);
        } catch (InterruptedException | SmackException.NotConnectedException e) {
            Log.e("xmppservicesend","send", e);
        }
    }

    public void sendKomenByPacket(final ArrayList<MessageBroadcast> mUser)
    {
        switch (mState) {
            case Disconnected:
            case Disconnecting:
                Log.e("xmppservice","Transport is disconnected, not going to send message to ");
                return;
            default:
                break;
        }
        if (!shouldUseXmppConnection()) {
            Log.e("xmppservice","Connection is not connected and no resumption possible, not going to send message to ");
            return;
        }
        MultipleAddresses multipleAddresses = new MultipleAddresses();

    }

    public void sendKomentar(final ArrayList<MessageBroadcast> mUser) {
        switch (mState) {
            case Disconnected:
            case Disconnecting:
                Log.e("xmppservice","Transport is disconnected, not going to send message to ");
                return;
            default:
                break;
        }
        if (!shouldUseXmppConnection()) {
            Log.e("xmppservice","Connection is not connected and no resumption possible, not going to send message to ");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(MessageBroadcast bcast: mUser)
                {
                    try
                    {
                        String username = bcast.username + "-"+ mContext.getResources().getString(R.string.CONF_MITRAID);
                        InfoTipe info = new InfoTipe();
                        List<InfoItem> details = new ArrayList<>();
                        InfoItem item = new InfoItem();
                        item.tipe = "komentar";
                        item.infoid = bcast.infoid;
                        item.lastkomen = bcast.id;
                        details.add(item);
                        info.id = "10"+bcast.infoid+""+bcast.id;
                        info.body = bcast.body;
                        info.title = bcast.title;
                        info.mitraid = mContext.getResources().getString(R.string.CONF_MITRAID);
                        info.detail = details;
                        username = username.toLowerCase() +"@"+ Constant.XMPP_HOST;
                        EntityBareJid jid = null;
                        try {
                            jid = JidCreate.entityBareFrom(username);
                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.setTo(jid);
                        message.setBody(new Gson().toJson(info));
                        try {
                            mConnection.sendStanza(message);

                        } catch (InterruptedException | SmackException.NotConnectedException e) {
                            Log.e("xmppservicesend","send", e);
                        }
                    }catch (NullPointerException e)
                    {

                    }

                }
            }
        }).start();

    }

    private void scheduleReconnect(String optionalReason) {
        if (mReconnectHandler == null) mReconnectHandler = new Handler();
        newState(State.WaitingForRetry, optionalReason);
        mReconnectHandler.removeCallbacks(mReconnectRunnable);
        int reconnectDelaySeconds;
        final int MINIMAL_DELAY_SECONDS = 10;
        final int ATTEMPTS_WITHOUT_PENALTY = 60;
        if (mReconnectionAttemptCount <= ATTEMPTS_WITHOUT_PENALTY) {
            reconnectDelaySeconds = MINIMAL_DELAY_SECONDS;
        } else {
            int delayFunctionResult = MINIMAL_DELAY_SECONDS * ((int) Math.pow(mReconnectionAttemptCount - ATTEMPTS_WITHOUT_PENALTY - 1, 1.2));
            // Maximum delay is 30 minutes
            reconnectDelaySeconds = Math.max(delayFunctionResult, 60 * 30);
        }
        mReconnectionAttemptCount++;
        Log.d("XMPPService","scheduleReconnect: scheduling reconnect in " + reconnectDelaySeconds + " seconds");
        mReconnectHandler.postDelayed(mReconnectRunnable, reconnectDelaySeconds * 1000);
    }

    private void newState(State newState) {
        newState(newState, "");
    }

    private synchronized void newState(State newState, String reason) {
        if (reason == null) reason = "";
        synchronized (mStateChangeListeners) {
            mState = newState;
            switch (newState) {
                case Connected:
                    for (StateChangeListener l : mStateChangeListeners) {
                        try {
                            l.connected(mConnection);
                        } catch (SmackException.NotConnectedException e) {
                            Log.d("newstate","newState", e);
                            // Do not call 'changeState(State.Disconnected)' here, instead simply
                            // schedule reconnect since we obviously didn't reach the connected state.
                            // Changing the state to Disconnected will create a transition from
                            // 'Connecting' to 'Disconnected', which why avoid implementing here
                            scheduleReconnect("Disconnected while connecting");
                            return;
                        }
                    }
                    EventBus.getDefault().post(Constant.XMPP_CONNECTED);
                    mConnected = true;
                    break;
                case InstantDisconnected:
                case Disconnected:
                    for (StateChangeListener l : mStateChangeListeners) {
                        l.disconnected(reason);
                        if (mConnection != null && mConnected) l.disconnected(mConnection);
                    }
                    EventBus.getDefault().post(Constant.XMPP_NOT_CONNECT);
                    mConnected = false;
                    break;
                case Connecting:
                    for (StateChangeListener l : mStateChangeListeners)
                        l.connecting();
                    break;
                case Disconnecting:
                    for (StateChangeListener l : mStateChangeListeners)
                        l.disconnecting();
                    break;
                case WaitingForNetwork:
                    for (StateChangeListener l : mStateChangeListeners)
                        l.waitingForNetwork();
                    break;
                case WaitingForRetry:
                    for (StateChangeListener l : mStateChangeListeners)
                        l.waitingForRetry(reason);
                    break;
                default:
                    break;
            }
        }
    }

    private synchronized void changeState(State desiredState) {
        Log.d("changeState","changeState: mState=" + mState + ", desiredState=" + desiredState);
        switch (mState) {
            case Connected:
                switch (desiredState) {
                    case Connected:
                        break;
                    case Disconnected:
                        disconnectConnection(false);
                        break;
                    case InstantDisconnected:
                    case WaitingForNetwork:
                        disconnectConnection(true);
                        newState(desiredState);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                break;
            case InstantDisconnected:
            case Disconnected:
                switch (desiredState) {
                    case InstantDisconnected:
                    case Disconnected:
                        break;
                    case Connected:
                        tryToConnect();
                        break;
                    case WaitingForNetwork:
                        newState(State.WaitingForNetwork);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                break;
            case WaitingForNetwork:
                switch (desiredState) {
                    case WaitingForNetwork:
                        break;
                    case Connected:
                        tryToConnect();
                        break;
                    case InstantDisconnected:
                    case Disconnected:
                        newState(desiredState);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                break;
            case WaitingForRetry:
                switch (desiredState) {
                    case WaitingForNetwork:
                        newState(State.WaitingForNetwork);
                        break;
                    case Connected:
                        // Do nothing here, instead, wait until the reconnect runnable did it's job.
                        // Otherwise deadlocks may occur, because the connection attempts will block the
                        // main thread, which will prevent SmackAndroid from receiving the
                        // ConnecvitvityChange receiver and calling Resolver.refresh(). So we have no
                        // up-to-date DNS server information, which will cause connect to fail.
                        break;
                    case InstantDisconnected:
                    case Disconnected:
                        newState(desiredState);
                        mReconnectHandler.removeCallbacks(mReconnectRunnable);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                break;
            default:
                throw new IllegalStateException("changeState: Unknown state change combination. mState="
                        + mState + ", desiredState=" + desiredState);
        }
    }


    private synchronized void tryToConnect() {
        String failureReason = mSettings.checkIfReadyToConnect();
        if (failureReason != null) {
            Log.e("tryToConnect","tryToConnect: failureReason=" + failureReason);
            mHandleTransportStatus.setAndSendStatus("Unable to connect: " + failureReason);
            return;
        }

        if (isConnected()) {
            Log.e("tryToConnect","tryToConnect: already connected, nothing to do here");
            return;
        }
        if (!ConnectivityManagerUtil.hasDataConnection(mContext)) {
            Log.e("tryToConnect","tryToConnect: no data connection available");
            newState(State.WaitingForNetwork);
            return;
        }

        Log.e("tryToConnect","tryToConnect: Changing state to 'Connecting'");
        newState(State.Connecting);

        XMPPTCPConnection connection;
        boolean newConnection = false;

        // We need to use an Application context instance here, because some Contexts may not work.
        XMPPTCPConnectionConfiguration latestConnectionConfiguration;
        try {
            latestConnectionConfiguration = mSettings.getConnectionConfiguration(mContext);
        } catch (XmppStringprepException e) {
            Log.e("tryToConnect","tryToConnect: getConnectionConfiguration failed. New State: Disconnected", e);
            newState(State.Disconnected, e.getLocalizedMessage());
            return;
        }
        if (mConnection == null || mConnectionConfiguration != latestConnectionConfiguration) {
            mConnectionConfiguration = latestConnectionConfiguration;
            connection = new XMPPTCPConnection(mConnectionConfiguration);

            final Roster roster = Roster.getInstanceFor(connection);

            newConnection = true;
        } else {
            connection = mConnection;
        }

        // Stream Management (XEP-198)
        connection.setUseStreamManagement(true);
        // Again a value that's hard to get right. Right now, we try it with 5 minutes, as Stream
        // resumption is meant for situations where the network switches, not when the Android
        // system kills and later restarts the service (in which case, SM resumption would be not
        // possible anyways).
        connection.setPreferredResumptionTime(5 * 60); // 5 minutes

        // Disable bundle and defer so that the connection and login sequence is fast. :)
        XMPPBundleAndDefer.disableBundleAndDefer();

        Log.d("tryToConnect","tryToConnect: Calling connect() on XMPP connection");
        try {
            connection.connect();
        } catch (Exception e) {
            XMPPBundleAndDefer.enableBundleAndDefer();
            Log.e("tryToConnect","tryToConnect: Exception from connect()", e);
            if (e instanceof SmackException.ConnectionException) {
                SmackException.ConnectionException ce = (SmackException.ConnectionException) e;
                String error = "The following host's failed to connect to:";
                for (HostAddress ha : ce.getFailedAddresses())
                    error += " " + ha;
                Log.d("tryToConnect","tryToConnect: " + error);
            }
            scheduleReconnect(e.getLocalizedMessage());
            return;
        }

        Log.d("tryToConnect","tryToConnect: connect() returned without exception, calling login()");
        try {
            connection.login();
        } catch (SmackException.NoResponseException e) {
            Log.d("tryToConnect","tryToConnect: NoResponseException. Scheduling reconnect.");
            scheduleReconnect("Not response while loggin in.");
            return;
        } catch (Exception e) {
            Log.e("tryToConnect","tryToConnect: login failed. New State: Disconnected", e);
            newState(State.Disconnected, e.getLocalizedMessage());
            return;
        } finally {
            XMPPBundleAndDefer.enableBundleAndDefer();
        }
        // Login Successful

        mConnection = connection;

        if (newConnection) {
            synchronized (mStateChangeListeners) {
                for (StateChangeListener l : mStateChangeListeners) {
                    l.newConnection(mConnection);
                }
            }
        }

        mReconnectionAttemptCount = 0;
        newState(State.Connected);

        Log.d("tryToConnect","tryToConnect: successfully connected \\o/");

    }

    private synchronized void disconnectConnection(boolean instant) {
        if (mConnection != null) {
            if (mConnection.isConnected()) {
                newState(State.Disconnecting);
                Log.d("tryToConnect","disconnectConnection: disconnect start. instant=" + instant);
                if (instant) {
                    mConnection.instantShutdown();
                } else {
                    mConnection.disconnect();
                }
                Log.d("tryToConnect","disconnectConnection: disconnect stop");
            }
            newState(State.Disconnected);
        }
    }

    public boolean shouldUseXmppConnection() {
        if (mConnection == null) {
            return false;
        }
        if (!mConnection.isAuthenticated()) {
            return false;
        }
        // If it is not connected and not resumable, return false
        return !(!mConnection.isConnected() && !mConnection.isDisconnectedButSmResumptionPossible());
    }
}
