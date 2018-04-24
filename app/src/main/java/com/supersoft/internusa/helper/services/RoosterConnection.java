package com.supersoft.internusa.helper.services;

/**
 * Created by itclub21 on 11/22/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.supersoft.internusa.R;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.Session;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.debugger.JulDebugger;
import org.jivesoftware.smack.debugger.SmackDebugger;
import org.jivesoftware.smack.debugger.SmackDebuggerFactory;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.StringTransformer;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.Normalizer;

public class RoosterConnection implements ConnectionListener {

    private static final String TAG = "RoosterConnection";

    private  final Context mApplicationContext;
    private  final String mUsername;
    private  final String mPassword;
    private  final String mServiceName;
    private XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;//Receives messages from the ui thread.
    Session _session;

    static
    {
        ServiceDiscoveryManager.setDefaultIdentity(new DiscoverInfo.Identity("client","android","bot"));
        // TODO This is not really needed, but for some reason the static initializer block of
        // LastActivityManager is not run. This could be a problem caused by aSmack together with
        // dalvik, as the initializer is run on Smack's test cases.
        LastActivityManager.setEnabledPerDefault(true);
        // Some network types, especially GPRS or EDGE is rural areas have a very slow response
        // time. Smack's default packet reply timeout of 5 seconds is way to low for such networks,
        // so we increase it to 2 minutes.
        // This value must also be greater then the highest returned bundle and defer value.
        SmackConfiguration.setDefaultReplyTimeout(2 * 60 * 1000);

        // @formatter:off
        SmackConfiguration.addDisabledSmackClasses(
                "org.jivesoftware.smack.ReconnectionManager"
                , "org.jivesoftware.smack.util.dns.javax"
                , "org.jivesoftware.smack.util.dns.dnsjava"
                , "org.jivesoftware.smack.sasl.javax"
                , "org.jivesoftware.smack.legacy"
                , "org.jivesoftware.smack.java7"
                , "org.jivesoftware.smackx.eme"
                , "org.jivesoftware.smackx.gcm"
                , "org.jivesoftware.smackx.httpfileupload"
                , "org.jivesoftware.smackx.hoxt"
                , "org.jivesoftware.smackx.iot"
                , "org.jivesoftware.smackx.json"
                , "org.jivesoftware.smackx.muc"
                , "org.jivesoftware.smackx.omemo"
                , "org.jivesoftware.smackx.xdata"
                , "org.jivesoftware.smackx.xdatalayout"
                , "org.jivesoftware.smackx.xdatavalidation"
        );
        // @formatter:on

        DNSUtil.setIdnaTransformer(new StringTransformer() {
            @Override
            public String transform(String string) {
                return java.net.IDN.toASCII(string);
            }
        });
        SASLMechanism.setSaslPrepTransformer(new StringTransformer() {
            @Override
            public String transform(String string) {
                return Normalizer.normalize(string, Normalizer.Form.NFKC);
            }
        });
        SmackConfiguration.setDebuggerFactory(new SmackDebuggerFactory() {
            @Override
            public SmackDebugger create(XMPPConnection connection, Writer writer, Reader reader) {
                return new JulDebugger(connection, writer, reader);
            }
        });
    }

    public enum ConnectionState
    {
        CONNECTED ,AUTHENTICATED, CONNECTING ,DISCONNECTING ,DISCONNECTED, ISWAITING
    }


    public RoosterConnection( Context context)
    {
        Log.e(TAG,"RoosterConnection Constructor called.");
        mApplicationContext = context.getApplicationContext();
        _session = new Session(mApplicationContext);
        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_jid",null);
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_password",null);

        if( jid != null)
        {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        }else
        {
            mUsername ="";
            mServiceName="";
        }
    }


    public void connect() throws IOException,XMPPException,SmackException
    {
        Log.e(TAG, "Connecting to server " + mServiceName);

        SASLAuthentication.unregisterSASLMechanism("KERBEROS_V4");
        SASLAuthentication.unregisterSASLMechanism("GSSAPI");
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");

        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(mServiceName)
                .setHost(Constant.XMPP_HOST)
                .setPort(Constant.XMPP_PORT)
                .setResource("AR"+mApplicationContext.getResources().getString(R.string.CONF_MITRAID))
                .setDebuggerEnabled(true)
                .setSendPresence(true) //If the presence is false or not set, then all your incoming messages will be saved in your Offline message storage and the interface will not be triggered
                .setKeystoreType(null) //This line seems to get rid of the problem
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setCompressionEnabled(true).build();

        //if (mMemTrust == null) mMemTrust = new MemorizingTrustManager(mContext);

        Log.e(TAG, "Username : "+mUsername);
        Log.e(TAG, "Password : "+mPassword);
        Log.e(TAG, "Server : "+mServiceName);


        //Set up the ui thread broadcast message receiver.
        setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(conf);
        mConnection.addConnectionListener(this);
        mConnection.addAsyncStanzaListener(new MsgStanzaListener(mApplicationContext), new StanzaTypeFilter(Message.class));

        //login();



    }




    private void setupUiThreadBroadCastMessageReceiver()
    {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check if the Intents purpose is to send the message.
                String action = intent.getAction();
                if( action.equals(ConnectionService.SEND_MESSAGE))
                {
                    //Send the message.
                    sendMessage(intent.getStringExtra(ConnectionService.BUNDLE_MESSAGE_BODY), intent.getStringExtra(ConnectionService.BUNDLE_TO));
                }
                else if (action.equals(ConnectionService.SEND_MESSAGE_CHATROOM))
                {
                    Log.e(TAG,"uiThreadMessageReceiver");
                    try {
                        sendMessageGroup(intent.getStringExtra(ConnectionService.BUNDLE_TO), intent.getStringExtra(ConnectionService.BUNDLE_MESSAGE_BODY));
                    }catch (Exception e)
                    {
                        e.getStackTrace();
                    }

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.SEND_MESSAGE);
        filter.addAction(ConnectionService.SEND_MESSAGE_CHATROOM);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver,filter);

    }

    public void sendMessage ( String body ,String toJid)
    {
        Log.e(TAG,"Sending message to :"+ toJid);

        EntityBareJid jid = null;

        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);

        try {
            jid = JidCreate.entityBareFrom(toJid);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Chat chat = chatManager.chatWith(jid);
        try {
            Message message = new Message(jid, Message.Type.chat);
            message.setBody(body);
            chat.send(message);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public void sendMessageNormalGroup(final String body, String chatRoomJid)
    {
        Log.e(TAG,"Sending message to groupname :"+ chatRoomJid);
        if(mConnection.isConnected())
        {

            EntityBareJid jid = null;
            try {
                jid = JidCreate.entityBareFrom(chatRoomJid);
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }

            try {
                Message message = new Message(jid, Message.Type.normal);
                message.setBody(body);
                mConnection.sendPacket(message);

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public void sendMessageGroup(final String body, String chatRoomJid) throws Exception
    {
        Log.e(TAG,"Sending message to groupname :"+ chatRoomJid);

        if(mConnection.isConnected())
        {

            MultiUserChatManager mucMgr = MultiUserChatManager.getInstanceFor(mConnection);

            EntityBareJid jid = null;
            try
            {
                try {
                    jid = JidCreate.entityBareFrom(chatRoomJid+"@broadcast." + mConnection.getServiceName());
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                MultiUserChat muc = mucMgr.getMultiUserChat(jid);
                if(!muc.isJoined())
                {

                    Resourcepart resourcepart = Resourcepart.from(mUsername);
                    muc.join(resourcepart);
                    Log.e("join", "isJoinied berhasil");
                }

                Form form = muc.getConfigurationForm().createAnswerForm();
                form.setAnswer("muc#roomconfig_publicroom", true);
                form.setAnswer("muc#roomconfig_roomname", "room786");
                //form.setAnswer("muc#roomconfig_roomowners",owners);
                form.setAnswer("muc#roomconfig_persistentroom", true);
                muc.sendConfigurationForm(form);


                Message ms = new Message();
                ms.setBody(body);
                ms.setType(Message.Type.groupchat);
                ms.setTo(muc.getRoom());
                //muc.sendMessage(ms);
                mConnection.sendPacket(ms);
            }catch (SmackException ex) {
                Log.e("err", ex.getMessage());
                throw new Exception("422", ex);
            }


        }

    }




    public void disconnect()
    {
        Log.e(TAG,"Disconnecting from serser "+ mServiceName);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
        prefs.edit().putBoolean("xmpp_logged_in",false).commit();
        if (mConnection != null)
        {
            mConnection.disconnect();
        }

        mConnection = null;
        // Unregister the message broadcast receiver.
        if( uiThreadMessageReceiver != null)
        {
            mApplicationContext.unregisterReceiver(uiThreadMessageReceiver);
            uiThreadMessageReceiver = null;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Constant.debug(TAG,"delay 3sec to try login xmpp");
                StartApp.startLoginXmpp();
            }
        }, 1000);

    }


    public void login()
    {
        try {
            ConnectionService.sConnectionState= ConnectionState.ISWAITING;
            mConnection.connect();
            mConnection.login(mUsername,mPassword);
            Log.e(TAG, " login() Called ");
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(XMPPConnection connection) {
        ConnectionService.sConnectionState= ConnectionState.CONNECTED;
        EventBus.getDefault().post("AUTHENTICATED");
        if(ConnectionService.sConnectionState != ConnectionState.AUTHENTICATED)
        {
            login();
        }
        Log.e(TAG,"Connected Successfully");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        ConnectionService.sConnectionState= ConnectionState.AUTHENTICATED;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"Authenticated Successfully");
        showContactListActivityWhenAuthenticated();
        sendBroadcastWhenqueueMessage();

        if(mConnection.isAuthenticated())
        {
            //Server.getInstanceFor(connection).setEnabled(true);

            PingManager pingManager = PingManager.getInstanceFor(connection);
            pingManager.setPingInterval(300);

            try
            {
                pingManager.pingMyServer();
                pingManager.pingMyServer(true,10);
                pingManager.pingServerIfNecessary();

                pingManager.registerPingFailedListener(new PingFailedListener() {
                    @Override
                    public void pingFailed() {
                        Log.e("Ping","pingFailed");
                        disconnect();
                        //StartApp.startLoginXmpp();
                    }
                });
            }catch (InterruptedException | SmackException.NotConnectedException  e)
            {

            }
        }
    }


    @Override
    public void connectionClosed() {
        ConnectionService.sConnectionState= ConnectionState.DISCONNECTED;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        ConnectionService.sConnectionState= ConnectionState.DISCONNECTED;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        ConnectionService.sConnectionState = ConnectionState.CONNECTING;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        ConnectionService.sConnectionState = ConnectionState.CONNECTED;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        ConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        EventBus.getDefault().post("AUTHENTICATED");
        Log.e(TAG,"ReconnectionFailed()");

    }

    private void showContactListActivityWhenAuthenticated()
    {
        Intent i = new Intent(ConnectionService.UI_AUTHENTICATED);
        i.setPackage(mApplicationContext.getPackageName());
        mApplicationContext.sendBroadcast(i);
        Log.e(TAG,"Sent the broadcast that we are authenticated");
    }

    private void sendBroadcastWhenqueueMessage()
    {
        try
        {
            if(StartApp.msgBroadcast.size() > 0)
            {
                Intent intent = new Intent(mApplicationContext, RestartServiceReceiver.class);
                intent.setAction(ConnectionService.SEND_MESSAGE_CHATROOM);
                intent.setPackage(mApplicationContext.getPackageName());
                mApplicationContext.sendBroadcast(intent);
            }
        }
        catch (NullPointerException ex)
        {

        }

    }


    private boolean shouldUseXmppConnection() {
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
