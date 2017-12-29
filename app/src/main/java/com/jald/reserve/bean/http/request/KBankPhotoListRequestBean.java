package com.jald.reserve.bean.http.request;

public class KBankPhotoListRequestBean {

	private String bank_id;
	private String bank_img_url;
	private String bank_name;

	public KBankPhotoListRequestBean() {
		bank_id = "";
		bank_img_url = "";
		bank_name = "点击选择归属银行";
	}

	public String getBank_id() {
		return bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getBank_img_url() {
		return bank_img_url;
	}

	public void setBank_img_url(String bank_img_url) {
		this.bank_img_url = bank_img_url;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

}