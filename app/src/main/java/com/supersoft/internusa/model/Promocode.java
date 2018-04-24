package com.supersoft.internusa.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 11/6/2017.
 * table : promocode
 * field:
 *  ID  =   TITLE   =   DESC    =   DATE_TIME   =   BGIMAGE =   ACTION  = ACTIVITY
 */

public class Promocode implements Serializable {
    public static final String TABLENAME = "promocode";

    public static final String F_ID = "id";
    public static final String F_TITLE = "judul";
    public static final String F_DESC = "isi";
    public static final String F_DATETIME = "dttayang";
    public static final String F_BGIMAGE = "bgimage";
    public static final String F_ACTION = "aksi";
    public static final String F_ACTIVITY = "aktivity";

    @SerializedName("ID")
    @Expose
    public String ID;

    @SerializedName("TITLE")
    @Expose
    public String TITLE;

    @SerializedName("DESC")
    @Expose
    public String DESC;

    @SerializedName("DATETIME")
    @Expose
    public String DATETIME;

    @SerializedName("BGIMAGE")
    @Expose
    public String BGIMAGE;

    @SerializedName("ACTION")
    @Expose
    public String ACTION;

    public Promocode (String id, String title, String desc, String dt, String bgimg, String act)
    {
        this.ID = id;
        this.TITLE = title;
        this.DESC = desc;
        this.DATETIME = dt;
        this.BGIMAGE = bgimg;
        this.ACTION = act;
    }



    public static Promocode fromCursor(Cursor cursor) {

        return new Promocode(cursor.getString(cursor.getColumnIndex(F_ID)),
                cursor.getString(cursor.getColumnIndex(F_TITLE)),
                cursor.getString(cursor.getColumnIndex(F_DESC)),
                cursor.getString(cursor.getColumnIndex(F_DATETIME)),
                cursor.getString(cursor.getColumnIndex(F_BGIMAGE)),
                cursor.getString(cursor.getColumnIndex(F_ACTION)));
    }
}
