package com.supersoft.internusa.helper.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Centaury on 21/04/2018.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "ifresh";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_PREF_UUID = "UuidPref";
    private static final String PHONE_NUM = "phone_number";
    private static final String MY_NAME = "namaku";
    private static final String MY_ADDRESS = "alamatku";
    private static final String FCMID = "fcmidku";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setUuid(String uid) {
        editor.putString(IS_PREF_UUID, uid);
        editor.commit();
    }

    public String getUuid() {
        return pref.getString(IS_PREF_UUID, "");
    }

    public void setPhoneNum(String uid) {
        editor.putString(PHONE_NUM, uid);
        editor.commit();
    }

    public String getPhoneNum() {
        return pref.getString(PHONE_NUM, "");
    }

    public void setMyName(String uid) {
        editor.putString(MY_NAME, uid);
        editor.commit();
    }

    public String getMyName() {
        return pref.getString(MY_NAME, "");
    }

    public void setMyAddress(String uid) {
        editor.putString(MY_ADDRESS, uid);
        editor.commit();
    }

    public String getMyAddress() {
        return pref.getString(MY_ADDRESS, "");
    }

    public void setMyFCMID(String fcmid) {
        editor.putString(FCMID, fcmid);
        editor.commit();
    }

    public String getFCMID() {
        return pref.getString(FCMID, "");
    }
}
