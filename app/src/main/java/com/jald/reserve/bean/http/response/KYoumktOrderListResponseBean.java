package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KYoumktOrderListResponseBean implements Serializable {

    private static final long serialVersionUID = -7156457244039163280L;
    private int page_sum;
    private int total;
    private ArrayList<KYoumktOrderItem> list;

    public static class KYoumktOrderItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String co_num;
        private String crt_date;
        private String born_date;
        private String iss_date;
        private String status;
        private String pmt_status;
        private String qty;
        private String amt;
        private String is_pay;
        private String com_tp_id;
        private String com_name;
        private String days;
        private List<KYoumktCommodityItem> items;

        public static class KYoumktCommodityItem implements Serializable {
            private static final long serialVersionUID = 1L;

            private String item_id;
            private String item_name;
            private String qty_ord;
            private String qty_req;
            private String price;
            private String qty_lmt;
            private String amt;

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

        public String getIs_pay() {
            return is_pay;
        }

        public void setIs_pay(String is_pay) {
            this.is_pay = is_pay;
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

        public List<KYoumktCommodityItem> getItems() {
            return items;
        }

        public void setItems(List<KYoumktCommodityItem> items) {
            this.items = items;
        }
    }

    public int getPage_sum() {
        return page_sum;
    }

    public void setPage_sum(int page_sum) {
        this.page_sum = page_sum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<KYoumktOrderItem> getList() {
        return list;
    }

    public void setList(ArrayList<KYoumktOrderItem> list) {
        this.list = list;
    }
}
