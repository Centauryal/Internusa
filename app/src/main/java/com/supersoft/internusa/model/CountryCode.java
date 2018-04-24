package com.supersoft.internusa.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 10/18/2017.
 */

public class CountryCode implements Serializable {
    public static final String TABLENAME = "coutrycode";
    public static final String P_CODE = "prefikCode";
    public static final String P_NAME = "countryName";

    @SerializedName("PREFIK")
    @Expose
    public String PREFIK;
    @SerializedName("COUNTRY")
    @Expose
    public String COUNTRY;

    public CountryCode()
    {
        this.PREFIK = "";
        this.COUNTRY = "";
    }

    public CountryCode(String code, String country)
    {
        this.PREFIK = code;
        this.COUNTRY = country;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(P_CODE, PREFIK);
        values.put(P_NAME, COUNTRY);

        return values;
    }

    public static CountryCode fromCursor(Cursor cursor) {
        CountryCode cCode = new CountryCode();
        cCode.PREFIK = cursor.getString(cursor.getColumnIndex(P_CODE));
        cCode.COUNTRY = cursor.getString(cursor.getColumnIndex(P_NAME));
        //Log.e("ada", cCode.PREFIK);
        return cCode;
    }
}
