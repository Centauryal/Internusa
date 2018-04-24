package com.supersoft.internusa.helper.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Centaury on 20/04/2018.
 */
public class Session {
    private static final String FILE_NAME = "config";
    private Context _context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Session(Context paramContext){
        _context = paramContext;

        this.preferences = _context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.editor.commit();
    }

    public void clearSession()
    {
        this.editor.clear();
        this.editor.commit();
    }

    public void createSessionString(String paramString1, String paramString2)
    {
        this.editor.putString(paramString1, paramString2);
        this.editor.commit();
    }

    public void createSessionBoolean(String paramString1, Boolean paramString2)
    {
        this.editor.putBoolean(paramString1, paramString2);
        this.editor.commit();
    }

    public void createSessionInteger(String paramString1, Integer paramString2)
    {
        this.editor.putInt(paramString1, paramString2);
        this.editor.commit();
    }

    public void createSessionLong(String paramsString, Long value){
        this.editor.putLong(paramsString, value);
        this.editor.commit();
    }

    public void setDeviceId(String value)
    {
        createSessionString("DEVICEID", value);
    }

    public String getDeviceId(){
        return this.preferences.getString("DEVICEID","");
    }

    public void setPhoneNum(String value)
    {
        createSessionString("PHONENUM", value);
    }

    public String getPhoneNum(){
        return this.preferences.getString("PHONENUM","");
    }


    public void setIsLoggedin(Boolean value)
    {
        createSessionBoolean("ISLOGIN", value);
    }

    public Boolean getIsLoggedin(){
        return this.preferences.getBoolean("ISLOGIN",false);
    }


    public void setKeypass(String value)
    {
        createSessionString("KEYPASS", value);
    }

    public String getKeypass(){
        return this.preferences.getString("KEYPASS","QWERTY");
    }


    public void setUid(String value)
    {
        createSessionString("UID", value);
    }

    public String getUid(){
        return this.preferences.getString("UID","");
    }


    public void setPass(String value)
    {
        createSessionString("PASS", value);
    }

    public String getPass(){
        return this.preferences.getString("PASS","");
    }


    public void setFullName(String value)
    {
        createSessionString("FULLNAME", value);
    }

    public String getFullName(){
        return this.preferences.getString("FULLNAME","");
    }


    public void setLastBalance(String value)
    {
        createSessionString("LASTBALANCE", value);
    }

    public String getLastBalance(){
        return this.preferences.getString("LASTBALANCE","");
    }

    public void setOptionNotification(Boolean value)
    {
        createSessionBoolean("OPTION_NOTIFICATION", value);
    }

    public Boolean getOptionNotification(){
        return this.preferences.getBoolean("OPTION_NOTIFICATION",false);
    }

    public void setOptionServiceBg(Boolean value)
    {
        createSessionBoolean("OPTION_SERVICE_BG", value);
    }

    public Boolean getOptionServiceBg(){
        return this.preferences.getBoolean("OPTION_SERVICE_BG",true);
    }

    public void setLastMacAddrBlutut(String value)
    {
        createSessionString("MACADDR_BT", value);
    }

    public String getLastMacAddrBlutut(){
        return this.preferences.getString("MACADDR_BT","");
    }

    public void setKomentarActivityIsOpen(boolean oke)
    {
        createSessionBoolean("KOMENTAR_IS_OPEN", oke);
    }

    public Boolean getKomentarActivityIsOpen()
    {
        return this.preferences.getBoolean("KOMENTAR_IS_OPEN",false);
    }

    public void setGroupActivityIsOpen(boolean oke)
    {
        createSessionBoolean("GROUP_IS_OPEN", oke);
    }

    public Boolean getGroupActivityIsOpen()
    {
        return this.preferences.getBoolean("GROUP_IS_OPEN",false);
    }

    public void setGroupDetailActivityIsOpen(boolean oke)
    {
        createSessionBoolean("GROUPCHAT_IS_OPEN", oke);
    }

    public Boolean getGroupDetailActivityIsOpen()
    {
        return this.preferences.getBoolean("GROUPCHAT_IS_OPEN",false);
    }

    public void setGroupIdOpened(String infoid)
    {
        createSessionString("GROUPID", infoid);
    }

    public String getGroupIdOpened()
    {
        return this.preferences.getString("GROUPID","");
    }

    public void setLastInfoId(String infoid)
    {
        createSessionString("INFOID", infoid);
    }

    public String getLastInfoId()
    {
        return this.preferences.getString("INFOID","");
    }

    public void setMemid(int memid)
    {
        createSessionInteger("MEMBER_ID", memid);
    }

    public Integer getMemid()
    {
        return this.preferences.getInt("MEMBER_ID",0);
    }
}
