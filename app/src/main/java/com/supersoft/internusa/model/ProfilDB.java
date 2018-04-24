package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by itclub21 on 3/6/2017.
 * id_profil integer primary key,
 * name text,
 * email text,
 * hp text,
 * pin text,
 * activated integer,
 * islogin integer,
 * img text
 */

public class ProfilDB {
    @SerializedName("agenid")
    @Expose
    public String agenid;

    @SerializedName("fullname")
    @Expose
    public String fullname;

    @SerializedName("prefik")
    @Expose
    public String prefik;

    @SerializedName("hp")
    @Expose
    public String hp;

    @SerializedName("pin")
    @Expose
    public String pin;

    @SerializedName("avatar")
    @Expose
    public String avatar;

	@SerializedName("mem_id")
    @Expose
    public Integer mem_id;
}
