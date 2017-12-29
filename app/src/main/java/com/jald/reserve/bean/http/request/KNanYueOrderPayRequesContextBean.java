package com.jald.reserve.bean.http.request;

import com.jald.reserve.bean.http.response.KOrderPayChannelResponseBean;

import java.io.Serializable;

public class KNanYueOrderPayRequesContextBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private KOrderPayChannelResponseBean payChannelInfo;
    private KNanYueOrderPayRequestBean orderPayRequestBean;

    public KNanYueOrderPayRequestBean getOrderPayRequestBean() {
        return orderPayRequestBean;
    }

    public void setOrderPayRequestBean(KNanYueOrderPayRequestBean orderPayRequestBean) {
        this.orderPayRequestBean = orderPayRequestBean;
    }

    public KOrderPayChannelResponseBean getPayChannelInfo() {
        return payChannelInfo;
    }

    public void setPayChannelInfo(KOrderPayChannelResponseBean payChannelInfo) {
        this.payChannelInfo = payChannelInfo;
    }
}
