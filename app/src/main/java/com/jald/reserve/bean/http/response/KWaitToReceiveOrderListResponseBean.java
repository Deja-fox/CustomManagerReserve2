package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.ArrayList;

public class KWaitToReceiveOrderListResponseBean implements Serializable {

    private static final long serialVersionUID = -7156457244039163280L;

    private ArrayList<KWaitToReceiveOrderItem> list;

    public static class KWaitToReceiveOrderItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String co_opt_date_time;
        private String dist_opt_date_time;
        private String dist_operator;
        private String sign_opt_date_time;
        private String sign_operator;
        private String point_lng;
        private String point_lat;
        private String dist_time;
        private String dist_opt_tel;
        private String status_name;
        private String co_num;
        private String crt_date;
        private String born_date;
        private String iss_date;
        private String com_id;
        private String cust_id;
        private String status;
        private String pmt_status;
        private String qty;
        private String amt;
        private ArrayList<KWaitToReceiveOrderCommodityItem> items;

        // 下面的字段是额外的字段,非服务端返回的字段,标记是否已确认收货
        // 1 待收货模块相关字段
        private boolean isConfirmReceived = false;


        public String getCo_opt_date_time() {
            return co_opt_date_time;
        }

        public void setCo_opt_date_time(String co_opt_date_time) {
            this.co_opt_date_time = co_opt_date_time;
        }

        public String getDist_opt_date_time() {
            return dist_opt_date_time;
        }

        public void setDist_opt_date_time(String dist_opt_date_time) {
            this.dist_opt_date_time = dist_opt_date_time;
        }

        public String getDist_operator() {
            return dist_operator;
        }

        public void setDist_operator(String dist_operator) {
            this.dist_operator = dist_operator;
        }

        public String getSign_opt_date_time() {
            return sign_opt_date_time;
        }

        public void setSign_opt_date_time(String sign_opt_date_time) {
            this.sign_opt_date_time = sign_opt_date_time;
        }

        public String getSign_operator() {
            return sign_operator;
        }

        public void setSign_operator(String sign_operator) {
            this.sign_operator = sign_operator;
        }

        public String getPoint_lng() {
            return point_lng;
        }

        public void setPoint_lng(String point_lng) {
            this.point_lng = point_lng;
        }

        public String getPoint_lat() {
            return point_lat;
        }

        public void setPoint_lat(String point_lat) {
            this.point_lat = point_lat;
        }

        public String getDist_time() {
            return dist_time;
        }

        public void setDist_time(String dist_time) {
            this.dist_time = dist_time;
        }

        public String getDist_opt_tel() {
            return dist_opt_tel;
        }

        public void setDist_opt_tel(String dist_opt_tel) {
            this.dist_opt_tel = dist_opt_tel;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }

        public String getCo_num() {
            return co_num;
        }

        public void setCo_num(String co_num) {
            this.co_num = co_num;
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

        public String getIss_date() {
            return iss_date;
        }

        public void setIss_date(String iss_date) {
            this.iss_date = iss_date;
        }

        public String getCom_id() {
            return com_id;
        }

        public void setCom_id(String com_id) {
            this.com_id = com_id;
        }

        public String getCust_id() {
            return cust_id;
        }

        public void setCust_id(String cust_id) {
            this.cust_id = cust_id;
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

        public ArrayList<KWaitToReceiveOrderCommodityItem> getItems() {
            return items;
        }

        public void setItems(ArrayList<KWaitToReceiveOrderCommodityItem> items) {
            this.items = items;
        }

        public boolean isConfirmReceived() {
            return isConfirmReceived;
        }

        public void setIsConfirmReceived(boolean isConfirmReceived) {
            this.isConfirmReceived = isConfirmReceived;
        }

    }

    public static class KWaitToReceiveOrderCommodityItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String co_num;
        private String item_id;
        private String item_name;
        private String qty_ord;
        private String qty_req;
        private String price;
        private String qty_lmt;
        private String amt;

        public static long getSerialVersionUID() {
            return serialVersionUID;
        }

        public String getCo_num() {
            return co_num;
        }

        public void setCo_num(String co_num) {
            this.co_num = co_num;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getQty_ord() {
            return qty_ord;
        }

        public void setQty_ord(String qty_ord) {
            this.qty_ord = qty_ord;
        }

        public String getQty_req() {
            return qty_req;
        }

        public void setQty_req(String qty_req) {
            this.qty_req = qty_req;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getQty_lmt() {
            return qty_lmt;
        }

        public void setQty_lmt(String qty_lmt) {
            this.qty_lmt = qty_lmt;
        }

        public String getAmt() {
            return amt;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }
    }

    public ArrayList<KWaitToReceiveOrderItem> getList() {
        return list;
    }

    public void setList(ArrayList<KWaitToReceiveOrderItem> list) {
        this.list = list;
    }
}
