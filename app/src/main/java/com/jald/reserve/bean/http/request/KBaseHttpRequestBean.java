package com.jald.reserve.bean.http.request;

public class KBaseHttpRequestBean {

	private String content;
	private String signature;
	private String user;

	public KBaseHttpRequestBean(String content, String user) {
		this.content = content;
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
