package com.jald.reserve.bean.http.request;

public class KSmsInfoRequestBean {

	private String tel;
	private String ver_type;

	public KSmsInfoRequestBean(String tel, String VerType) {
		this.tel = tel;
		this.ver_type = VerType;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getVer_type() {
		return ver_type;
	}

	public void setVer_type(String ver_type) {
		this.ver_type = ver_type;
	}

}
