package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 4/5/2017.
 */

public class MutualFriend implements Serializable {
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("mem_id")
    @Expose
    private String memId;

    /**
     *
     * @return
     * The nama
     */
    public String getNama() {
        return nama;
    }

    /**
     *
     * @param nama
     * The nama
     */
    public void setNama(String nama) {
        this.nama = nama;
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
}