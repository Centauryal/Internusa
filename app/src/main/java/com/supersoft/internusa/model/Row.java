package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by itclub21 on 4/5/2017.
 */

public class Row implements Serializable {
    @SerializedName("onViewMore")
    @Expose
    public Boolean onViewMore;
    @SerializedName("mem_id")
    @Expose
    private String memId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tipe")
    @Expose
    private String tipe;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("totalkomentar")
    @Expose
    public String totalkomentar;
    @SerializedName("totallikes")
    @Expose
    public String totallikes;
    @SerializedName("judul")
    @Expose
    public String judul;
    @SerializedName("isi")
    @Expose
    public String isi;

    @SerializedName("pengirim")
    @Expose
    public String pengirim;
    @SerializedName("ownlike")
    @Expose
    public Boolean ownlike;

    @SerializedName("tglkirim")
    @Expose
    public String tglkirim;
    @SerializedName("kirimdari")
    @Expose
    public String kirimdari;
    @SerializedName("telp")
    @Expose
    public String telp;
    @SerializedName("image")
    @Expose
    private List<String> image = null;

    @SerializedName("mem_nama")
    @Expose
    private String memNama;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("hp")
    @Expose
    private String hp;
    @SerializedName("almt_detail")
    @Expose
    private String almtDetail;
    @SerializedName("kab_kode")
    @Expose
    private String kabKode;
    @SerializedName("lat")
    @Expose
    private Object lat;
    @SerializedName("lon")
    @Expose
    private Object lon;
    @SerializedName("vzoom")
    @Expose
    private Object vzoom;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("mutual_friend")
    @Expose
    private List<MutualFriend> mutualFriend = new ArrayList<MutualFriend>();
    @SerializedName("mem_background")
    @Expose
    private String memBackground;
    @SerializedName("list_usaha_profesi")
    @Expose
    private List<ListUsahaProfesi> listUsahaProfesi = new ArrayList<ListUsahaProfesi>();

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("last_update")
    @Expose
    public String last_update;

    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("pertanyaan")
    @Expose
    private String pertanyaan;
    @SerializedName("jawaban")
    @Expose
    private String jawaban;

    @SerializedName("sql")
    @Expose
    public String sql;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String nama) {
        this.status = nama;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String nama) {
        this.tipe = nama;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    public void setPertanyaan(String pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public String getJawaban() {
        return jawaban;
    }

    public void setJawaban(String jawaban) {
        this.jawaban = jawaban;
    }

    /**
     *
     * @return
     * The memId
     */
    public String getMemId() {
        return memId;
    }

    /**
     *
     * @param memId
     * The mem_id
     */
    public void setMemId(String memId) {
        this.memId = memId;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     *
     * @return
     * The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     *
     * @param avatar
     * The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     *
     * @return
     * The memNama
     */
    public String getMemNama() {
        return memNama;
    }

    /**
     *
     * @param memNama
     * The mem_nama
     */
    public void setMemNama(String memNama) {
        this.memNama = memNama;
    }

    /**
     *
     * @return
     * The hp
     */
    public String getHp() {
        return hp;
    }

    /**
     *
     * @param hp
     * The hp
     */
    public void setHp(String hp) {
        this.hp = hp;
    }

    /**
     *
     * @return
     * The almtDetail
     */
    public String getAlmtDetail() {
        return almtDetail;
    }

    /**
     *
     * @param almtDetail
     * The almt_detail
     */
    public void setAlmtDetail(String almtDetail) {
        this.almtDetail = almtDetail;
    }

    /**
     *
     * @return
     * The kabKode
     */
    public String getKabKode() {
        return kabKode;
    }

    /**
     *
     * @param kabKode
     * The kab_kode
     */
    public void setKabKode(String kabKode) {
        this.kabKode = kabKode;
    }

    /**
     *
     * @return
     * The lat
     */
    public Object getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Object lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lon
     */
    public Object getLon() {
        return lon;
    }

    /**
     *
     * @param lon
     * The lon
     */
    public void setLon(Object lon) {
        this.lon = lon;
    }

    /**
     *
     * @return
     * The vzoom
     */
    public Object getVzoom() {
        return vzoom;
    }

    /**
     *
     * @param vzoom
     * The vzoom
     */
    public void setVzoom(Object vzoom) {
        this.vzoom = vzoom;
    }

    /**
     *
     * @return
     * The logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     *
     * @param logo
     * The logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     *
     * @return
     * The mutualFriend
     */
    public List<MutualFriend> getMutualFriend() {
        return mutualFriend;
    }

    /**
     *
     * @param mutualFriend
     * The mutual_friend
     */
    public void setMutualFriend(List<MutualFriend> mutualFriend) {
        this.mutualFriend = mutualFriend;
    }

    /**
     *
     * @return
     * The memBackground
     */
    public String getMemBackground() {
        return memBackground;
    }

    /**
     *
     * @param memBackground
     * The mem_background
     */
    public void setMemBackground(String memBackground) {
        this.memBackground = memBackground;
    }

    /**
     *
     * @return
     * The listUsahaProfesi
     */
    public List<ListUsahaProfesi> getListUsahaProfesi() {
        return listUsahaProfesi;
    }

    /**
     *
     * @param listUsahaProfesi
     * The list_usaha_profesi
     */
    public void setListUsahaProfesi(List<ListUsahaProfesi> listUsahaProfesi) {
        this.listUsahaProfesi = listUsahaProfesi;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }
}