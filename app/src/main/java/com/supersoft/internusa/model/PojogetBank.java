package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/9/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojogetBank {
    @SerializedName("nominal")
    @Expose
    private String nominal;

    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<Datum>();


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


    /**
     *
     * @return
     * The nominal
     */
    public String getNominal() {
        return nominal;
    }

    /**
     *
     * @param nominal
     * The nominal
     */
    public void setNominal(String nominal) {
        this.nominal = nominal;
    }



    public class Datum {

        @SerializedName("bank")
        @Expose
        private String bank;
        @SerializedName("nama")
        @Expose
        private String nama;
        @SerializedName("rek")
        @Expose
        private String rek;

        @SerializedName("message")
        @Expose
        private String message;

        /**
         *
         * @return
         * The bank
         */
        public String getBank() {
            return bank;
        }

        /**
         *
         * @param bank
         * The bank
         */
        public void setBank(String bank) {
            this.bank = bank;
        }

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
         * The rek
         */
        public String getRek() {
            return rek;
        }

        /**
         *
         * @param rek
         * The rek
         */
        public void setRek(String rek) {
            this.rek = rek;
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

    }
}
