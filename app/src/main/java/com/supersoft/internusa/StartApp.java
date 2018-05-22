package com.supersoft.internusa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.messaging.RemoteMessage;
import com.supersoft.internusa.helper.fcm.FCMListener;
import com.supersoft.internusa.helper.fcm.SaveFCMIdService;
import com.supersoft.internusa.helper.services.AndroidUtilities;
import com.supersoft.internusa.helper.services.ConnectionService;
import com.supersoft.internusa.helper.services.ForegroundDetector;
import com.supersoft.internusa.helper.services.NotificationsService;
import com.supersoft.internusa.helper.services.RestartServiceReceiver;
import com.supersoft.internusa.helper.services.ScreenReceiver;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.MessageBroadcast;
import com.supersoft.internusa.model.ProfilDB;

import java.util.ArrayList;

/**
 * Created by Centaury on 19/04/2018.
 */
public class StartApp extends Application implements Application.ActivityLifecycleCallbacks, FCMListener {
    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean isNetworkAvailable = false;
    public static volatile int LastID = 0;
    public static volatile boolean FoundNewTimeline = false;
    public static volatile int TotalNewTimeline = 0;
    public static volatile String PREFIK_NUMBER = "";
    public static volatile boolean ONCLICK_BUTTON_TIMELINE = false;
    private PrefManager prefManager;

    private int refs;
    private boolean wasInBackground = true;
    private long enterBackgroundTime = 0;
    public static volatile ArrayList<MessageBroadcast> msgBroadcast = new ArrayList<>();
    //private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }

        applicationInited = true;

        try {
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            final BroadcastReceiver mReceiver = new ScreenReceiver();
            applicationContext.registerReceiver(mReceiver, filter);


        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PowerManager pm = (PowerManager)StartApp.applicationContext.getSystemService(Context.POWER_SERVICE);
            isScreenOn = pm.isScreenOn();
            Log.e("StartApp","screen state = " + isScreenOn);
        } catch (Exception e) {

        }



        SharedPreferences preferences = StartApp.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
        boolean enablePushConnection = preferences.getBoolean("pushConnection", true);

        StartApp app = (StartApp)StartApp.applicationContext;
        app.initPlayServices();
        Log.e("StartApp","app initied");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        applicationContext = getApplicationContext();
        prefManager = new PrefManager(applicationContext);
        new ForegroundDetector(this);
        applicationHandler = new Handler(applicationContext.getMainLooper());
        startPushService();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++refs == 1) {
            if (System.currentTimeMillis() - enterBackgroundTime < 200) {
                wasInBackground = false;
            }
            Log.e("StartApp", "switch to foreground");
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //Log.e("restars","service di restart");
        //Intent restartService = new Intent(getApplicationContext(), FCMListenerService.class);
        //PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1,restartService,PendingIntent.FLAG_ONE_SHOT);
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME,5000,pendingIntent);
    }

    public static void startLoginXmpp()
    {

        DBHelper databaseBackend = new DBHelper(applicationContext);
        Session _session = new Session(applicationContext);
        final PrefManager pref = new PrefManager(applicationContext);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        //Start the service
        String activated = databaseBackend.getFieldProfil("activated", true);
        Log.e("StartApp","saveCredentialsAndLogin() called. activated " + activated +" memid " + _session.getMemid());
        if(activated.equals("1") && (_session.getMemid() > 0))
        {
            /*ProfilDB profil = new DBHelper(applicationContext).getProfilDb();
            String jid = _session.getMemid() + "-" + applicationContext.getResources().getString(R.string.CONF_MITRAID).toLowerCase() + "@"+Constant.XMPP_HOST;

            boolean regsitered = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("xmpp_registered",false);
            if(!regsitered) Constant.sendFcmToServer(applicationContext, "");
            else {
                prefs.edit()
                        .putString("xmpp_jid", jid.toLowerCase())
                        .putString("xmpp_password", "123456")
                        .putBoolean("xmpp_logged_in", true)
                        .commit();*/


                Intent intent = new Intent(applicationContext, RestartServiceReceiver.class);
                intent.setAction(ConnectionService.START_LOGIN_XMPP);
                applicationContext.sendBroadcast(intent);

            //}
        }
        /*else if(activated.equals("1") && (_session.getMemid() <= 0))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Constant.sendFcmToServer(applicationContext, pref.getFCMID());
                }
            }, 1000);

        }*/
    }


    public static void startPushService() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);

        if (preferences.getBoolean("pushService", true)) {
            applicationContext.startService(new Intent(applicationContext, ConnectionService.class));
        } else {
            stopPushService();
        }
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        applicationContext.stopService(new Intent(applicationContext, ConnectionService.class));

        PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
        AlarmManager alarm = (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (checkPlayServices()) {
                    Intent intent = new Intent(applicationContext, SaveFCMIdService.class);
                    startService(intent);
                } else {
                    Log.d("StartApp","No valid Google Play Services APK found.");
                }
            }
        }, 1000);
    }



    private boolean checkPlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            return resultCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            Log.e("checkPlayServices",e.getMessage());
        }
        return true;

        /*if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("tmessages", "This device is not supported.");
            }
            return false;
        }
        return true;*/
    }

    @Override
    public void onDeviceRegistered(String deviceToken) {
        prefManager.setMyFCMID(deviceToken);
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {

    }

    @Override
    public void onPlayServiceError() {

    }
}
