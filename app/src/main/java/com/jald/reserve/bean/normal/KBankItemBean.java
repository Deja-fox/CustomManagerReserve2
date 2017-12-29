package com.jald.reserve.bean.normal;

import java.io.Serializable;

public class KBankItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String bank_id;
	private String bank_name;

	public KBankItemBean() {
	}

	public KBankItemBean(String bank_id, String bank_name) {
		super();
		this.bank_id = bank_id;
		this.bank_name = bank_name;
	}

	public String getBank_id() {
		return bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

}
