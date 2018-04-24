package com.supersoft.internusa.helper.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.tasks.RuntimeExecutionException;

import com.supersoft.internusa.R;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.services.xmpp.MAXTransportService;
import com.supersoft.internusa.helper.services.xmpp.XMPPService;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 10/11/2017.
 */

public class ConnectionService extends MAXTransportService {

    private static boolean sIsRunning = false;
    public static final String START_LOGIN_XMPP = "com.supersoft.internusa.rooster.startLogin";
    public static final String UI_AUTHENTICATED = "com.supersoft.internusa.rooster.uiauthenticated";
    public static final String ACTION_CONNECTION_CLOSED_ON_ERROR = "com.supersoft.internusa.CONNECTION_CLOSED_ON_ERROR";
    public static final String SEND_MESSAGE = "com.supersoft.internusa.rooster.sendmessage";
    public static final String SEND_MESSAGE_CHATROOM = "com.supersoft.internusa.rooster.sendmessage.chatrooms";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";

    public static final String NEW_MESSAGE = "com.supersoft.internusa.rooster.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";


    public static final String ACTION_IDLE_PING = "idle_ping";
    public static final String ACTION_GCM_TOKEN_REFRESH = "gcm_token_refresh";
    private String LOG_TAG = "ConnectionService";
    public static final int NOTIFICATION_CODE = 1;
    private WakeLock wakeLock;
    private PowerManager pm;
    private int unreadCount = -1;
    private RestartServiceReceiver mEventReceiver = new RestartServiceReceiver();
    public DBHelper databaseBackend;
    private PrefManager prefManager;
    private Session _session;
    private ConnectivityManager mManager;

    public static RoosterConnection.ConnectionState sConnectionState;

    private XMPPService mXMPPService;

    public ConnectionService() {
        super("XMPP", ConnectionService.class);
    }

    public static boolean isRunning() {
        return sIsRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "service created");
        this.databaseBackend = DBHelper.getInstance(getApplicationContext());
        this.prefManager = new PrefManager(getApplicationContext());
        this._session = new Session(getApplicationContext());
        mManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        this.pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ConnectionService");

        updateUnreadCountBadge();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scheduleNextIdlePing();
            runForeGround();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(this.mEventReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        if (mXMPPService == null) mXMPPService = XMPPService.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String action = intent == null ? null : intent.getAction();

        if(action != null)
        {
            Log.e("ACTION_COMMAND", action);
            switch (action)
            {
                case ACTION_IDLE_PING:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        scheduleNextIdlePing();
                        if (checkNetworkStateChanged()) updateUnreadCountBadge();
                    }
                    Log.e("checkkoneksi", "mconn : "+mXMPPService.shouldUseXmppConnection() + " state " + mXMPPService.getCurrentState().name());
                    EventBus.getDefault().post((!mXMPPService.shouldUseXmppConnection()) ? Constant.XMPP_NOT_CONNECT : Constant.XMPP_CONNECTED);
                    if(!mXMPPService.shouldUseXmppConnection())
                    {
                        sendBroadcast(new Intent(START_LOGIN_XMPP));
                    }
                    break;
                case "com.supersoft.internusa.StartApp":

                    break;
                case "com.supersoft.internusa.StopForeground":
                    stopForeground(true);
                    stopSelf();
                    break;
                case "com.supersoft.internusa.StartForeground":
                    runForeGround();
                    break;
                case START_LOGIN_XMPP:
                    Log.e("sStatce", " action " + mXMPPService.getCurrentState().name());
                    if (hasMessage(Constant.ACTION_NETWORK_CONNECTED.hashCode())) {
                        Log.e("ConnService","Not handling NETWORK_CONNECTED because another intent of the same type is in the queue");
                        break;
                    }

                    new Thread(new Runnable() {
                        public void run()
                        {
                            mXMPPService.connect();
                        }
                    }).start();
                    break;
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    Log.e("conn", "Connectivity change");

                case Constant.ACTION_NETWORK_CONNECTED:
                    if (hasMessage(Constant.ACTION_NETWORK_CONNECTED.hashCode())) {
                        Log.e("ConnService","Not handling NETWORK_CONNECTED because another intent of the same type is in the queue");
                        break;
                    }
                    new Thread(new Runnable() {
                        public void run()
                        {
                            mXMPPService.connect();
                        }
                    }).start();
                    break;
                case Constant.ACTION_NETWORK_DISCONNECTED:
                    if (hasMessage(Constant.ACTION_NETWORK_DISCONNECTED.hashCode())) {
                        Log.e("ConnService","Not handling NETWORK_DISCONNECTED because another intent of the same type is in the queue");
                        break;
                    }
                    new Thread(new Runnable() {
                        public void run()
                        {
                            mXMPPService.networkDisconnected();
                        }
                    }).start();

                    break;
                case ACTION_CONNECTION_CLOSED_ON_ERROR:
                    if (hasMessage(action.hashCode())) {
                        Log.e("connservice","Not handling " + action + " because another intent of the same type is in the queue");
                        break;
                    }

                    if (mXMPPService.fastPingServer()) {
                        Log.e("connservice","Not handling " + action + " because connection is (still/again) alive");
                        break;
                    }
                    mXMPPService.instantDisconnect();
                    if (action.equals(ACTION_CONNECTION_CLOSED_ON_ERROR)) {
                        // If the connection was closed on error, then try to reconnect now. In case
                        // NETWORK_TYPE_CHANGED was send, then we will receive a NETWORK_CONNECTED intent
                        // also, which would trigger the reconnect.
                        new Thread(new Runnable() {
                            public void run()
                            {
                                mXMPPService.connect();
                            }
                        }).start();
                    }
                    break;

                case NEW_MESSAGE:

                    String jid_from = intent.getStringExtra(BUNDLE_FROM_JID);
                    String body = intent.getStringExtra(BUNDLE_MESSAGE_BODY);
                    Log.d("NEW MSS","Got a message from jid :"+jid_from +" body " + body);

                    IntentFilter filter = new IntentFilter(NEW_MESSAGE);
                    final RestartServiceReceiver mReceiver = new RestartServiceReceiver();
                    registerReceiver(mReceiver,filter);
                    break;
                case SEND_MESSAGE_CHATROOM:
                    Log.d("RoosterConnection","SEND_MESSAGE_CHATROOM");
                    mXMPPService.sendKomentar(StartApp.msgBroadcast);
                    StartApp.msgBroadcast = new ArrayList<>();

                    break;
                case Intent.ACTION_SCREEN_OFF:
                    _session.setGroupActivityIsOpen(false);
                    break;
            }
        }

