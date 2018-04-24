package com.supersoft.internusa.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.supersoft.internusa.Dashboard;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.fcm.FCMListener;
import com.supersoft.internusa.helper.fcm.FCMManager;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojoAktifasi;
import com.supersoft.internusa.model.PojoDefault;
import com.supersoft.internusa.model.Row;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Centaury on 21/04/2018.
 */
public class SplashMain extends AppCompatActivity implements FCMListener {

    private Session _sess;
    private AlertDialog progressDialog;
    private DBHelper db;
    FragmentManager fm;
    android.support.v4.app.FragmentTransaction transaction;
    private PrefManager prefManager;
    TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            //launchHomeScreen();
            //finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.splash_main);
        ShortcutBadger.removeCount(getApplicationContext());
        //changeStatusBarColor();

        _sess = new Session(this);
        db = new DBHelper(SplashMain.this);

        progressDialog = new SpotsDialog(this, R.style.Custom);
        getPermisionUid();

        _sess.setKomentarActivityIsOpen(false);
        _sess.setLastInfoId("");

        //startService(new Intent(this, ConnectionService.class));
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void replaceFragment(final int position) {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        //Log.e("replaceFrag", "pos " + position);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                transaction.replace(R.id.pager, showFragment(position));
                fm.popBackStack();
                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.commit();
            }
        });
    }

    private void replaceFragment(final int position, final Fragment wFrag) {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                transaction.replace(R.id.pager, wFrag);
                fm.popBackStack();
                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.commit();
            }
        });
    }


    private Fragment showFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case Constant.EVENT_SPLASH:
                fragment = SplashFragment.newInstnace(db, _sess);
                break;
            case Constant.EVENT_LOGIN:
                fragment = LoginFragment.newInstnace(db, _sess);
                break;
            case Constant.EVENT_AKTIFASI:
                fragment = AktifasiFragment.newInstnace();
                break;
            case Constant.EVENT_OTP:
                fragment = WaitingOtpFragment.newInstnace(db, _sess);
                break;
            case Constant.EVENT_AKTIFASI_DAFTAR_ONLINE:
                fragment = DaftarOnlineFragment.newInstnace();
                break;
            case Constant.EVENT_FORM_REGISTRASI:
                fragment = FormRegistrasiFragment.newInstnace();
                break;
            case Constant.EVENT_MERGE_MEMBER:
                fragment = MergeMemberFragment.instance();
                break;
            case Constant.EVENT_DETAIL_PROFIL:
                fragment = DetailMemberBaruFragment.instance();
                break;
        }
        return fragment;
    }


    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMessage(MsgEvent evt) {

        //Log.e("Event", " key " + evt.getPosition());
        switch (evt.getPosition()) {
            case Constant.EVENT_AKTIFASI:
                String nohp = evt.getKey();
                if (!nohp.equals("")) {
                    String prefik = evt.getPhonenumber();
                    Log.e("ref", prefik);
                    aktifasiForm(prefik, nohp);
                } else replaceFragment(evt.getPosition());
                break;
            case Constant.EVENT_OTP:
                String otp = evt.getKey();
                Activated(otp);
                break;
            case Constant.EVENT_SPLASH:
                replaceFragment(evt.getPosition());
                break;
            case Constant.EVENT_LOGIN:
                String agenid = evt.getKey();
                String password = evt.getPhonenumber();
                if (!agenid.equals("") && !password.equals("")) doLogin(agenid, password, false);
                else replaceFragment(evt.getPosition());
                break;
            case Constant.EVENT_MERGE_MEMBER:
                if (evt.getKey().equals("merge")) {
                    ModelRequestInfo model = (ModelRequestInfo) evt.getObject();
                    //Log.e("MODEL", new Gson().toJson(model));
                    checkMerger(model);
                } else
                    replaceFragment(evt.getPosition());
                break;
            case Constant.EVENT_DETAIL_PROFIL:
                ModelRequestInfo model = (ModelRequestInfo) evt.getObject();
                Row mRows = (Row) evt.getRowObject();
                replaceFragment(Constant.EVENT_MERGE_MEMBER, DetailMemberBaruFragment.instance(model, mRows, (evt.getKey().equals("benar")) ? "MERGER" : "NEW"));

                break;
            default:
                replaceFragment(evt.getPosition());
                break;
        }
        //mPager.setCurrentItem(evt.getPosition());
    }


    private void aktifasiForm(final String prefik, final String nohp) {
        progressDialog.show();
        Session _session = Constant.getSession(this);
        final ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = prefik + nohp;
        inf.deviceid = _session.getDeviceId();
        inf.mitraid = getResources().getString(R.string.CONF_MITRAID);
        inf.code = prefik;

        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "getactivationkey");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoAktifasi> call = githubUserAPI.getDataAktifasi(inf);
        call.enqueue(new Callback<PojoAktifasi>() {
            @Override
            public void onResponse(Call<PojoAktifasi> call, Response<PojoAktifasi> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    try {
                        Constant.showInfoMessageDialog(SplashMain.this, "ERROR: \n" + response.errorBody().string(), "ERROR");
                    } catch (IOException ex) {

                    }
                    return;
                }
                //Log.e("RESPO", new Gson().toJson(response).toString());
                PojoAktifasi tmpInfo = response.body();
                ArrayList<PojoAktifasi.Response> datas = new ArrayList<PojoAktifasi.Response>(tmpInfo.getResponse());

                if (datas.get(0).getStatus() == 1) {
                    boolean OK = db.addProfil("", "", prefik, inf.hp, "", "0", "", "");
                    // next otp
                    if (OK)
                        replaceFragment(Constant.EVENT_OTP);
                    else
                        Constant.showInfoMessageDialog(SplashMain.this, "Error ketika insert ke DB.", "Failed");
                } else {
                    Constant.showInfoMessageDialog(SplashMain.this, datas.get(0).getMessage(), "Failed");
                }
            }

            @Override
            public void onFailure(Call<PojoAktifasi> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        Constant.showInfoMessageDialog(SplashMain.this, t.getMessage(), "FAILED");
                    }
                });
            }
        });
    }


    private void Activated(String OTP) {
        progressDialog.show();
        Session _session = Constant.getSession(this);
        String hpnumber = db.getFieldProfil("hp", false);
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = hpnumber;
        inf.deviceid = _session.getDeviceId();
        inf.keyactivation = OTP;
        inf.mitraid = getResources().getString(R.string.CONF_MITRAID);
        //Log.e("REQUEST", new Gson().toJson(inf).toString());
        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "activated");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);

        Call<PojoAktifasi> call = githubUserAPI.getDataAktifasi(inf);
        call.enqueue(new Callback<PojoAktifasi>() {
            @Override
            public void onResponse(Call<PojoAktifasi> call, Response<PojoAktifasi> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    try {
                        Constant.showInfoMessageDialog(SplashMain.this, "ERROR: \n" + response.errorBody().string(), "ERROR");
                    } catch (IOException ex) {

                    }
                    return;
                }
                //Log.e("RESPOsms", new Gson().toJson(response).toString());
                PojoAktifasi tmpInfo = response.body();
                final ArrayList<PojoAktifasi.Response> datas = new ArrayList<PojoAktifasi.Response>(tmpInfo.getResponse());

                if (datas.get(0).getStatus() == 1) {
                    boolean ok = db.addProfil("", "", "", "", "", "1", "0", "");

                    if (ok) {
                        StartApp.startLoginXmpp();
                        new AlertDialog.Builder(SplashMain.this)
                                .setTitle("Aktifasi")
                                .setMessage("Selamat!!!!\nAktifasi berhasil, Tekan tombol OK untuk melanjutkan.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (datas.get(0).hp != null && datas.get(0).pin != null) {
                                            if (!datas.get(0).hp.isEmpty() || !datas.get(0).pin.isEmpty())
                                                doLogin(datas.get(0).hp, datas.get(0).pin, true);
                                            else
                                                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));
                                        } else
                                            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));

                                    }
                                })
                                .create().show();
                    } else {
                        Constant.showInfoMessageDialog(SplashMain.this, "Error ketika insert ke DB.", "Failed");
                    }
                } else {
                    Constant.showInfoMessageDialog(SplashMain.this, datas.get(0).getMessage(), "Info");
                }
            }

            @Override
            public void onFailure(Call<PojoAktifasi> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        Constant.showInfoMessageDialog(SplashMain.this, t.getMessage(), "FAILED");
                    }
                });
            }
        });
    }


    //cek merger apa ada

    private void checkMerger(final ModelRequestInfo model) {
        progressDialog.show();
        Session _session = Constant.getSession(this);
        model.mitraid = getResources().getString(R.string.CONF_MITRAID);
        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoDefault> call = githubUserAPI.postPojoDefault(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "checkandmerge"), model);
        call.enqueue(new Callback<PojoDefault>() {
            @Override
            public void onResponse(Call<PojoDefault> call, Response<PojoDefault> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    try {
                        Constant.showInfoMessageDialog(SplashMain.this, "ERROR: \n" + response.errorBody().string(), "ERROR");
                    } catch (IOException ex) {

                    }
                    return;
                }
                PojoDefault respo = response.body();
                ArrayList<Row> row = new ArrayList<Row>(respo.getRow());
                if (row.size() > 0)
                    replaceFragment(Constant.EVENT_MERGE_MEMBER, MergeMemberFragment.instance(model, respo));
                else
                    replaceFragment(Constant.EVENT_MERGE_MEMBER, MergeMemberFragment.instance(model, respo));
            }

            @Override
            public void onFailure(Call<PojoDefault> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        Constant.showInfoMessageDialog(SplashMain.this, t.getMessage(), "FAILED");
                    }
                });

            }
        });
    }

    // Login fungsi

    private void doLogin(final String username, final String pass, final boolean direct) {
        progressDialog.show();
        Session _session = Constant.getSession(this);
        String hpnumber = db.getFieldProfil("hp", false);
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = hpnumber;
        inf.deviceid = _session.getDeviceId();
        inf.mitraid = getResources().getString(R.string.CONF_MITRAID);
        inf.pin = pass;
        inf.agenid = username;

        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "login");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);

        Call<PojoAktifasi> call = githubUserAPI.getDataAktifasi(inf);
        call.enqueue(new Callback<PojoAktifasi>() {
            @Override
            public void onResponse(Call<PojoAktifasi> call, Response<PojoAktifasi> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    try {
                        Constant.showInfoMessageDialog(SplashMain.this, "ERROR: \n" + response.errorBody().string(), "ERROR");
                    } catch (IOException ex) {

                    }
                    return;
                }
                //Log.e("RESPO", new Gson().toJson(response).toString());
                PojoAktifasi pojos = response.body();
                List<PojoAktifasi.Response> datas = new ArrayList<PojoAktifasi.Response>(pojos.getResponse());
                if (datas.get(0).getStatus() == 1) {
                    boolean ok = db.addProfil("", datas.get(0).getAgenid().toUpperCase(), "", username, pass, "1", "1", "");
                    if (ok) {
                        Constant.showActivity(SplashMain.this, Dashboard.class, true);
                    } else {
                        if (direct)
                            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));
                        Constant.showInfoMessageDialog(SplashMain.this, "Error ketika insert ke DB.", "Failed");
                    }

                } else {

                    if (direct) EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));
                    Constant.showInfoMessageDialog(SplashMain.this, datas.get(0).getMessage(), "Info");

                }


            }

            @Override
            public void onFailure(Call<PojoAktifasi> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        if (direct)
                            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));
                        Constant.showInfoMessageDialog(SplashMain.this, t.getMessage(), "FAILED");
                    }
                });
            }
        });


    }

    @Override
    public void onDeviceRegistered(final String deviceToken) {

        Session _session = Constant.getSession(this);
        _session.setDeviceId(prefManager.getUuid());
        prefManager.setMyFCMID(deviceToken);

        String activated = db.getFieldProfil("activated", true);
        String islogin = db.getFieldProfil("islogin", true);

        if(activated.equals("1")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Constant.sendFcmToServer(SplashMain.this, deviceToken);
                }
            });
        }
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {

    }

    @Override
    public void onPlayServiceError() {
        Log.e("device", "error");
    }

    private void getPermisionUid() {
        if (isReadStatePermissionRequired()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 2222);
            } else {
                this.tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                prefManager.setUuid(getUUID());
                FCMManager.getInstance(this).registerListener(this);
                replaceFragment(Constant.EVENT_SPLASH);
            }
        } else {
            this.tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            prefManager.setUuid(getUUID());
            FCMManager.getInstance(this).registerListener(this);
            replaceFragment(Constant.EVENT_SPLASH);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 2222) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                prefManager.setUuid(getUUID());
                FCMManager.getInstance(this).registerListener(this);
                replaceFragment(Constant.EVENT_SPLASH);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();

            }
        }
    }


    public boolean isReadStatePermissionRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Constant.hasPermissionInManifest(this, "android.permission.READ_PHONE_STATE") &&
                    checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private String getUUID() {
        final String tmDevice, tmSerial, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        tmDevice = "" + this.tm.getDeviceId();
        tmSerial = "" + this.tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}
