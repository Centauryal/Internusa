package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/9/2017.
 */

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoResponseConfirm {
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

    /**
     *
     * @param response
     * The response
     */
    public void setResponse(List<Response> response) {
        this.response = response;
    }




    public class Response {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("isotp")
        @Expose
        @Nullable
        private Integer isotp;
        @SerializedName("data")
        @Expose
        private List<Datum> data = new ArrayList<Datum>();

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
         * The isotp
         */
        public Integer getIsotp() {
            return isotp;
        }

        /**
         *
         * @param isotp
         * The isotp
         */
        public void setIsotp(Integer isotp) {
            this.isotp = isotp;
        }

        /**
         *
         * @return
         * The data
         */
        public List<Datum> getData() {
            return data;
        }

        /**
         *
         * @param data
         * The data
         */
        public void setData(List<Datum> data) {
            this.data = data;
        }

    }





    public class Datum {

        @SerializedName("voucher")
        @Expose
        private String voucher;
        @SerializedName("msisdn")
        @Expose
        private String msisdn;
        @SerializedName("harga")
        @Expose
        private String harga;

        /**
         *
         * @return
         * The voucher
         */
        public String getVoucher() {
            return voucher;
        }

        /**
         *
         * @param voucher
         * The voucher
         */
        public void setVoucher(String voucher) {
            this.voucher = voucher;
        }

        /**
         *
         * @return
         * The msisdn
         */
        public String getMsisdn() {
            return msisdn;
        }

        /**
         *
         * @param msisdn
         * The msisdn
         */
        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
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

    }
}

