package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.ArrayList;

public class KNanYueLoanListResponseBean implements Serializable {

    private int total;
    private int page_sum;
    private ArrayList<KNanYueLoanItem> list;

    public static class KNanYueLoanItem implements Serializable {
        private String loan_id;
        private String account_no;
        private String loan_amt;
        private String total_balance;
        private String trans_time;
        private String loan_status;
        private String expiration_date;
        private String amount_repayment;
        private String interest_repayment;
        private String expired_interest_repayment;
        private String compound_interest_repayment;
        private String amount_balance;
        private String interest_balance;
        private String expired_interest_balance;
        private String compound_interest_balance;
        private String total_fee_hs;

        public String getLoan_id() {
            return loan_id;
        }

        public void setLoan_id(String loan_id) {
            this.loan_id = loan_id;
        }

        public String getAccount_no() {
            return account_no;
        }

        public void setAccount_no(String account_no) {
            this.account_no = account_no;
        }

        public String getLoan_amt() {
            return loan_amt;
        }

        public void setLoan_amt(String loan_amt) {
            this.loan_amt = loan_amt;
        }

        public String getTotal_balance() {
            return total_balance;
        }

        public void setTotal_balance(String total_balance) {
            this.total_balance = total_balance;
        }

        public String getTrans_time() {
            return trans_time;
        }

        public void setTrans_time(String trans_time) {
            this.trans_time = trans_time;
        }

        public String getLoan_status() {
            return loan_status;
        }

        public void setLoan_status(String loan_status) {
            this.loan_status = loan_status;
        }

        public String getExpiration_date() {
            return expiration_date;
        }

        public void setExpiration_date(String expiration_date) {
            this.expiration_date = expiration_date;
        }

        public String getAmount_repayment() {
            return amount_repayment;
        }

        public void setAmount_repayment(String amount_repayment) {
            this.amount_repayment = amount_repayment;
        }

        public String getInterest_repayment() {
            return interest_repayment;
        }

        public void setInterest_repayment(String interest_repayment) {
            this.interest_repayment = interest_repayment;
        }

        public String getExpired_interest_repayment() {
            return expired_interest_repayment;
        }

        public void setExpired_interest_repayment(String expired_interest_repayment) {
            this.expired_interest_repayment = expired_interest_repayment;
        }

        public String getCompound_interest_repayment() {
            return compound_interest_repayment;
        }

        public void setCompound_interest_repayment(String compound_interest_repayment) {
            this.compound_interest_repayment = compound_interest_repayment;
        }

        public String getAmount_balance() {
            return amount_balance;
        }

        public void setAmount_balance(String amount_balance) {
            this.amount_balance = amount_balance;
        }

        public String getInterest_balance() {
            return interest_balance;
        }

        public void setInterest_balance(String interest_balance) {
            this.interest_balance = interest_balance;
        }

        public String getExpired_interest_balance() {
            return expired_interest_balance;
        }

        public void setExpired_interest_balance(String expired_interest_balance) {
            this.expired_interest_balance = expired_interest_balance;
        }

        public String getCompound_interest_balance() {
            return compound_interest_balance;
        }

        public String getTotal_fee_hs() {
            return total_fee_hs;
        }

        public void setTotal_fee_hs(String total_fee_hs) {
            this.total_fee_hs = total_fee_hs;
        }

        public void setCompound_interest_balance(String compound_interest_balance) {
            this.compound_interest_balance = compound_interest_balance;
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

    public ArrayList<KNanYueLoanItem> getList() {
        return list;
    }

    public void setList(ArrayList<KNanYueLoanItem> list) {
        this.list = list;
    }



}
