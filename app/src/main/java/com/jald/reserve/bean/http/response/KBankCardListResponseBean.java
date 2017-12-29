package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;

public class KBankCardListResponseBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private int total;
	private List<KBankCardItemBean> list;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<KBankCardItemBean> getList() {
		return list;
	}

	public void setList(List<KBankCardItemBean> list) {
		this.list = list;
	}

	public static class KBankCardItemBean implements Serializable {

		private static final long serialVersionUID = 1L;
		private String debit_accont_no;
		private String account_name;
		private String id_number;
		private String is_default;
		private String account_category;
		private String account_nature;
		private String account_status;
		private String bank_id;
		private String bank_name;

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

		public String getIs_default() {
			return is_default;
		}

		public void setIs_default(String is_default) {
			this.is_default = is_default;
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

		public String getAccount_status() {
			return account_status;
		}

		public void setAccount_status(String account_status) {
			this.account_status = account_status;
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
}
