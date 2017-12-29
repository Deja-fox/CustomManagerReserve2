package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KAccountChargeRequestBean implements Serializable {

	private static final long serialVersionUID = 7680289942538459842L;

	private String acc_no;
	private String acc_bank_id;
	private String trans_amt;
	private String payment_password;
	private String fee;

	public String getAcc_no() {
		return acc_no;
	}

	public void setAcc_no(String acc_no) {
		this.acc_no = acc_no;
	}

	public String getAcc_bank_id() {
		return acc_bank_id;
	}

	public void setAcc_bank_id(String acc_bank_id) {
		this.acc_bank_id = acc_bank_id;
	}

	public String getTrans_amt() {
		return trans_amt;
	}

	public void setTrans_amt(String trans_amt) {
		this.trans_amt = trans_amt;
	}

	public String getPayment_password() {
		return payment_password;
	}

	public void setPayment_password(String payment_password) {
		this.payment_password = payment_password;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

}
