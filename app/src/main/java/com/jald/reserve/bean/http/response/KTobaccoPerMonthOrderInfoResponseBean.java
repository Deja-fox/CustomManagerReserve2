package com.jald.reserve.bean.http.response;

import java.io.Serializable;

public class KTobaccoPerMonthOrderInfoResponseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String amt;
	private String qty;

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

}
