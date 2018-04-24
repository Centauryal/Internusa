package com.supersoft.internusa.helper.services;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.supersoft.internusa.R;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojoProfilUsaha;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 11/13/2017.
 */

public class TimelineService extends Service {
    DBHelper _db;
    ProfilDB profil;
    Session _session;
    int prevLastId = 0;
    @Override
    public void onCreate() {
        _db = new DBHelper(getApplicationContext());
        profil = _db.getProfilDb();
        _session = new Session(getApplicationContext());
        startTimerCheckUpdate();
        //Log.e("serviceTimeline", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("serviceTimeline", "Starting prev = "+prevLastId+" lastId = "+StartApp.LastID);
        startTimerCheckUpdate();

        if(StartApp.LastID > 0)
        {
            checkTimeline();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }

    public void onDestroy() {

    }


    public void checkTimeline()
    {
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = profil.hp;
        inf.deviceid = Constant.getUUID(getApplicationContext());
        inf.mitraid = getApplicationContext().getResources().getString(R.string.CONF_MITRAID);
        inf.limit = 1;
        inf.lastid = StartApp.LastID;
        inf.offset = 10;
        prevLastId = StartApp.LastID;
        //Log.e("TimelineService", "Params : " + new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder("get_info");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoProfilUsaha> call = githubUserAPI.getProfilUsahaRekan(String.format(Constant.CONTROLLER_4S,Constant.CONTROLLER_DEV, "get_info", "read",profil.hp),inf);
        call.enqueue(new Callback<PojoProfilUsaha>() {
            @Override
            public void onResponse(Call<PojoProfilUsaha> call, Response<PojoProfilUsaha> response) {
                //Log.e("TimelineService", "found" + new Gson().toJson(response.body()));
                if(!response.isSuccessful()){

                    return;
                }

                PojoProfilUsaha pojos = response.body();
                ArrayList<Row> datas = new ArrayList<Row>(pojos.getRows());

                if(datas.size() > 0)
                {
                    StartApp.FoundNewTimeline = true;
                    StartApp.TotalNewTimeline = datas.size();
                    StartApp.LastID = pojos.getLastId();

                    for (int o=0; o < datas.size(); o++) {
                        Log.e("TimelineService", "data ke " + o);
                        _db.insertTimeline(datas.get(o));
                    }

                    if(!StartApp.ONCLICK_BUTTON_TIMELINE)
                    {
                        MsgEvent event = new MsgEvent(prevLastId, ""+datas.size(), "", "newTimeline");
                        EventBus.getDefault().post(event);
                    }
                    StartApp.ONCLICK_BUTTON_TIMELINE = false;

                }
            }

            @Override
            public void onFailure(Call<PojoProfilUsaha> call, Throwable t) {
                Log.e("TimelineService", "error " + t.getMessage());
            }
        });
    }

    public void startTimerCheckUpdate()
    {
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 5000,restartPendingIntent);

    }
}
