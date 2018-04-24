package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 2/2/2017.
 */

public class MsgEvent {

    private String key;
    private String phonenumber;
    private Integer position;
    private Object object;
    private Object object1;

    public MsgEvent(int position, String key)
    {
        this.position = position;
        this.key = key;
    }

    public MsgEvent(int position, String key, String phone)
    {
        this.position = position;
        this.key = key;
        this.phonenumber = phone;
    }

    public MsgEvent(int position, String key, Object obj)
    {
        this.position = position;
        this.key = key;
        this.object = obj;
    }

    public MsgEvent(int position, String key, Object obj, Object obj1)
    {
        this.position = position;
        this.key = key;
        this.object = obj;
        this.object1 = obj1;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getKey(){return this.key;}

    public void setPhonenumber(String key)
    {
        this.phonenumber = key;
    }

    public String getPhonenumber(){return this.phonenumber;}

    public void setPosition(int position)
    {
        this.position = position;
    }

    public Integer getPosition(){return this.position;}

    public void setObject(Object position)
    {
        this.object = position;
    }

    public Object getObject(){return this.object;}

    public void setRowObject(Object position)
    {
        this.object1 = position;
    }

    public Object getRowObject(){return this.object1;}


}
