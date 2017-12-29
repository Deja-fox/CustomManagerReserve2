package com.jald.reserve.bean.http.response;

import com.jald.reserve.http.KHttpConst;

public class KBaseHttpResponseBean {

	private String ret_code;
	private String ret_msg;
	private String content;
	private String signature;

	public String getRet_code() {
		return ret_code;
	}

	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}

	public String getRet_msg() {
		return ret_msg;
	}

	public void setRet_msg(String ret_msg) {
		this.ret_msg = ret_msg;
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

	public static KBaseHttpResponseBean makeErrReturnResult(String errMsg) {
		KBaseHttpResponseBean result = new KBaseHttpResponseBean();
		result.setRet_code(KHttpConst.HTTP_CONNECT_ERR);
		result.setRet_msg(errMsg);
		return result;
	}
}
