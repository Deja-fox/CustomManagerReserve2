package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

// 新版商品订货
public class GoodsListResponseBeanV2 implements Serializable {

    private int total;
    private ArrayList<GoodsItem> list = new ArrayList<>();


    public static class GoodsItem implements Serializable {
        private String id;
        private String item_name;
        private String um_name;
        private double price;
        private String img_url;
        private String image_name;
        private String whse_qty;
        private String specification;
        private String days = "0";//支持的账期天数
        private String item_id;
        private String type_id;
        private String type_name;
        private String sort_letter;
        private String goods_zq_tit;//活动的标题
        // 额外字段,用户打算购买的数量
        private int purchaseCount = 0;


        public String getId() {
            return id;
        }

        public String getDays() {
            return days;
        }

        public String getGoods_zq_tit() {
            return goods_zq_tit;
        }

        public void setGoods_zq_tit(String goods_zq_tit) {
            this.goods_zq_tit = goods_zq_tit;
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

        public String getUm_name() {
            return um_name;
        }

        public void setUm_name(String um_name) {
            this.um_name = um_name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
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

        public String getWhse_qty() {
            return whse_qty;
        }

        public void setWhse_qty(String whse_qty) {
            this.whse_qty = whse_qty;
        }

        public String getSpecification() {
            return specification;
        }

        public void setSpecification(String specification) {
            this.specification = specification;
        }


        public int getPurchaseCount() {
            return purchaseCount;
        }

        public void setPurchaseCount(int purchaseCount) {
            this.purchaseCount = purchaseCount;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        public String getSort_letter() {
            return sort_letter;
        }

        public void setSort_letter(String sort_letter) {
            this.sort_letter = sort_letter;
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<GoodsItem> getList() {
        return list;
    }

    public void setList(ArrayList<GoodsItem> list) {
        this.list = list;
    }
}
