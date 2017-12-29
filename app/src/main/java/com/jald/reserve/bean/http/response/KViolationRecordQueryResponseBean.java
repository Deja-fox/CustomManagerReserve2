package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;


public class KViolationRecordQueryResponseBean implements Serializable {
    private static final long serialVersionUID = -2379989538887157847L;

    private String total;
    private List<KViolationRecordBean> list;

    public static class KViolationRecordBean implements Serializable {
        private static final long serialVersionUID = -3593400782116856073L;
        private String temp_id;
        private String regulation_id;
        private String city;
        private String province_id;
        private String city_id;
        private String city_area_id;
        private String regulation_sn;
        private String regulation_name;
        private String fine_amount;
        private String pay_charge;
        private String porint;
        private String violation_sn;
        private String violation_road;
        private String violation_time;
        private String reference;
        private String authority;
        private String dealaddress;
        private String needdays;
        private String deal_id;
        private String is_on_site_single;
        private String errror_msg;
        private String special_charge;

        // 额外字段,标志该项是否选中
        private transient boolean isSelected = false;

        public String getTemp_id() {
            return temp_id;
        }

        public void setTemp_id(String temp_id) {
            this.temp_id = temp_id;
        }

        public String getRegulation_id() {
            return regulation_id;
        }

        public void setRegulation_id(String regulation_id) {
            this.regulation_id = regulation_id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCity_area_id() {
            return city_area_id;
        }

        public void setCity_area_id(String city_area_id) {
            this.city_area_id = city_area_id;
        }

        public String getRegulation_sn() {
            return regulation_sn;
        }

        public void setRegulation_sn(String regulation_sn) {
            this.regulation_sn = regulation_sn;
        }

        public String getRegulation_name() {
            return regulation_name;
        }

        public void setRegulation_name(String regulation_name) {
            this.regulation_name = regulation_name;
        }

        public String getFine_amount() {
            return fine_amount;
        }

        public void setFine_amount(String fine_amount) {
            this.fine_amount = fine_amount;
        }

        public String getPay_charge() {
            return pay_charge;
        }

        public void setPay_charge(String pay_charge) {
            this.pay_charge = pay_charge;
        }

        public String getPorint() {
            return porint;
        }

        public void setPorint(String porint) {
            this.porint = porint;
        }

        public String getViolation_sn() {
            return violation_sn;
        }

        public void setViolation_sn(String violation_sn) {
            this.violation_sn = violation_sn;
        }

        public String getViolation_road() {
            return violation_road;
        }

        public void setViolation_road(String violation_road) {
            this.violation_road = violation_road;
        }

        public String getViolation_time() {
            return violation_time;
        }

        public void setViolation_time(String violation_time) {
            this.violation_time = violation_time;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

        public String getDealaddress() {
            return dealaddress;
        }

        public void setDealaddress(String dealaddress) {
            this.dealaddress = dealaddress;
        }

        public String getNeeddays() {
            return needdays;
        }

        public void setNeeddays(String needdays) {
            this.needdays = needdays;
        }

        public String getDeal_id() {
            return deal_id;
        }

        public void setDeal_id(String deal_id) {
            this.deal_id = deal_id;
        }

        public String getIs_on_site_single() {
            return is_on_site_single;
        }

        public void setIs_on_site_single(String is_on_site_single) {
            this.is_on_site_single = is_on_site_single;
        }

        public String getErrror_msg() {
            return errror_msg;
        }

        public void setErrror_msg(String errror_msg) {
            this.errror_msg = errror_msg;
        }

        public String getSpecial_charge() {
            return special_charge;
        }

        public void setSpecial_charge(String special_charge) {
            this.special_charge = special_charge;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<KViolationRecordBean> getList() {
        return list;
    }

    public void setList(List<KViolationRecordBean> list) {
        this.list = list;
    }

}
