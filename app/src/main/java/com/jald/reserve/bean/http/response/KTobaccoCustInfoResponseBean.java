package com.jald.reserve.bean.http.response;

import java.io.Serializable;

public class KTobaccoCustInfoResponseBean implements Serializable {
    private static final long serialVersionUID = -8341557138780384956L;

    private String sale_amt;
    private String last_order_amt;
    private String authorize_amt;
    private String effective_amt;
    private String balance;
    private String generation_amt;
    private String nan_yue_service_amt;
    private String out_of_stock_count;
    private String to_be_paid_count;
    private String whse_amt;
    private String cust_level;
    private String order_begin_date_time;
    private String order_end_date_time;
    private String is_order;
    private String to_be_harvested_count;

    public String getTo_be_harvested_count() {
        return to_be_harvested_count;
    }

    public void setTo_be_harvested_count(String to_be_harvested_count) {
        this.to_be_harvested_count = to_be_harvested_count;
    }

    public String getSale_amt() {
        return sale_amt;
    }

    public void setSale_amt(String sale_amt) {
        this.sale_amt = sale_amt;
    }

    public String getLast_order_amt() {
        return last_order_amt;
    }

    public void setLast_order_amt(String last_order_amt) {
        this.last_order_amt = last_order_amt;
    }

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

    public String getOut_of_stock_count() {
        return out_of_stock_count;
    }

    public void setOut_of_stock_count(String out_of_stock_count) {
        this.out_of_stock_count = out_of_stock_count;
    }

    public String getTo_be_paid_count() {
        return to_be_paid_count;
    }

    public void setTo_be_paid_count(String to_be_paid_count) {
        this.to_be_paid_count = to_be_paid_count;
    }

    public String getCust_level() {
        return cust_level;
    }

    public void setCust_level(String cust_level) {
        this.cust_level = cust_level;
    }

    public String getWhse_amt() {
        return whse_amt;
    }

    public void setWhse_amt(String whse_amt) {
        this.whse_amt = whse_amt;
    }

    public String getOrder_begin_date_time() {
        return order_begin_date_time;
    }

    public void setOrder_begin_date_time(String order_begin_date_time) {
        this.order_begin_date_time = order_begin_date_time;
    }

    public String getOrder_end_date_time() {
        return order_end_date_time;
    }

    public void setOrder_end_date_time(String order_end_date_time) {
        this.order_end_date_time = order_end_date_time;
    }

    public boolean getCanOrder() {
        if (this.is_order == null || is_order.equals("")) {
            return false;
        } else if (is_order.equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    public void setIs_order(String is_order) {
        this.is_order = is_order;
    }
}
