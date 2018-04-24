package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.supersoft.internusa.helper.util.Session;

import java.io.Serializable;

/**
 * Created by itclub21 on 12/4/2017.
 */

public class ChatGroupDetail implements Serializable {

    public static final String TABLENAME = "chatgroupdetail";

    public static final String P_ID = "id";
    public static final String P_IDBASE = "groupid";
    public static final String P_IDCHAT = "chatid";
    public static final String P_CREATEDATE = "createdate";
    public static final String P_CONTENT = "message";
    public static final String P_CREATORID = "creatorid";
    public static final String P_CREATORNAME = "creatorname";
    public static final String P_TYPE = "jenis"; //INCOMING / OUTGOING
    public static final String P_REPLAYID = "replayid"; //chatid
    public static final String P_ISUNREAD = "isread"; //chatid
    public static final String P_URIPATH = "uripath"; //chatid
    public static final String P_DOWNLOAD_PATH = "downloadpath"; //chatid
    public static final String P_CATEGORY = "kategori"; //file,movie,image

    @SerializedName("ID")
    @Expose
    public String ID;
    @SerializedName("GROUPID")
    @Expose
    public String GROUPID;
    @SerializedName("CREATEDATE")
    @Expose
    public String CREATEDATE;
    @SerializedName("LASTMESSAGE")
    @Expose
    public String LASTMESSAGE;
    @SerializedName("CREATORID")
    @Expose
    public String CREATORID;
    @SerializedName("CREATORNAME")
    @Expose
    public String CREATORNAME;

    @SerializedName("URIPATH")
    @Expose
    public String URIPATH;

    @SerializedName("DOWNLOADPATH")
    @Expose
    public String DOWNLOADPATH;

    @SerializedName("IDCHAT")
    @Expose
    public Integer IDCHAT;

    @SerializedName("TYPE")
    @Expose
    public Integer TYPE;

    @SerializedName("REPLAYID")
    @Expose
    public Integer REPLAYID;

    @SerializedName("ISREAD")
    @Expose
    public Integer ISREAD;

    public ChatGroupDetail()
    {

    }

    public ChatGroupDetail(String idBase, String msg, ProfilDB profil, Session _session)
    {

    }

    public ChatGroupDetail(ChatSingleModel cht, int isread)
    {
        this.CREATORID = cht.mem_id;
        this.CREATORNAME = cht.namapengirim;
        this.ISREAD = isread;
        this.IDCHAT = Integer.parseInt(cht.chatid);
        this.GROUPID = cht.groupid;
        this.LASTMESSAGE = cht.message;
        this.TYPE = cht.type;
        this.CREATEDATE = cht.tanggal;
        this.REPLAYID = 0;
        this.URIPATH = "";
        this.DOWNLOADPATH = "";
        this.URIPATH = "";
        this.DOWNLOADPATH = "";
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        //values.put(P_ID, ID);
        values.put(P_IDBASE, GROUPID);
        values.put(P_CREATEDATE, CREATEDATE);
        values.put(P_CREATORID, CREATORID);
        values.put(P_CREATORNAME, CREATORNAME);
        values.put(P_CONTENT, LASTMESSAGE);
        values.put(P_IDCHAT, IDCHAT);
        values.put(P_TYPE, TYPE);
        values.put(P_REPLAYID, REPLAYID);
        values.put(P_ISUNREAD, ISREAD);
        values.put(P_URIPATH, URIPATH);
        values.put(P_DOWNLOAD_PATH, DOWNLOADPATH);
        return values;
    }

    public static ChatGroupDetail fromCursor(Cursor cursor) {
        ChatGroupDetail cCode = new ChatGroupDetail();
        cCode.ID = cursor.getString(cursor.getColumnIndex(P_ID));
        cCode.GROUPID = cursor.getString(cursor.getColumnIndex(P_IDBASE));
        cCode.CREATEDATE = cursor.getString(cursor.getColumnIndex(P_CREATEDATE));
        cCode.CREATORID = cursor.getString(cursor.getColumnIndex(P_CREATORID));
        cCode.CREATORNAME = cursor.getString(cursor.getColumnIndex(P_CREATORNAME));
        cCode.LASTMESSAGE = cursor.getString(cursor.getColumnIndex(P_CONTENT));
        cCode.URIPATH = cursor.getString(cursor.getColumnIndex(P_URIPATH));
        cCode.DOWNLOADPATH = cursor.getString(cursor.getColumnIndex(P_DOWNLOAD_PATH));
        cCode.IDCHAT = cursor.getInt(cursor.getColumnIndex(P_IDCHAT));
        cCode.TYPE = cursor.getInt(cursor.getColumnIndex(P_TYPE));
        cCode.REPLAYID = cursor.getInt(cursor.getColumnIndex(P_REPLAYID));
        cCode.ISREAD = cursor.getInt(cursor.getColumnIndex(P_ISUNREAD));
        //Log.e("ada", cCode.PREFIK);
        return cCode;
    }
}
