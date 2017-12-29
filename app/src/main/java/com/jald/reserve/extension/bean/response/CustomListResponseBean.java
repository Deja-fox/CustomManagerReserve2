package com.jald.reserve.extension.bean.response;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomListResponseBean implements Serializable {
    private String mtpId;
    private String stpId;
    private int total;
    private ArrayList<KCustomBean> list;

    public CustomListResponseBean() {

    }

    public String getMtpId() {
        return mtpId;
    }

    public void setMtpId(String mtpId) {
        this.mtpId = mtpId;
    }

    public String getStpId() {
        return stpId;
    }

    public void setStpId(String stpId) {
        this.stpId = stpId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<KCustomBean> getList() {
        return list;
    }

    public void setList(ArrayList<KCustomBean> list) {
        this.list = list;
    }

    public static class KCustomBean implements Serializable {
        private String tp_id;
        private String lice_id;
        private String cust_name;
        private String manager;
        private String tel;
        private String address;
        private String isCredit;

        //新增
        private String mobile;
        private String store_title;
        private String principal;
        private String from;

        // 额外(排序字母)
        private String sort_letter;

        public KCustomBean() {
        }

        public String getPrincipal() {
            return principal;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getStore_title() {
            return store_title;
        }

        public void setStore_title(String store_title) {
            this.store_title = store_title;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getIsCredit() {
            return isCredit;
        }

        public void setIsCredit(String isCredit) {
            this.isCredit = isCredit;
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

        public String getCust_name() {
            return cust_name;
        }

        public void setCust_name(String cust_name) {
            this.cust_name = cust_name;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSort_letter() {
            return sort_letter;
        }

        public void setSort_letter(String sort_letter) {
            this.sort_letter = sort_letter;
        }

    }

}
