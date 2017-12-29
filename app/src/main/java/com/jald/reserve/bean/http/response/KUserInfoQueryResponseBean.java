package com.jald.reserve.bean.http.response;

import java.io.Serializable;

public class KUserInfoQueryResponseBean implements Serializable {

    private String uuid;
    private String cust_name;
    private String id_number;
    private String manager;
    private String telephone;
    private String co_num;
    private String born_date;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCo_num() {
        return co_num;
    }

    public void setCo_num(String co_num) {
        this.co_num = co_num;
    }

    public String getBorn_date() {
        return born_date;
    }

    public void setBorn_date(String born_date) {
        this.born_date = born_date;
    }
}
