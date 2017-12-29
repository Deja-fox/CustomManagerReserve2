package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.ArrayList;

public class KBaiTiaoAndReachPayTransListResponseBean implements Serializable {
    private static final long serialVersionUID = -8341557138780384956L;

    private int total;
    private int page_sum;
    private ArrayList<KBaiTiaoAndReachPayTransItem> list;

    public static class KBaiTiaoAndReachPayTransItem implements Serializable {
        private static final long serialVersionUID = -8341557138780384956L;

        private String trans_id;
        private String amt;
        private String trans_no;
        private String co_num;
        private String com_tp_id;
        private String trans_type;
        private String last_process_date;


        public String getTrans_id() {

            return trans_id;
        }

        public void setTrans_id(String trans_id) {
            this.trans_id = trans_id;
        }

        public String getAmt() {
            return amt;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public String getTrans_no() {
            return trans_no;
        }

        public void setTrans_no(String trans_no) {
            this.trans_no = trans_no;
        }

        public String getCo_num() {
            return co_num;
        }

        public void setCo_num(String co_num) {
            this.co_num = co_num;
        }

        public String getCom_tp_id() {
            return com_tp_id;
        }

        public void setCom_tp_id(String com_tp_id) {
            this.com_tp_id = com_tp_id;
        }

        public String getTrans_type() {
            return trans_type;
        }

        public void setTrans_type(String trans_type) {
            this.trans_type = trans_type;
        }

        public String getLast_process_date() {
            return last_process_date;
        }

        public void setLast_process_date(String last_process_date) {
            this.last_process_date = last_process_date;
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

    public ArrayList<KBaiTiaoAndReachPayTransItem> getList() {
        return list;
    }

    public void setList(ArrayList<KBaiTiaoAndReachPayTransItem> list) {
        this.list = list;
    }
}

