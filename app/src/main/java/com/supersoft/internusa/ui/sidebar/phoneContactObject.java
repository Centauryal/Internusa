package com.supersoft.internusa.ui.sidebar;

import com.google.gson.annotations.SerializedName;

/**
 * Created by itclub21 on 6/19/2017.
 */

public class phoneContactObject
{
    @SerializedName("contactId")
    int contactId;
    @SerializedName("contactName")
    String contactName;
    @SerializedName("contactNumber")
    String contactNumber;
}