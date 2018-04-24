package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 3/5/2017.
 */

public class ModelRequestInfo implements Serializable {
    @SerializedName("agenid")
    @Expose
    public String agenid;

    @SerializedName("localid")
    @Expose
    public String localid;

    @SerializedName("hp")
    @Expose
    public String hp;


    @SerializedName("deviceid")
    @Expose
    public String deviceid;


    @SerializedName("mitraid")
    @Expose
    public String mitraid;

    @SerializedName("pin")
    @Expose
    public String pin;

    @SerializedName("keyactivation")
    @Expose
    public String keyactivation;

    @SerializedName("opr")
    @Expose
    public String opr;

    @SerializedName("msisdn")
    @Expose
    public String msisdn;

    @SerializedName("nominal")
    @Expose
    public String nominal;

    @SerializedName("voc")
    @Expose
    public String voc;

    @SerializedName("code")
    @Expose
    public String code;

    @SerializedName("bank")
    @Expose
    public String bank;

    @SerializedName("hp_pelanggan")
    @Expose
    public String hp_pelanggan;

    @SerializedName("limit")
    @Expose
    public Integer limit;

    @SerializedName("offset")
    @Expose
    public Integer offset;

    @SerializedName("info_id")
    @Expose
    public String info_id;

    @SerializedName("member_id")
    @Expose
    public String member_id;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("nama")
    @Expose
    public String nama;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("confpassword")
    @Expose
    public String confpassword;

    @SerializedName("upline")
    @Expose
    public String upline;

    @SerializedName("gender")
    @Expose
    public String gender;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("pertanyaan")
    @Expose
    public String pertanyaan;

    @SerializedName("jawaban")
    @Expose
    public String jawaban;

    @SerializedName("type")
    @Expose
    public String type;


    @SerializedName("reg_gcm")
    @Expose
    public String reg_gcm;

    @SerializedName("lat")
    @Expose
    public String lat;

    @SerializedName("lon")
    @Expose
    public String lon;

    @SerializedName("info")
    @Expose
    public String info;

    @SerializedName("kota")
    @Expose
    public String kota;


    @SerializedName("an")
    @Expose
    public String an;



    @SerializedName("bonusup")
    @Expose
    public String bonusup;

    @SerializedName("agentujuan")
    @Expose
    public String agentujuan;

    @SerializedName("trxid")
    @Expose
    public String trxid;

    @SerializedName("dinapin")
    @Expose
    public String dinapin;

    @SerializedName("berita")
    @Expose
    public String berita;

    @SerializedName("lastid")
    @Expose
    public int lastid;

    @SerializedName("ccode")
    @Expose
    public String ccode;

}
