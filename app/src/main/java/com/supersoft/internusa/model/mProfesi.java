package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 4/21/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class mProfesi {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("posisi")
    @Expose
    private String posisi;
    @SerializedName("seko_nama")
    @Expose
    private String sekoNama;
    @SerializedName("jur_nama")
    @Expose
    private String jurNama;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("bidang")
    @Expose
    private String bidang;
    @SerializedName("instansi")
    @Expose
    private String instansi;
    @SerializedName("jurusan")
    @Expose
    private String jurusan;
    @SerializedName("keyword")
    @Expose
    private List<String> keyword = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getSekoNama() {
        return sekoNama;
    }

    public void setSekoNama(String sekoNama) {
        this.sekoNama = sekoNama;
    }

    public String getJurNama() {
        return jurNama;
    }

    public void setJurNama(String jurNama) {
        this.jurNama = jurNama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getInstansi() {
        return instansi;
    }

    public void setInstansi(String instansi) {
        this.instansi = instansi;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }
}
