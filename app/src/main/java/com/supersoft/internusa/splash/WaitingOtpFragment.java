package com.supersoft.internusa.splash;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

/**
 * Created by Centaury on 21/04/2018.
 */
public class WaitingOtpFragment extends Fragment implements TextWatcher {

    final String INTENT_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
    private Toolbar toolbar;
    EditText txtAgenid;
    Button btnSubmit;
    LinearLayout linearResend;
    RelativeLayout linearCountdown;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBarCircle;
    static DBHelper _db;
    static Session _session;
    private AlertDialog progressDialog;
    private long timeCountInMilliSeconds = 1 * 60000;
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    public static WaitingOtpFragment newInstnace(DBHelper db, Session session) {
        WaitingOtpFragment fragment = new WaitingOtpFragment();
        _db = db;
        _session = session;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.waiting_otp, container, false);

        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        txtAgenid = rootView.findViewById(R.id.txtAgenid);
        linearResend = rootView.findViewById(R.id.linearResend);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        progressBarCircle = rootView.findViewById(R.id.progressBarCircle);
        textViewTime = rootView.findViewById(R.id.textViewTime);
        linearCountdown = rootView.findViewById(R.id.linearCountdown);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.Is_Valid_Sign_Number_Validation(8, 14, txtAgenid)) {
                    EventBus.getDefault().post(new MsgEvent(Constant.EVENT_OTP, txtAgenid.getText().toString()));
                }
            }
        });
        linearResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAgenid.setText("");
                String nohp = _db.getFieldProfil("hp", false);
                startCountDownTimer();
                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI, nohp, StartApp.PREFIK_NUMBER));
            }
        });

        initToolbar(rootView);

        if (isReadStatePermissionRequired()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.RECEIVE_SMS}, 2222);
            }
        } else {
            getActivity().registerReceiver(IncomingSms, new IntentFilter(INTENT_RECEIVE_SMS));
        }

        startCountDownTimer();
        return rootView;
    }

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, null));
            }
        });


    }

    private void startCountDownTimer() {
        timeCountInMilliSeconds = 2 * 60 * 1000;
        linearCountdown.setVisibility(View.VISIBLE);
        linearResend.setVisibility(View.GONE);
        setProgressBarValues();
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                linearCountdown.setVisibility(View.GONE);
                linearResend.setVisibility(View.VISIBLE);
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    public boolean isReadStatePermissionRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Constant.hasPermissionInManifest(getContext(), "android.permission.RECEIVE_SMS") &&
                    getActivity().checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 2222) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getActivity().registerReceiver(IncomingSms, new IntentFilter(INTENT_RECEIVE_SMS));
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions READ SMS are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Constant.Is_Valid_Sign_Number_Validation(8, 14, txtAgenid);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("SmsReceiver", "register receiver");
        getActivity().registerReceiver(IncomingSms, new IntentFilter(INTENT_RECEIVE_SMS));
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.e("SmsReceiver", "pause receiver");
        getActivity().unregisterReceiver(IncomingSms);
    }

    private final BroadcastReceiver IncomingSms = new BroadcastReceiver() {
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();

        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        //Log.e("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                        //Yth. PU0001 Kode Aktivasi Anda: 296917 Bersifat RAHASIA. Hati2 Penipuan. Jangan Pernah memberi Kode RAHASIA Apapun kepada Orang Lain !!

                        // Show Alert

                        final String key = getKeySms(message.toLowerCase(), "aktivasi anda : ", " . untuk");
                        //Log.e("get KEY","FOUND : " + key.trim());
                        if (key.length() == 6) {

                            txtAgenid.setText(key.trim());
                            txtAgenid.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (Constant.Is_Valid_Sign_Number_Validation(6, 6, txtAgenid)) {
                                        EventBus.getDefault().post(new MsgEvent(Constant.EVENT_OTP, txtAgenid.getText().toString()));
                                    }
                                }
                            }, 1000);

                        }

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

            }
        }
    };


    public static final String getKeySms(String msg, String awal, String akhir) {
        String result = "";
        try {
            if (msg.length() > 10) {
                int pa = msg.indexOf(awal);
                if (pa > 0) {
                    //$pr = (!empty($akir)) ? strpos($msg, $akir, $pa + strlen($awal) + 1) : strlen($msg);
                    int pr = (akhir.length() > 0) ? msg.indexOf(akhir, (pa + awal.length())) : msg.length();
                    if (pr == 0) pr = msg.length();
                    result = msg.substring(pa + awal.length(), pr);
                }
            }
        } catch (IndexOutOfBoundsException e) {

        } catch (Exception e) {

        }

        return result;
    }
}