        synchronized (this)
        {
            this.wakeLock.acquire();
            if(wakeLock.isHeld())
            {
                try
                {
                    wakeLock.release();
                }catch (final RuntimeExecutionException e){}
            }
        }


        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        Log.e("ACTION_onHandleIntent","onHandleIntent: " + action);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startTimerOnTaskRemoved();
        _session.setKomentarActivityIsOpen(false);
        _session.setLastInfoId("");

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("com.supersoft.internusa.StartApp"));
    }

    public void startTimerOnTaskRemoved()
    {
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,restartPendingIntent);

    }


    private boolean checkNetworkStateChanged() {
        boolean prev = StartApp.isNetworkAvailable;
        NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
        StartApp.isNetworkAvailable = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != StartApp.isNetworkAvailable;
    }

    private void runForeGround()
    {
        String activated = this.databaseBackend.getFieldProfil("activated", true);
        if(this._session.getOptionServiceBg() && activated.equals("1"))
        {
            Notification notification = new Notification();
            startForeground(42, notification);
        }
        else
        {
            stopForeground(true);
        }
    }

    public synchronized void updateUnreadCountBadge() {
        unreadCount = this.databaseBackend.getLastInfoId();
        String activated = this.databaseBackend.getFieldProfil("activated", true);
        if(!activated.equals("1")) return;
        RetrofitBuilder builder = new RetrofitBuilder("updateLastidInfo");
        RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getLastIdInfo"

        Call<Integer> repos = service.updateLastidInfo(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "getLastIdInfo"), this.databaseBackend.getLastInfoId(), getApplicationContext().getResources().getString(R.string.CONF_MITRAID));
        repos.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                //Log.d(LOG_TAG, "res " + response.body());
                if(!response.isSuccessful()) return;
                int count = response.body();
                int tmpCount = count - unreadCount;
                //Log.d(LOG_TAG, "update unread count to " + count);
                if (unreadCount != count) {

                    if (count > 0) {
                        ShortcutBadger.applyCount(getApplicationContext(), tmpCount);
                    } else {
                        ShortcutBadger.removeCount(getApplicationContext());
                    }
                    unreadCount = count;
                    databaseBackend.updateLastInfoId(String.valueOf(count));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                //Log.d(LOG_TAG, "err " + t.getMessage());
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void scheduleNextIdlePing() {
        Log.d(LOG_TAG,"schedule next idle ping");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, RestartServiceReceiver.class);
        intent.setAction(ACTION_IDLE_PING);
        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()+(60 * 1000), //Config.IDLE_PING_INTERVAL
                PendingIntent.getBroadcast(this,0,intent,0)
        );
    }

}
