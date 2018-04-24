package com.supersoft.internusa.model;

/**
 * Created by itclub21 on 3/9/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetBalance {

    @SerializedName("response")
    @Expose
    private List<Response> response = null;

    public List<Response> getResponse() {
        return response;
    }

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
        @SerializedName("row")
        @Expose
        private Row row;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Row getRow() {
            return row;
        }

        public void setRow(Row row) {
            this.row = row;
        }

    }


    public class Row {

        @SerializedName("Nama")
        @Expose
        private String nama;
        @SerializedName("Kota")
        @Expose
        private String kota;
        @SerializedName("Cluster")
        @Expose
        private String cluster;
        @SerializedName("Saldo Deposit")
        @Expose
        private String saldoDeposit;
        @SerializedName("Bonus saat ini")
        @Expose
        private String bonusSaatIni;

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getKota() {
            return kota;
        }

        public void setKota(String kota) {
            this.kota = kota;
        }

        public String getCluster() {
            return cluster;
        }

        public void setCluster(String cluster) {
            this.cluster = cluster;
        }

        public String getSaldoDeposit() {
            return saldoDeposit;
        }

        public void setSaldoDeposit(String saldoDeposit) {
            this.saldoDeposit = saldoDeposit;
        }

        public String getBonusSaatIni() {
            return bonusSaatIni;
        }

        public void setBonusSaatIni(String bonusSaatIni) {
            this.bonusSaatIni = bonusSaatIni;
        }

    }
}
