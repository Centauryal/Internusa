package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.supersoft.internusa.helper.util.DBHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by itclub21 on 12/4/2017.
 */

public class ChatGroupModel implements Serializable {
    public static final String TABLENAME = "chatgroup";

    public static final String P_ID = "id";
    public static final String P_IDBASE = "groupid";
    public static final String P_CREATEDATE = "createdate";
    public static final String P_CONTENT = "message";
    public static final String P_TITLETOPIK = "topik";
    public static final String P_CREATORID = "creatorid";
    public static final String P_CREATORNAME = "creatorname";
    public static final String P_CREATORAVATAR = "avatar";
    public static final String P_UNREAD = "unread";
    public static final String P_STATUS = "status";
    public static final String P_IMPORTANT = "isimportant";
    public static final String P_COLOR = "color";
    public static final String P_LASTNAMESENDER = "npterakhir";
    public static final String P_HASJOINED = "joingroup";
    public static final String P_LASTUPDATE = "lastupdate";

    DBHelper _db;


    @SerializedName("ID")
    @Expose
    public String ID;
    @SerializedName("IDBASE")
    @Expose
    public String IDBASE;
    @SerializedName("CREATEDATE")
    @Expose
    public String CREATEDATE;
    @SerializedName("LAST_UPDATE")
    @Expose
    public String LAST_UPDATE;
    @SerializedName("LASTMESSAGE")
    @Expose
    public String LASTMESSAGE;
    @SerializedName("CREATORID")
    @Expose
    public String CREATORID;
    @SerializedName("CREATORNAME")
    @Expose
    public String CREATORNAME;
    @SerializedName("AVATAR")
    @Expose
    public String AVATAR;
    @SerializedName("STATUS")
    @Expose
    public String STATUS;
    @SerializedName("NAMAPENGIRIMTERAKHIR")
    @Expose
    public String NAMAPENGIRIMTERAKHIR;

    @SerializedName("TOPIK")
    @Expose
    public String TOPIK;

    @SerializedName("ISCHECKED")
    @Expose
    public Boolean ISCHECKED;
    @SerializedName("ISUNREAD")
    @Expose
    public Integer ISUNREAD;

    @SerializedName("COLOR")
    @Expose
    public Integer COLOR;

    @SerializedName("IMPORTANT")
    @Expose
    public Boolean IMPORTANT;

    @SerializedName("JOINGROUP")
    @Expose
    public Boolean JOINGROUP;

    public ChatGroupModel()
    {

    }

    public ChatGroupModel(ChatSingleModel cht)
    {
        this.LASTMESSAGE = cht.message;
        this.CREATORNAME = cht.creatorname;
        this.NAMAPENGIRIMTERAKHIR = cht.namapengirim;
        this.CREATORID = String.valueOf(cht.creatorid);
        this.CREATEDATE = cht.tanggal;
        this.COLOR = cht.color;
        this.IMPORTANT = false;
        this.STATUS = "successfull";
        this.ISUNREAD = 0;
        this.IDBASE = cht.groupid;
        this.CREATEDATE = cht.tanggal;

        if ("create_topik".equals(cht.jenis)) {
            this.TOPIK = cht.message;
            this.AVATAR = cht.avatar;

        }
        else {
            this.LASTMESSAGE = "";
            this.TOPIK = "";
            this.AVATAR = "";
        }
    }

    //untuk update pengirim terakhoir
    public ChatGroupModel(String idBase, ProfilDB profil, String message)
    {
        this.IDBASE = String.valueOf(idBase);
        this.NAMAPENGIRIMTERAKHIR = (profil.fullname == null) ? profil.hp : profil.fullname;
        this.LASTMESSAGE = message;
        this.ISUNREAD = 0;
    }

