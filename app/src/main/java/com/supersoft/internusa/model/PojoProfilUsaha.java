package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/10/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PojoProfilUsaha implements Serializable{
    @SerializedName("rows")
    @Expose
    private List<Row> rows = null;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("offset")
    @Expose
    private Integer offset;

    @SerializedName("lastid")
    @Expose
    private Integer lastid;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Object getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLastId() {
        return lastid;
    }

    public void setLastid(Integer offset) {
        this.lastid = offset;
    }

    public class ListImages implements Serializable
    {

    }
}
