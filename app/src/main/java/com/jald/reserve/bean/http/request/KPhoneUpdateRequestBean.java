package com.jald.reserve.bean.http.request;

public class KPhoneUpdateRequestBean 
{
	private String tel;
	private String sms_code;
	private String payment_password;
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getSms_code() {
		return sms_code;
	}
	public void setSms_code(String sms_code) {
		this.sms_code = sms_code;
	}
	public String getPayment_password() {
		return payment_password;
	}
	public void setPayment_password(String payment_password) {
		this.payment_password = payment_password;
	}
	
}
