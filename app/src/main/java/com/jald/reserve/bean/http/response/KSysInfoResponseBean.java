package com.jald.reserve.bean.http.response;

public class KSysInfoResponseBean {

	public String version_url;
	public String version;
	public String sms_switch;
	public String sms_code;
	public String support_version;
	public String alert_type;
	public String alert_msg;
	public String public_key;

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public String getVersion_url() {
		return version_url;
	}

	public void setVersion_url(String version_url) {
		this.version_url = version_url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSms_switch() {
		return sms_switch;
	}

	public void setSms_switch(String sms_switch) {
		this.sms_switch = sms_switch;
	}

	public String getSms_code() {
		return sms_code;
	}

	public void setSms_code(String sms_code) {
		this.sms_code = sms_code;
	}

	public String getSupport_version() {
		return support_version;
	}

	public void setSupport_version(String support_version) {
		this.support_version = support_version;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlert_msg() {
		return alert_msg;
	}

	public void setAlert_msg(String alert_msg) {
		this.alert_msg = alert_msg;
	}

}