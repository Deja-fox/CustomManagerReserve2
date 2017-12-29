package com.jald.reserve.bean.http.response;

public class KAccountInfoResponseBean {

    private String authorize_amt;
    private String effective_amt;
    private String balance;
    private String generation_amt;
    private String nan_yue_service_amt;


    public String getAuthorize_amt() {
        return authorize_amt;
    }

    public void setAuthorize_amt(String authorize_amt) {
        this.authorize_amt = authorize_amt;
    }

    public String getEffective_amt() {
        return effective_amt;
    }

    public void setEffective_amt(String effective_amt) {
        this.effective_amt = effective_amt;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getGeneration_amt() {
        return generation_amt;
    }

    public void setGeneration_amt(String generation_amt) {
        this.generation_amt = generation_amt;
    }

    public String getNan_yue_service_amt() {
        return nan_yue_service_amt;
    }

    public void setNan_yue_service_amt(String nan_yue_service_amt) {
        this.nan_yue_service_amt = nan_yue_service_amt;
    }
}
