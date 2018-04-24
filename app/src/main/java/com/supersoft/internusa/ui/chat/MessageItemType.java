package com.supersoft.internusa.ui.chat;

/**
 * Created by itclub21 on 12/7/2017.
 */

public enum MessageItemType {
    INCOMING_MEDIA, //0
    INCOMING_TEXT, //1
    OUTGOING_MEDIA, //2
    OUTGOING_TEXT, //3
    SPINNER, //4
    GENERAL_TEXT, //5
    GENERAL_OPTIONS, //6
    SEPARATE_DATE; //7

    public static final MessageItemType values[] = values();
}
