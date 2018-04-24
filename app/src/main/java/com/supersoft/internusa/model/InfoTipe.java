package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by itclub21 on 10/18/2017.
 */

public class InfoTipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("sound")
    @Expose
    public String sound;
    @SerializedName("activity")
    @Expose
    public String activity;
    @SerializedName("bgpic")
    @Expose
    public String bgpic;
    @SerializedName("mitraid")
    @Expose
    public String mitraid;

    @SerializedName("tanggal")
    @Expose
    public String tanggal;

    @SerializedName("detail")
    @Expose
    public List<InfoItem> detail;

    @SerializedName("chatdata")
    @Expose
    public ChatSingleModel chatdata;

    @SerializedName("likedata")
    @Expose
    public LikestatusModel likedata;

    @SerializedName("komendata")
    @Expose
    public KomenModel komendata;
}
