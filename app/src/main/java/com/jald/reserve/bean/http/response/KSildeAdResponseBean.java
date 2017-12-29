package com.jald.reserve.bean.http.response;

import java.util.List;

public class KSildeAdResponseBean {

	private int total;
	private List<KAdsItemBean> list;

	public static class KAdsItemBean {

		private String ad_img_url;
		private String exec_code;

		public String getAd_img_url() {
			return ad_img_url;
		}

		public void setAd_img_url(String ad_img_url) {
			this.ad_img_url = ad_img_url;
		}

		public String getExec_code() {
			return exec_code;
		}

		public void setExec_code(String exec_code) {
			this.exec_code = exec_code;
		}

	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<KAdsItemBean> getList() {
		return list;
	}

	public void setList(List<KAdsItemBean> list) {
		this.list = list;
	}

}
