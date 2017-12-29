package com.jald.reserve.extension.bean.request;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 扫码下单
 */
public class ScannerOrderBean implements Serializable {
    private String tp_id;//客户TP_ID
    private String function;
    private String c_tp_id;
    private String zq_days;
    private String amount;
    private String sTpId;

    public void setsTpId(String sTpId) {
        this.sTpId = sTpId;
    }

    public String getsTpId() {
        return sTpId;
    }

    private ArrayList<ShoppingCarItem> goods_list;

    public String getTp_id() {
        return tp_id;
    }

    public void setTp_id(String tp_id) {
        this.tp_id = tp_id;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getC_tp_id() {
        return c_tp_id;
    }

    public void setC_tp_id(String c_tp_id) {
        this.c_tp_id = c_tp_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getZq_days() {
        return zq_days;
    }

    public void setZq_days(String zq_days) {
        this.zq_days = zq_days;
    }

    public ArrayList<ShoppingCarItem> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(ArrayList<ShoppingCarItem> goods_list) {
        this.goods_list = goods_list;
    }

    public static class ShoppingCarItem implements Serializable {
        private String pid;
        private String goods_title;
        private String price;
        private String goods_num;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getGoods_title() {
            return goods_title;
        }

        public void setGoods_title(String goods_title) {
            this.goods_title = goods_title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getGoods_num() {
            return goods_num;
        }

        public void setGoods_num(String goods_num) {
            this.goods_num = goods_num;
        }
    }
}
