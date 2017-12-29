package com.jald.reserve.bean.http.response;

import java.util.ArrayList;
import java.util.List;

public class KTelChargeBillResponseBean {

	private int total;
	private int page_sum;
	private List<TelChargeBillItem> list = new ArrayList<TelChargeBillItem>();

	public static class TelChargeBillItem {
		private String trans_date;
		private String order_no;
		private String tel;
		private String recharge_amt;
		private String boss;
		private String province;
		private String city;
		private String status;

		public String getTrans_date() {
			return trans_date;
		}

		public void setTrans_date(String trans_date) {
			this.trans_date = trans_date;
		}

		public String getOrder_no() {
			return order_no;
		}

		public void setOrder_no(String order_no) {
			this.order_no = order_no;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getRecharge_amt() {
			return recharge_amt;
		}

		public void setRecharge_amt(String recharge_amt) {
			this.recharge_amt = recharge_amt;
		}

		public String getBoss() {
			return boss;
		}

		public void setBoss(String boss) {
			this.boss = boss;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
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

	public List<TelChargeBillItem> getList() {
		return list;
	}

	public void setList(List<TelChargeBillItem> list) {
		this.list = list;
	}

}
