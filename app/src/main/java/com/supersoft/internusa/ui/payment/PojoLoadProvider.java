package com.supersoft.internusa.ui.payment;

/**
 * Created by itclub21 on 3/5/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PojoLoadProvider implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("row")
    @Expose
    private List<Row> row = new ArrayList<Row>();

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
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code= code;
    }

    /**
     *
     * @return
     * The row
     */
    public List<Row> getRow() {
        return row;
    }

    /**
     *
     * @param row
     * The row
     */
    public void setRow(List<Row> row) {
        this.row = row;
    }

    //parent data

    public class Row implements Serializable {

        @SerializedName("nama")
        @Expose
        private String nama;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("child")
        @Expose
        private List<Child> child = new ArrayList<Child>();

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
         * The code
         */
        public String getCode() {
            return code;
        }

        /**
         *
         * @param code
         * The code
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         *
         * @return
         * The child
         */
        public List<Child> getChild() {
            return child;
        }

        /**
         *
         * @param child
         * The child
         */
        public void setChild(List<Child> child) {
            this.child = child;
        }

    }


    // child....

    public class Child implements Serializable{

        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("ada")
        @Expose
        private String ada;
        @SerializedName("opr")
        @Expose
        private String opr;
        /**
         *
         * @return
         * The code
         */
        public String getCode() {
            return code;
        }

        /**
         *
         * @param code
         * The code
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         *
         * @return
         * The opr
         */
        public String getOpr() {
            return opr;
        }

        /**
         *
         * @param opr
         * The opr
         */
        public void setOpr(String opr) {
            this.opr= opr;
        }

        /**
         *
         * @return
         * The name
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param name
         * The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         * The ada
         */
        public String getAda() {
            return ada;
        }

        /**
         *
         * @param ada
         * The ada
         */
        public void setAda(String ada) {
            this.ada = ada;
        }

    }
}
