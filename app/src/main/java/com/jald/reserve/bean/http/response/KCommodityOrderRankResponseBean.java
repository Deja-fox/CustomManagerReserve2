package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;

public class KCommodityOrderRankResponseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int total;
	private int page_sum;

	private List<KCommodityOrderInfo> list;

	public static class KCommodityOrderInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		private String item_id;
		private String item_name;
		private String qty;
		private String amt;

		public String getItem_id() {
			return item_id;
		}

		public void setItem_id(String item_id) {
			this.item_id = item_id;
		}

		public String getItem_name() {
			return item_name;
		}

		public void setItem_name(String item_name) {
			this.item_name = item_name;
		}

		public String getQty() {
			return qty;
		}

		public void setQty(String qty) {
			this.qty = qty;
		}

		public String getAmt() {
			return amt;
		}

		public void setAmt(String amt) {
			this.amt = amt;
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

	public List<KCommodityOrderInfo> getList() {
		return list;
	}

	public void setList(List<KCommodityOrderInfo> list) {
		this.list = list;
	}

}
