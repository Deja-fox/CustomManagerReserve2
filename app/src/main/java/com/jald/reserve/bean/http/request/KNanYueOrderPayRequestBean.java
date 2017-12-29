package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KNanYueOrderPayRequestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String co_num;
    private String com_id;
    private String pay_mode;
    private String days;
    private String amt;
    private String password;
    private String sms_code;
    // 额外商品信息
    private String com_name;

    public String getCo_num() {
        return co_num;
    }

    public void setCo_num(String co_num) {
        this.co_num = co_num;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getCom_id() {
        return com_id;
    }

    public void setCom_id(String com_id) {
        this.com_id = com_id;
    }

    public String getPay_mode() {
        return pay_mode;
    }

    public void setPay_mode(String pay_mode) {
        this.pay_mode = pay_mode;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
    }
}
