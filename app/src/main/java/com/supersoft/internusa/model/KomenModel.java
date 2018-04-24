package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 12/1/2017.
 *
 * (id integer primary key,
 * idbase integer,
 * infoid integer,
 * pengirim TEXT,
 * kirimdari TEXT,
 * isi TEXT,
 * tglkirim TEXT)
 */

public class KomenModel implements Serializable {
    public static final String TABLENAME = "komentar";
    public static final String F_ID = "id";
    public static final String F_IDBASE = "idbase";
    public static final String F_INFOID = "infoid";
    public static final String F_PENGIRIM = "pengirim";
    public static final String F_KIRIMDARI = "kirimdari";
    public static final String F_TGLKIRIM = "tglkirim";
    public static final String F_ISI = "isi";


    public KomenModel()
    {

    }

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("idbase")
    @Expose
    public Integer idbase;

    @SerializedName("infoid")
    @Expose
    public Integer infoid;

    @SerializedName("pengirim")
    @Expose
    public String pengirim;

    @SerializedName("isi")
    @Expose
    public String isi;

    @SerializedName("kirimdari")
    @Expose
    public String kirimdari;

    @SerializedName("tglkirim")
    @Expose
    public String tglkirim;

    @SerializedName("viewMore")
    @Expose
    public Boolean viewMore;


    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(F_IDBASE, idbase);
        values.put(F_INFOID, infoid);
        values.put(F_PENGIRIM, pengirim);
        values.put(F_ISI, isi);
        values.put(F_KIRIMDARI, kirimdari);
        values.put(F_TGLKIRIM, tglkirim);

        return values;
    }

    public static KomenModel fromCursor(Cursor cursor) {
        KomenModel cCode = new KomenModel();
        cCode.id = cursor.getString(cursor.getColumnIndex(F_ID));
        cCode.pengirim = cursor.getString(cursor.getColumnIndex(F_PENGIRIM));
        cCode.isi = cursor.getString(cursor.getColumnIndex(F_ISI));
        cCode.kirimdari = cursor.getString(cursor.getColumnIndex(F_KIRIMDARI));
        cCode.tglkirim = cursor.getString(cursor.getColumnIndex(F_TGLKIRIM));
        cCode.idbase = cursor.getInt(cursor.getColumnIndex(F_IDBASE));
        cCode.infoid = cursor.getInt(cursor.getColumnIndex(F_INFOID));
        //Log.e("ada", cCode.PREFIK);
        return cCode;
    }
}
