package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.supersoft.internusa.helper.imagePicker.Image;

import java.io.Serializable;
import java.util.List;

/**
 * Created by itclub21 on 4/22/2017.
 */

public class mUsaha implements Serializable {

    @SerializedName("pnf_id")
    @Expose
    private String pnfId;
    @SerializedName("pnf_nama")
    @Expose
    private String pnfNama;
    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("bidang")
    @Expose
    private String bidang;
    @SerializedName("posisi")
    @Expose
    private String posisi;
    @SerializedName("alamat_detail")
    @Expose
    private String alamatDetail;
    @SerializedName("gallery")
    @Expose
    private List<Image> gallery = null;

    public String getPnfId() {
        return pnfId;
    }

    public void setPnfId(String id) {
        this.pnfId = id;
    }

    public String getPnfNama() {
        return pnfNama;
    }

    public void setPnfNama(String posisi) {
        this.pnfNama = posisi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String sekoNama) {
        this.deskripsi = sekoNama;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String jurNama) {
        this.url = jurNama;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String deskripsi) {
        this.logo = deskripsi;
    }

    public String getBidang() {
        return bidang;
    }

    public void setBidang(String bidang) {
        this.bidang = bidang;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String instansi) {
        this.posisi = instansi;
    }

    public String getAlamatDetail() {
        return alamatDetail;
    }

    public void setAlamatDetail(String jurusan) {
        this.alamatDetail = jurusan;
    }

    public List<Image> getGallery() {
        return gallery;
    }

    public void setGallery(List<Image> keyword) {
        this.gallery = keyword;
    }
}
