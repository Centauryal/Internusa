package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by itclub21 on 11/30/2017.
 */

public class MessageBroadcast implements Serializable {

    @SerializedName("username")
    @Expose
    public Integer username;

    @SerializedName("body")
    @Expose
    public String body;

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("infoid")
    @Expose
    public Integer infoid;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("mitraid")
    @Expose
    public String mitraid;

    @SerializedName("detail")
    @Expose
    public List<detil> detail;

    public class detil
    {
        @SerializedName("tipe")
        @Expose
        public String tipe;

        @SerializedName("infoid")
        @Expose
        public Integer infoid;

        @SerializedName("lastkomen")
        @Expose
        public Integer lastkomen;
    }
}
