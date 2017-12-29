package com.jald.reserve.bean.http.request;

public class KUserRegisterRequestBean {

	private String password;
	private String sms_code;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSms_code() {
		return sms_code;
	}

	public void setSms_code(String sms_code) {
		this.sms_code = sms_code;
	}

}
