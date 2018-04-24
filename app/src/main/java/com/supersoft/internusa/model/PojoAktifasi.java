package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itclub21 on 10/7/2016.
 */
public class PojoAktifasi {
    @SerializedName("response")
    @Expose
    private List<Response> response = new ArrayList<Response>();

    /**
     *
     * @return
     * The response
     */
    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }


    public class Response{
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("agenid")
        @Expose
        private String agenid;
        @SerializedName("hp")
        @Expose
        public String hp;
        @SerializedName("pin")
        @Expose
        public String pin;

        /**
         *
         * @return
         * The status
         */
        public Integer getStatus() {
            return status;
        }

        /**
         *
         * @param status
         * The status
         */
        public void setStatus(Integer status) {
            this.status = status;
        }

        /**
         *
         * @return
         * The message
         */
        public String getMessage() {
            return message;
        }

        /**
         *
         * @param message
         * The message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         *
         * @return
         * The agenid
         */
        public String getAgenid() {
            return agenid;
        }

        /**
         *
         * @param agenid
         * The agenid
         */
        public void setAgenid(String agenid) {
            this.agenid = agenid;
        }
    }
}
