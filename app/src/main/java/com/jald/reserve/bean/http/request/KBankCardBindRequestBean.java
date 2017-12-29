package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KBankCardBindRequestBean implements Serializable {

	private static final long serialVersionUID = -1920299447953500873L;
	private String debit_accont_no;
	private String account_name;
	private String id_number;
	private String account_category;
	private String account_nature;
	private String bank_id;
	private String debit_bind_type;

	public String getDebit_accont_no() {
		return debit_accont_no;
	}

	public void setDebit_accont_no(String debit_accont_no) {
		this.debit_accont_no = debit_accont_no;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getAccount_category() {
		return account_category;
	}

	public void setAccount_category(String account_category) {
		this.account_category = account_category;
	}

	public String getAccount_nature() {
		return account_nature;
	}

	public void setAccount_nature(String account_nature) {
		this.account_nature = account_nature;
	}

	public String getBank_id() {
		return bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getDebit_bind_type() {
		return debit_bind_type;
	}

	public void setDebit_bind_type(String debit_bind_type) {
		this.debit_bind_type = debit_bind_type;
	}

}
