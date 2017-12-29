package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 协议列表返回Bean
 */
public class AgreementListResponseBean implements Serializable {
    private String ret_code;
    private String ret_msg;
    private ArrayList<AgreementBean> data;

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public ArrayList<AgreementBean> getData() {
        return data;
    }

    public void setData(ArrayList<AgreementBean> data) {
        this.data = data;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public static class AgreementBean implements Serializable {
        private String id;//协议id
        private String store_id;
        private String title;//名称
        private String content;// 描述
        private String goods_content;
        private String start_time;
        private String end_time;
        private String payment;
        private String tactics_id;
        private String c_type;//期货标识
        private String min_price;
        private String max_price;//总额
        private String c_content;//月数
        private String zqfk_code;
        private String privacy;
        private String status;
        private String is_front_show;
        private String view_order;
        private String add_time;
        private String update_time;
        private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> feature_goods;

        public String getTactics_id() {
            return tactics_id;
        }

        public void setTactics_id(String tactics_id) {
            this.tactics_id = tactics_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getGoods_content() {
            return goods_content;
        }

        public void setGoods_content(String goods_content) {
            this.goods_content = goods_content;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getC_type() {
            return c_type;
        }

        public void setC_type(String c_type) {
            this.c_type = c_type;
        }

        public String getMin_price() {
            return min_price;
        }

        public void setMin_price(String min_price) {
            this.min_price = min_price;
        }

        public String getMax_price() {
            return max_price;
        }

        public void setMax_price(String max_price) {
            this.max_price = max_price;
        }

        public String getC_content() {
            return c_content;
        }

        public void setC_content(String c_content) {
            this.c_content = c_content;
        }

        public String getZqfk_code() {
            return zqfk_code;
        }

        public void setZqfk_code(String zqfk_code) {
            this.zqfk_code = zqfk_code;
        }

        public String getPrivacy() {
            return privacy;
        }

        public void setPrivacy(String privacy) {
            this.privacy = privacy;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_front_show() {
            return is_front_show;
        }

        public void setIs_front_show(String is_front_show) {
            this.is_front_show = is_front_show;
        }

        public String getView_order() {
            return view_order;
        }

        public void setView_order(String view_order) {
            this.view_order = view_order;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public ArrayList<AgreementGoodsBean> getFeature_goods() {
            return feature_goods;
        }

        public void setFeature_goods(ArrayList<AgreementGoodsBean> feature_goods) {
            this.feature_goods = feature_goods;
        }

        public static class AgreementGoodsBean implements Serializable {
            private String id;
            private String market_id;
            private String goods_id;// 商品id
            private String store_id;
            private String goods_num;//数量
            private String max_num;
            private String gift_num;
            private String sell_num;
            private String goods_price;//价格
            private String goods_storage_id;
            private String user_id;
            private String type;
            private String is_sale;
            private String zqfk_id;
            private String category_id;
            private String title;// 名称
            private String special_title;
            private String intro;
            private String store_category_id;
            private String cover;
            private String stock_num;
            private String manufacturer_id;
            private String brand_id;
            private String specification;// 规格
            private String unit_uuid;
            private String goods_unit;//单位
            private String bar_code;
            private String special_code;
            private String price;
            private String origin_price;
            private String price_sell_limit;
            private String content;
            private String privacy;
            private String status;
            private String hot;
            private String view_order;
            private String add_time;
            private String update_time;
            private String sell_count;
            private String item_id;
            private String pri_wsale;
            private String box_bar;
            private String is_active;
            private String fact_name;
            private String source;
            private String goods_uuid;
            private String qd_market_type;
            private String surplus;
            // 额外字段,用户打算购买的数量
            private int purchaseCount = 0;

            public int getPurchaseCount() {
                return purchaseCount;
            }

            public void setPurchaseCount(int purchaseCount) {
                this.purchaseCount = purchaseCount;
            }

            public String getSurplus() {
                return surplus;
            }

            public void setSurplus(String surplus) {
                this.surplus = surplus;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMarket_id() {
                return market_id;
            }

            public void setMarket_id(String market_id) {
                this.market_id = market_id;
            }

            public String getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(String goods_id) {
                this.goods_id = goods_id;
            }

            public String getStore_id() {
                return store_id;
            }

            public void setStore_id(String store_id) {
                this.store_id = store_id;
            }

            public String getGoods_num() {
                return goods_num;
            }

            public void setGoods_num(String goods_num) {
                this.goods_num = goods_num;
            }

            public String getMax_num() {
                return max_num;
            }

            public void setMax_num(String max_num) {
                this.max_num = max_num;
            }

            public String getGift_num() {
                return gift_num;
            }

            public void setGift_num(String gift_num) {
                this.gift_num = gift_num;
            }

            public String getSell_num() {
                return sell_num;
            }

            public void setSell_num(String sell_num) {
                this.sell_num = sell_num;
            }

            public String getGoods_price() {
                return goods_price;
            }

            public void setGoods_price(String goods_price) {
                this.goods_price = goods_price;
            }

            public String getGoods_storage_id() {
                return goods_storage_id;
            }

            public void setGoods_storage_id(String goods_storage_id) {
                this.goods_storage_id = goods_storage_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getIs_sale() {
                return is_sale;
            }

            public void setIs_sale(String is_sale) {
                this.is_sale = is_sale;
            }

            public String getZqfk_id() {
                return zqfk_id;
            }

            public void setZqfk_id(String zqfk_id) {
                this.zqfk_id = zqfk_id;
            }

            public String getCategory_id() {
                return category_id;
            }

            public void setCategory_id(String category_id) {
                this.category_id = category_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSpecial_title() {
                return special_title;
            }

            public void setSpecial_title(String special_title) {
                this.special_title = special_title;
            }

            public String getIntro() {
                return intro;
            }

            public void setIntro(String intro) {
                this.intro = intro;
            }

            public String getStore_category_id() {
                return store_category_id;
            }

            public void setStore_category_id(String store_category_id) {
                this.store_category_id = store_category_id;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getStock_num() {
                return stock_num;
            }

            public void setStock_num(String stock_num) {
                this.stock_num = stock_num;
            }

            public String getManufacturer_id() {
                return manufacturer_id;
            }

            public void setManufacturer_id(String manufacturer_id) {
                this.manufacturer_id = manufacturer_id;
            }

            public String getBrand_id() {
                return brand_id;
            }

            public void setBrand_id(String brand_id) {
                this.brand_id = brand_id;
            }

            public String getSpecification() {
                return specification;
            }

            public void setSpecification(String specification) {
                this.specification = specification;
            }

            public String getUnit_uuid() {
                return unit_uuid;
            }

            public void setUnit_uuid(String unit_uuid) {
                this.unit_uuid = unit_uuid;
            }

            public String getGoods_unit() {
                return goods_unit;
            }

            public void setGoods_unit(String goods_unit) {
                this.goods_unit = goods_unit;
            }

            public String getBar_code() {
                return bar_code;
            }

            public void setBar_code(String bar_code) {
                this.bar_code = bar_code;
            }

            public String getSpecial_code() {
                return special_code;
            }

            public void setSpecial_code(String special_code) {
                this.special_code = special_code;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getOrigin_price() {
                return origin_price;
            }

            public void setOrigin_price(String origin_price) {
                this.origin_price = origin_price;
            }

            public String getPrice_sell_limit() {
                return price_sell_limit;
            }

            public void setPrice_sell_limit(String price_sell_limit) {
                this.price_sell_limit = price_sell_limit;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPrivacy() {
                return privacy;
            }

            public void setPrivacy(String privacy) {
                this.privacy = privacy;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getHot() {
                return hot;
            }

            public void setHot(String hot) {
                this.hot = hot;
            }

            public String getView_order() {
                return view_order;
            }

            public void setView_order(String view_order) {
                this.view_order = view_order;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public String getSell_count() {
                return sell_count;
            }

            public void setSell_count(String sell_count) {
                this.sell_count = sell_count;
            }

            public String getItem_id() {
                return item_id;
            }

            public void setItem_id(String item_id) {
                this.item_id = item_id;
            }

            public String getPri_wsale() {
                return pri_wsale;
            }

            public void setPri_wsale(String pri_wsale) {
                this.pri_wsale = pri_wsale;
            }

            public String getBox_bar() {
                return box_bar;
            }

            public void setBox_bar(String box_bar) {
                this.box_bar = box_bar;
            }

            public String getIs_active() {
                return is_active;
            }

            public void setIs_active(String is_active) {
                this.is_active = is_active;
            }

            public String getFact_name() {
                return fact_name;
            }

            public void setFact_name(String fact_name) {
                this.fact_name = fact_name;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getGoods_uuid() {
                return goods_uuid;
            }

            public void setGoods_uuid(String goods_uuid) {
                this.goods_uuid = goods_uuid;
            }

            public String getQd_market_type() {
                return qd_market_type;
            }

            public void setQd_market_type(String qd_market_type) {
                this.qd_market_type = qd_market_type;
            }
        }
    }

}
