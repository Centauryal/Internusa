package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 4/21/2017.
 */

public class ProfesiParamModel implements Serializable {

    @SerializedName("agenid")
    @Expose
    public String agenid;

    @SerializedName("hp")
    @Expose
    public String hp;

    @SerializedName("bidang")
    @Expose
    public String bidang;

    @SerializedName("tag")
    @Expose
    public String tag;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("posisi")
    @Expose
    public String posisi;

    @SerializedName("sekolah")
    @Expose
    public String sekolah;

    @SerializedName("jurusan")
    @Expose
    public String jurusan;

    @SerializedName("instansi")
    @Expose
    public String instansi;

    @SerializedName("pfid")
    @Expose
    public String pfid;
}
