package com.jald.reserve.bean.http.response;

import java.util.ArrayList;
import java.util.List;

public class KAllTransBillResponseBean {

	private int total;
	private int page_sum;
	private List<KTransBillItem> list = new ArrayList<KTransBillItem>();

	public static class KTransBillItem {
		private String trans_type;
		private String trans_date;
		private String trans_amount;
		private String fee_amount;

		public String getTrans_type() {
			return trans_type;
		}

		public void setTrans_type(String trans_type) {
			this.trans_type = trans_type;
		}

		public String getTrans_date() {
			return trans_date;
		}

		public void setTrans_date(String trans_date) {
			this.trans_date = trans_date;
		}

		public String getTrans_amount() {
			return trans_amount;
		}

		public void setTrans_amount(String trans_amount) {
			this.trans_amount = trans_amount;
		}

		public String getFee_amount() {
			return fee_amount;
		}

		public void setFee_amount(String fee_amount) {
			this.fee_amount = fee_amount;
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

	public List<KTransBillItem> getList() {
		return list;
	}

	public void setList(List<KTransBillItem> list) {
		this.list = list;
	}

}
