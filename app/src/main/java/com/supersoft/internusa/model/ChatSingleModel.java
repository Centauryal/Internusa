package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 12/11/2017.
 */

public class ChatSingleModel implements Serializable {

    @SerializedName("chatid")
    @Expose
    public String chatid;

    @SerializedName("groupid")
    @Expose
    public String groupid;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("mem_id")
    @Expose
    public String mem_id;

    @SerializedName("namapengirim")
    @Expose
    public String namapengirim;

    @SerializedName("avatar")
    @Expose
    public String avatar;

    @SerializedName("tanggal")
    @Expose
    public String tanggal;

    @SerializedName("jenis")
    @Expose
    public String jenis;

    @SerializedName("type")
    @Expose
    public Integer type;

    @SerializedName("color")
    @Expose
    public Integer color;

    @SerializedName("creatorid")
    @Expose
    public Integer creatorid;

    @SerializedName("creatorname")
    @Expose
    public String creatorname;
}
