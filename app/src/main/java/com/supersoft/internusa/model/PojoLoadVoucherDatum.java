package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/8/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class PojoLoadVoucherDatum implements Serializable {

    @SerializedName("vtype")
    @Expose
    private String vtype;
    @SerializedName("ket")
    @Expose
    private String ket;
    @SerializedName("harga")
    @Expose
    private String harga;


    /**
     *
     * @return
     * The vtype
     */
    public String getVtype() {
        return vtype;
    }

    /**
     *
     * @param vtype
     * The vtype
     */
    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    /**
     *
     * @return
     * The ket
     */
    public String getKet() {
        return ket;
    }

    /**
     *
     * @param ket
     * The ket
     */
    public void setKet(String ket) {
        this.ket = ket;
    }

    /**
     *
     * @return
     * The harga
     */
    public String getHarga() {
        return harga;
    }

    /**
     *
     * @param harga
     * The harga
     */
    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String toString(){
        return getVtype() + " - " + getHarga();
    }

}