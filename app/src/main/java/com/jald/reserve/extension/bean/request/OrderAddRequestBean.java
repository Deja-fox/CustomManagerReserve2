package com.jald.reserve.extension.bean.request;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderAddRequestBean implements Serializable {
    private String tp_id;//客户TP_ID
    private String stpId;
    private String amt;
    private String user_feature_id;
    private ArrayList<ShoppingCarItem> items;

    public String getUser_feature_id() {
        return user_feature_id;
    }

    public void setStpId(String stpId) {
        this.stpId = stpId;
    }

    public String getStpId() {
        return stpId;
    }

    public void setUser_feature_id(String user_feature_id) {
        this.user_feature_id = user_feature_id;
    }

    public String getTp_id() {
        return tp_id;
    }

    public void setTp_id(String tp_id) {
        this.tp_id = tp_id;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public ArrayList<ShoppingCarItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingCarItem> items) {
        this.items = items;
    }

    public static class ShoppingCarItem implements Serializable {
        private String id;
        private String item_name;
        private String price;
        private String qty_ord;
        private String amt;
        private String days;

        // 额外
        private transient String img_url;
        private transient String image_name;
        private transient String um_name;

        public String getId() {
            return id;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getPrice() {
            return price;
        }

        public String getUm_name() {
            return um_name;
        }

        public void setUm_name(String um_name) {
            this.um_name = um_name;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getQty_ord() {
            return qty_ord;
        }

        public void setQty_ord(String qty_ord) {
            this.qty_ord = qty_ord;
        }

        public String getAmt() {
            return amt;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getImage_name() {
            return image_name;
        }

        public void setImage_name(String image_name) {
            this.image_name = image_name;
        }
    }
}
