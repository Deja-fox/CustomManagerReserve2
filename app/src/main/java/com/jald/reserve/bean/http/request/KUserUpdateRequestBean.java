package com.jald.reserve.bean.http.request;

public class KUserUpdateRequestBean {

    private String id_number;
    private String manager;
    private String cust_id;
    private String cust_name;
    private String lice_id;
    private String login_name;
    private String tel;
    private String is_reg;
    private String pay_pwd;
    private String is_reg_nanyue;

    public String getIs_reg_nanyue() {
        return is_reg_nanyue;
    }

    public void setIs_reg_nanyue(String is_reg_nanyue) {
        this.is_reg_nanyue = is_reg_nanyue;
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

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getLice_id() {
        return lice_id;
    }

    public void setLice_id(String lice_id) {
        this.lice_id = lice_id;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIs_reg() {
        return is_reg;
    }

    public void setIs_reg(String is_reg) {
        this.is_reg = is_reg;
    }

    public String getPay_pwd() {
        return pay_pwd;
    }

    public void setPay_pwd(String pay_pwd) {
        this.pay_pwd = pay_pwd;
    }

}
