package com.jald.reserve.bean.normal;

import java.io.Serializable;
import java.util.ArrayList;

import com.jald.reserve.bean.http.request.KViolationRecordQueryRequestBean;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;

public class KTrafficFineContextBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private KViolationRecordQueryRequestBean queryCondition;
	private ArrayList<KViolationRecordBean> selectedRecordList;
	private KTrafficPostInfoFrom postInfo;
	private KViolationPaymentQueryResponseBean paymentInfoBean;

	public KViolationRecordQueryRequestBean getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(KViolationRecordQueryRequestBean queryCondition) {
		this.queryCondition = queryCondition;
	}

	public ArrayList<KViolationRecordBean> getSelectedRecordList() {
		return selectedRecordList;
	}

	public void setSelectedRecordList(ArrayList<KViolationRecordBean> selectedRecordList) {
		this.selectedRecordList = selectedRecordList;
	}

	public KTrafficPostInfoFrom getPostInfo() {
		return postInfo;
	}

	public void setPostInfo(KTrafficPostInfoFrom postInfo) {
		this.postInfo = postInfo;
	}

	public KViolationPaymentQueryResponseBean getPaymentInfoBean() {
		return paymentInfoBean;
	}

	public void setPaymentInfoBean(KViolationPaymentQueryResponseBean paymentInfoBean) {
		this.paymentInfoBean = paymentInfoBean;
	}

}
