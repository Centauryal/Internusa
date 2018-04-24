package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 4/19/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cities {
    @SerializedName("kk_nama")
    @Expose
    private String kkNama;
    @SerializedName("kk_kode")
    @Expose
    private String kkKode;

    public String getKkNama() {
        return kkNama;
    }

    public void setKkNama(String kkNama) {
        this.kkNama = kkNama;
    }

    public String getKkKode() {
        return kkKode;
    }

    public void setKkKode(String kkKode) {
        this.kkKode = kkKode;
    }
}
