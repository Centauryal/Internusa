package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 1/4/2018.
 */

public class HistorytrxModel implements Serializable {
    public static final String TBL_NAME = "history_trx";
    public static final String P_NOHP = "tujuan";
    public static final String P_ID = "id";

    @SerializedName("ID")
    @Expose
    public String ID;

    @SerializedName("TUJUAN")
    @Expose
    public String TUJUAN;

    public HistorytrxModel()
    {

    }

    public HistorytrxModel(String tujuan)
    {
        this.TUJUAN = tujuan;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(P_NOHP, TUJUAN);
        return values;
    }

    public static HistorytrxModel fromCursor(Cursor cursor) {
        HistorytrxModel cCode = new HistorytrxModel();
        cCode.ID = cursor.getString(cursor.getColumnIndex(P_ID));
        cCode.TUJUAN = cursor.getString(cursor.getColumnIndex(P_NOHP));
        return cCode;
    }
}
