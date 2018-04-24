package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 1/6/2018.
 */

public class LikestatusModel implements Serializable {

    @SerializedName("mitraid")
    @Expose
    public String mitraid;

    @SerializedName("infoid")
    @Expose
    public Integer infoid;

    @SerializedName("komenid")
    @Expose
    public Integer komenid;

    @SerializedName("likeid")
    @Expose
    public Integer likeid;

    @SerializedName("totallike")
    @Expose
    public Integer totallike;

    @SerializedName("namaliker")
    @Expose
    public String namaliker;

}
