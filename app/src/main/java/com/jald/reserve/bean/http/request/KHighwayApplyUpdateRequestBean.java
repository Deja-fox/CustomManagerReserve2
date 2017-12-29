package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KHighwayApplyUpdateRequestBean implements Serializable {
	private static final long serialVersionUID = -1191762623322046651L;

	private String car_num;
	private String address;
	private String open_branch;

	public String getCar_num() {
		return car_num;
	}

	public void setCar_num(String car_num) {
		this.car_num = car_num;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOpen_branch() {
		return open_branch;
	}

	public void setOpen_branch(String open_branch) {
		this.open_branch = open_branch;
	}

}
