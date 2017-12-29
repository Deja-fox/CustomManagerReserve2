package com.jald.reserve.bean.http.response;

import java.io.Serializable;
import java.util.List;

public class KOutOfStockGoodsListResponseBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String total;
	private List<KOutOfStockGoodsItem> list;

	public static class KOutOfStockGoodsItem implements Serializable {
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

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<KOutOfStockGoodsItem> getList() {
		return list;
	}

	public void setList(List<KOutOfStockGoodsItem> list) {
		this.list = list;
	}

}
