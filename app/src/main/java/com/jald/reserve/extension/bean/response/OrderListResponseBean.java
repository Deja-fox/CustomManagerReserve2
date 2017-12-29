package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListResponseBean implements Serializable {

    private int total;
    private int page_sum;
    private ArrayList<OrderBean> list;

    public static class OrderBean implements Serializable {
        private String co_num;
        private String is_confirm;
        private String crt_date;
        private String born_date;
        private String status;
        private String pmt_status;
        private String qty;
        private String amt;
        private String com_tp_id;
        private String com_name;
        private String days;
        private String tp_id;
        private String lice_id;
        private String manager;
        private String address;
        private String tel;
        private String cust_name;
        private String isZq;
        private String is_feature;//是否期货 0不是 12是

        private ArrayList<GoodsBean> items;
        private FuturesBean user_feature;

        public static class GoodsBean implements Serializable {
            private String id;
            private String item_name;
            private String original_price;
            private String qty_ord;
            private String price;
            private String amt;
            private String specification;
            private String img_url;
            private String image_name;
            private String um_name;

            public String getUm_name() {
                return um_name;
            }

            public void setUm_name(String um_name) {
                this.um_name = um_name;
            }

            public String getId() {
                return id;
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

            public String getOriginal_price() {
                return original_price;
            }

            public void setOriginal_price(String original_price) {
                this.original_price = original_price;
            }

            public String getQty_ord() {
                return qty_ord;
            }

            public void setQty_ord(String qty_ord) {
                this.qty_ord = qty_ord;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getAmt() {
                return amt;
            }

            public void setAmt(String amt) {
                this.amt = amt;
            }

            public String getSpecification() {
                return specification;
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

            public void setSpecification(String specification) {
                this.specification = specification;
            }
        }

        public static class FuturesBean implements Serializable {
            private String Id;
            private String Feature_id;
            private String Client_manager_tp_id;
            private String User_tp_id;
            private String Feature_price;
            private String Feature_completed_price;
            private String Type;
            private String Status;
            private String Add_time;
            private String Update_time;
            private String Agree_time;
            private String Feature_month;

            public String getId() {
                return Id;
            }

            public void setId(String id) {
                Id = id;
            }

            public String getFeature_id() {
                return Feature_id;
            }

            public void setFeature_id(String feature_id) {
                Feature_id = feature_id;
            }

            public String getClient_manager_tp_id() {
                return Client_manager_tp_id;
            }

            public void setClient_manager_tp_id(String client_manager_tp_id) {
                Client_manager_tp_id = client_manager_tp_id;
            }

            public String getUser_tp_id() {
                return User_tp_id;
            }

            public void setUser_tp_id(String user_tp_id) {
                User_tp_id = user_tp_id;
            }

            public String getFeature_price() {
                return Feature_price;
            }

            public void setFeature_price(String feature_price) {
                Feature_price = feature_price;
            }

            public String getFeature_completed_price() {
                return Feature_completed_price;
            }

            public void setFeature_completed_price(String feature_completed_price) {
                Feature_completed_price = feature_completed_price;
            }

            public String getType() {
                return Type;
            }

            public void setType(String type) {
                Type = type;
            }

            public String getStatus() {
                return Status;
            }

            public void setStatus(String status) {
                Status = status;
            }

            public String getAdd_time() {
                return Add_time;
            }

            public void setAdd_time(String add_time) {
                Add_time = add_time;
            }

            public String getUpdate_time() {
                return Update_time;
            }

            public void setUpdate_time(String update_time) {
                Update_time = update_time;
            }

            public String getAgree_time() {
                return Agree_time;
            }

            public void setAgree_time(String agree_time) {
                Agree_time = agree_time;
            }

            public String getFeature_month() {
                return Feature_month;
            }

            public void setFeature_month(String feature_month) {
                Feature_month = feature_month;
            }
        }

        public FuturesBean getUser_feature() {
            return user_feature;
        }

        public void setUser_feature(FuturesBean user_feature) {
            this.user_feature = user_feature;
        }

        public String getIs_feature() {
            return is_feature;
        }

        public void setIs_feature(String is_feature) {
            this.is_feature = is_feature;
        }

        public String getCo_num() {
            return co_num;
        }

        public String getCust_name() {
            return cust_name;
        }

        public void setCust_name(String cust_name) {
            this.cust_name = cust_name;
        }

        public void setCo_num(String co_num) {
            this.co_num = co_num;
        }

        public String getIs_confirm() {
            return is_confirm;
        }

        public void setIs_confirm(String is_confirm) {
            this.is_confirm = is_confirm;
        }

        public String getCrt_date() {
            return crt_date;
        }

        public void setCrt_date(String crt_date) {
            this.crt_date = crt_date;
        }

        public String getBorn_date() {
            return born_date;
        }

        public void setBorn_date(String born_date) {
            this.born_date = born_date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPmt_status() {
            return pmt_status;
        }

        public void setPmt_status(String pmt_status) {
            this.pmt_status = pmt_status;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getAmt() {
            return amt;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public String getCom_tp_id() {
            return com_tp_id;
        }

        public void setCom_tp_id(String com_tp_id) {
            this.com_tp_id = com_tp_id;
        }

        public String getCom_name() {
            return com_name;
        }

        public void setCom_name(String com_name) {
            this.com_name = com_name;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getTp_id() {
            return tp_id;
        }

        public void setTp_id(String tp_id) {
            this.tp_id = tp_id;
        }

        public String getLice_id() {
            return lice_id;
        }

        public void setLice_id(String lice_id) {
            this.lice_id = lice_id;
        }

        public String getIsZq() {
            return isZq;
        }

        public void setIsZq(String isZq) {
            this.isZq = isZq;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public ArrayList<GoodsBean> getItems() {
            return items;
        }

        public void setItems(ArrayList<GoodsBean> items) {
            this.items = items;
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage_sum() {
        return page_sum;
    }

    public void setPage_sum(int page_sum) {
        this.page_sum = page_sum;
    }

    public ArrayList<OrderBean> getList() {
        return list;
    }

    public void setList(ArrayList<OrderBean> list) {
        this.list = list;
    }
}
