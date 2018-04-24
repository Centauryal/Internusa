package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 12/1/2017.
 *
 *
 tipe:
 . timeline
 . komentar
 . promo

 */

public class InfoItem implements Serializable {



    @SerializedName("tipe")
    @Expose
    public String tipe;

    @SerializedName("infoid")
    @Expose
    public int infoid;

    @SerializedName("lastkomen")
    @Expose
    public int lastkomen;
}
