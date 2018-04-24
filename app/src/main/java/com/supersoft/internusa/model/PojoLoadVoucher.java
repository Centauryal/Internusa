package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/8/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PojoLoadVoucher implements Serializable {
    @SerializedName("data")
    @Expose
    private List<PojoLoadVoucherDatum> data = new ArrayList<PojoLoadVoucherDatum>();

    /**
     *
     * @return
     * The data
     */
    public List<PojoLoadVoucherDatum> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<PojoLoadVoucherDatum> data) {
        this.data = data;
    }



}