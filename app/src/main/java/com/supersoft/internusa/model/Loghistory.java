package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.supersoft.internusa.helper.util.Constant;

import java.io.Serializable;

/**
 * Created by itclub21 on 12/27/2017.
 */

public class Loghistory implements Serializable {
    public static final String TABLENAME = "loghistory";
    public static final String P_ID = "id";
    public static final String P_TITLE = "title";
    public static final String P_SENDER = "sender";
    public static final String P_DESCRIPTION = "dskripsi";
    public static final String P_TYPE = "type"; // create_topik,chat,promocode,webcode,komentar,postpaid,prepaid
    public static final String P_IDBASE = "idbase";
    public static final String P_ACTIVITY = "activity";
    public static final String P_CREATEDATE = "tanggal";

    @SerializedName("ID")
    @Expose
    public String ID;
    @SerializedName("ACTIVITY")
    @Expose
    public String ACTIVITY;
    @SerializedName("IDBASE")
    @Expose
    public String IDBASE;
    @SerializedName("TITLE")
    @Expose
    public String TITLE;
    @SerializedName("DESCRIPTION")
    @Expose
    public String DESCRIPTION;
    @SerializedName("TYPE")
    @Expose
    public String TYPE;
    @SerializedName("CREATEDATE")
    @Expose
    public String CREATEDATE;
    @SerializedName("SENDER")
    @Expose
    public String SENDER;

    public Loghistory()
    {

    }

    public Loghistory(String type, InfoTipe object)
    {
        this.TYPE = type;
        this.TITLE = object.title;
        this.DESCRIPTION = object.body;
        this.IDBASE = object.id;
        this.ACTIVITY = object.activity;
        this.CREATEDATE = object.tanggal;

    }


    public Loghistory(String type, Object object)
    {

        if(object instanceof LikestatusModel) {
            LikestatusModel obj = (LikestatusModel)object;
            this.TYPE = type;
            this.TITLE = obj.namaliker;
            this.DESCRIPTION = obj.namaliker + " Menyukai kiriman ";
            this.IDBASE = String.valueOf(obj.infoid);
            this.ACTIVITY = "";
            this.CREATEDATE = Constant.currDate();
        }
        else if(object instanceof KomenModel) {
            KomenModel obj = (KomenModel)object;
            this.TYPE = type;
            this.TITLE = obj.pengirim;
            this.DESCRIPTION = obj.isi;
            this.IDBASE = String.valueOf(obj.idbase);
            this.ACTIVITY = "";
            this.CREATEDATE = obj.tglkirim;
        }
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(P_IDBASE, IDBASE);
        values.put(P_TITLE, TITLE);
        values.put(P_DESCRIPTION, DESCRIPTION);
        values.put(P_TYPE, TYPE);
        values.put(P_ACTIVITY, ACTIVITY);
        values.put(P_CREATEDATE, CREATEDATE);
        return values;
    }

    public static Loghistory fromCursor(Cursor cursor) {
        Loghistory cCode = new Loghistory();
        cCode.ID = cursor.getString(cursor.getColumnIndex(P_ID));
        cCode.IDBASE = cursor.getString(cursor.getColumnIndex(P_IDBASE));
        cCode.TITLE = cursor.getString(cursor.getColumnIndex(P_TITLE));
        cCode.DESCRIPTION = cursor.getString(cursor.getColumnIndex(P_DESCRIPTION));
        cCode.TYPE = cursor.getString(cursor.getColumnIndex(P_TYPE));
        cCode.ACTIVITY = cursor.getString(cursor.getColumnIndex(P_ACTIVITY));
        cCode.CREATEDATE = cursor.getString(cursor.getColumnIndex(P_CREATEDATE));
        return cCode;
    }
}
