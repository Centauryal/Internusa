package com.supersoft.internusa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by itclub21 on 4/23/2017.
 */

public class mUsahaGallery implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("path")
    @Expose
    public String path;

}