    public ChatGroupModel(JsonObject obj, String priority)
    {
        switch (priority)
        {
            case "priority":
                this.TOPIK = obj.get("message").getAsString();
                this.LASTMESSAGE = obj.get("message").getAsString();
                this.CREATORNAME = obj.get("namapengirim").getAsString();
                this.CREATORID = obj.get("mem_id").getAsString();
                this.CREATEDATE = obj.get("tanggal").getAsString();
                this.IDBASE = obj.get("groupid").getAsString();
                this.IMPORTANT = true;
                this.STATUS = "successfull";
                //this.ISUNREAD = 0;
                this.COLOR = obj.get("color").getAsInt();
                this.NAMAPENGIRIMTERAKHIR = obj.get("namapengirim").getAsString();
                this.JOINGROUP = true;
                break;
            case "create_group":
                this.TOPIK = "";
                this.LASTMESSAGE = obj.get("message").getAsString();
                this.CREATORNAME = obj.get("nama").getAsString();
                this.CREATORID = obj.get("mem_id").getAsString();
                this.CREATEDATE = obj.get("tanggal").getAsString();
                this.ID = obj.get("localid").getAsString();
                this.IDBASE = obj.get("groupid").getAsString();
                this.IMPORTANT = false;
                this.STATUS = "successfull";
                this.ISUNREAD = 0;
                this.COLOR = obj.get("color").getAsInt();
                this.NAMAPENGIRIMTERAKHIR = obj.get("nama").getAsString();
                break;
            case "sync_group":
                this.TOPIK = obj.get("message").getAsString();
                this.LASTMESSAGE = obj.get("message").getAsString();
                this.CREATORNAME = (obj.get("namapengirim").isJsonNull()) ? "" : obj.get("namapengirim").getAsString();
                this.CREATORID = obj.get("mem_id").getAsString();
                this.CREATEDATE = obj.get("tanggal").getAsString();
                this.IDBASE = obj.get("groupid").getAsString();
                this.IMPORTANT = false;
                this.STATUS = "successfull";
                this.ISUNREAD = 0;
                this.COLOR = obj.get("color").getAsInt();
                this.NAMAPENGIRIMTERAKHIR = (obj.get("namapengirim").isJsonNull()) ? "" : obj.get("namapengirim").getAsString();
                this.JOINGROUP = false;
                break;
            case "update_last_pengirim":
                break;
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(P_IDBASE, IDBASE);
        values.put(P_CREATEDATE, CREATEDATE);
        values.put(P_CREATORID, CREATORID);
        values.put(P_CREATORNAME, CREATORNAME);
        values.put(P_CREATORAVATAR, AVATAR);
        if(!LASTMESSAGE.equals("")) values.put(P_CONTENT, LASTMESSAGE);
        if(!TOPIK.equals("")) values.put(P_TITLETOPIK, TOPIK);
        values.put(P_UNREAD, ISUNREAD);
        values.put(P_STATUS, STATUS);
        values.put(P_IMPORTANT, (!IMPORTANT) ? 0 : 1);
        values.put(P_COLOR, COLOR);
        values.put(P_LASTNAMESENDER, NAMAPENGIRIMTERAKHIR);
        values.put(P_LASTUPDATE, getDateTime());
        try{values.put(P_HASJOINED, (JOINGROUP) ? 1 : 0);} catch (NullPointerException e){}
        try{
            if(!AVATAR.equals("")) values.put(P_CREATORAVATAR, AVATAR);
        }
        catch (NullPointerException e){}

        return values;
    }

    public ContentValues getContentValuesForJinGroup() {
        ContentValues values = new ContentValues();
        //values.put(P_ID, ID);
        values.put(P_IDBASE, IDBASE);
        values.put(P_HASJOINED, (!JOINGROUP) ? 0 : 1);

        return values;
    }

    public static ChatGroupModel fromCursor(Cursor cursor) {
        ChatGroupModel cCode = new ChatGroupModel();
        cCode.ID = cursor.getString(cursor.getColumnIndex(P_ID));
        cCode.IDBASE = cursor.getString(cursor.getColumnIndex(P_IDBASE));
        cCode.CREATEDATE = cursor.getString(cursor.getColumnIndex(P_CREATEDATE));
        cCode.LAST_UPDATE = cursor.getString(cursor.getColumnIndex(P_LASTUPDATE));
        cCode.CREATORID = cursor.getString(cursor.getColumnIndex(P_CREATORID));
        cCode.CREATORNAME = cursor.getString(cursor.getColumnIndex(P_CREATORNAME));
        cCode.AVATAR = cursor.getString(cursor.getColumnIndex(P_CREATORAVATAR));
        cCode.LASTMESSAGE = cursor.getString(cursor.getColumnIndex(P_CONTENT));
        cCode.NAMAPENGIRIMTERAKHIR = cursor.getString(cursor.getColumnIndex(P_LASTNAMESENDER));
        cCode.TOPIK = cursor.getString(cursor.getColumnIndex(P_TITLETOPIK));
        cCode.ISUNREAD = cursor.getInt(cursor.getColumnIndex(P_UNREAD));

        cCode.COLOR = cursor.getInt(cursor.getColumnIndex(P_COLOR));
        Boolean importatn = (cursor.getInt(cursor.getColumnIndex(P_IMPORTANT)) == 1);
        cCode.IMPORTANT = importatn;
        Boolean hasjoin = (cursor.getInt(cursor.getColumnIndex(P_HASJOINED)) == 1);
        cCode.JOINGROUP = hasjoin;
        //Log.e("ada", cCode.PREFIK);
        return cCode;
    }
}
