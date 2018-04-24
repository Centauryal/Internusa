package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by itclub21 on 10/11/2017.
 */

public class Timelinetujuan implements Serializable{
    public static final String TABLENAME = "statustimeline";

    public static final String F_NAMA = "fnama";
    public static final String F_ID = "id";
    public static final String F_HINT = "hint";

    @SerializedName("tujuan")
    @Expose
    public List<Tujuan> tujuan;

    @SerializedName("status")
    @Expose
    public List<Tujuan> status;



    public final class Tujuan implements Serializable
    {
        @SerializedName("nama")
        @Expose
        public String nama;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("hint")
        @Expose
        public String hint;
    }
}
