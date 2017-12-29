package com.jald.reserve.bean.http.response;

import java.io.Serializable;

public class KTobaccoCustRankResponseBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String com_name;
	private String com_ranking;
	private String dpt_name;
	private String dpt_ranking;

	public String getCom_name() {
		return com_name;
	}

	public void setCom_name(String com_name) {
		this.com_name = com_name;
	}

	public String getCom_ranking() {
		return com_ranking;
	}

	public void setCom_ranking(String com_ranking) {
		this.com_ranking = com_ranking;
	}

	public String getDpt_name() {
		return dpt_name;
	}

	public void setDpt_name(String dpt_name) {
		this.dpt_name = dpt_name;
	}

	public String getDpt_ranking() {
		return dpt_ranking;
	}

	public void setDpt_ranking(String dpt_ranking) {
		this.dpt_ranking = dpt_ranking;
	}

}
