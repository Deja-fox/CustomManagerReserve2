package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoOrderListAdapter;
import com.jald.reserve.adapter.KTobaccoOrderListAdapter.OnOrderDetailClickListener;
import com.jald.reserve.adapter.KYoumktOrderListAdapter;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequesContextBean;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KOrderPayChannelResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;
import com.jald.reserve.bean.http.response.KYoumktOrderListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KWaitToPayOrderActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.pullrefresh.ExtendedRefreshableListViewWrapper;


import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;

public class KWaitToPayOrderFragment extends Fragment {

    public static final String TAG = KWaitToPayOrderFragment.class.getSimpleName();

    private KWaitToPayOrderActivity mParent;
    private View mRoot;

    // 烟草订单相关
    @ViewInject(R.id.bill_list)
    private ListView tobaccoOrderList;
    private KTobaccoOrderListAdapter tobaccoOrderAdapter;
    private KTobaccoOrderListResponseBean tobaccoOrderDetailInfo;
    private ArrayList<KTobaccoOrderItem> tobaccoOrderListData;


    // 优市网订单相关
    @ViewInject(R.id.youmktOrderList)
    private ExtendedRefreshableListViewWrapper youmktOrderListWrapper;
    private ListView youmktOrderListView;
    private int PAGE_SIZE = 15;
    private int curPageNum = -1;
    private int totalCount = -1;
    private KYoumktOrderListAdapter youmktOrderAdapter;
    private ArrayList<KYoumktOrderListResponseBean.KYoumktOrderItem> youmktOrderListData;

    private boolean isTobaccoOrderDataLoaded = false;
    private boolean isYoumktOrderDataLoaded = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KWaitToPayOrderActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null) {
            return ViewUtil.detachFromParent(mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_wati_to_pay_bills, container, false);
        x.view().inject(this, this.mRoot);
        this.tobaccoOrderAdapter = new KTobaccoOrderListAdapter(getActivity());

        this.youmktOrderListWrapper.setHideHeader();
        this.youmktOrderListView = this.youmktOrderListWrapper.getListView();
        this.youmktOrderAdapter = new KYoumktOrderListAdapter(getActivity());
        return this.mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!isTobaccoOrderDataLoaded) {
            this.loadTobacooOrderList();
        }
        if (!isYoumktOrderDataLoaded) {
            this.firstLoadYoumktBillData();
        }
    }

    private void firstLoadYoumktBillData() {
        DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", 1);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "1");
        postJson.put("payment", "0");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.YOUMKT_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    isYoumktOrderDataLoaded = true;
                    curPageNum = 1;
                    final KYoumktOrderListResponseBean rspBean = JSON.parseObject(result.getContent(), KYoumktOrderListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (totalCount <= PAGE_SIZE) {
                        youmktOrderListWrapper.notifyCloseLoadMore(true);
                    }
                    youmktOrderListData = rspBean.getList();
                    if (youmktOrderListData != null) {
                        youmktOrderAdapter.setOrderList(youmktOrderListData);
                        youmktOrderListView.setAdapter(youmktOrderAdapter);
                        youmktOrderListWrapper.setOnPullDownListener(new ExtendedRefreshableListViewWrapper.OnPullListener() {
                            @Override
                            public void onRefresh() {
                            }

                            @Override
                            public void onMore() {
                                loadMoreYoumktOrderData();
                            }
                        });
                        youmktOrderAdapter.setOnOrderDetailClickListener(new KYoumktOrderListAdapter.OnOrderDetailClickListener() {
                            @Override
                            public void onOrderDetailClicked(KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
                                jumpToYoumktOrderDetailPage(orderItem);
                            }
                        });
                        youmktOrderAdapter.setOnOrderPayClickListener(new KYoumktOrderListAdapter.OnOrderPayClickListener() {
                            @Override
                            public void onOrderPay(int position, KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
                                jumpToYoumktOrderPayPage(rspBean, orderItem);
                            }
                        });
                    }
                    DialogProvider.hideProgressBar();
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }

    private void loadMoreYoumktOrderData() {
        if (curPageNum == -1) {
            return;
        }
        if (this.youmktOrderListData.size() >= totalCount) {
            this.youmktOrderListWrapper.notifyCloseLoadMore(true);
            return;
        }
        JSONObject postJson = new JSONObject();
        postJson.put("page_num", ++curPageNum);
        postJson.put("page_size", PAGE_SIZE);
        postJson.put("pay_status", "1");
        postJson.put("payment", "0");
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.YOUMKT_ORDER_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    youmktOrderListWrapper.notifyDidMore();
                    KYoumktOrderListResponseBean rspBean = JSON.parseObject(result.getContent(), KYoumktOrderListResponseBean.class);
                    totalCount = rspBean.getTotal();
                    if (rspBean.getList() != null) {
                        youmktOrderListData.addAll(rspBean.getList());
                        youmktOrderAdapter.notifyDataSetChanged();
                    }
                    if (youmktOrderListData.size() >= totalCount) {
                        youmktOrderListWrapper.notifyCloseLoadMore(true);
                        return;
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                youmktOrderListWrapper.notifyCloseLoadMore(true);
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                youmktOrderListWrapper.notifyCloseLoadMore(true);
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }


    private void loadTobacooOrderList() {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.TOBACCO_ORDER_QUERY, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    isTobaccoOrderDataLoaded = true;
                    KTobaccoOrderListResponseBean tobaccoBillListInfo = JSON.parseObject(result.getContent(), KTobaccoOrderListResponseBean.class);
                    if (tobaccoBillListInfo == null || tobaccoBillListInfo.getTotal() == 0) {
                        Log.e(TAG, "暂无烟草订单");
                    } else {
                        tobaccoOrderDetailInfo = tobaccoBillListInfo;
                        tobaccoOrderListData = tobaccoBillListInfo.getList();
                        tobaccoOrderAdapter.setBillList(tobaccoOrderListData);
                        tobaccoOrderList.setAdapter(tobaccoOrderAdapter);
                        tobaccoOrderAdapter.setOnOrderDetailClickListener(new OnOrderDetailClickListener() {
                            @Override
                            public void onOrderDetailClicked(KTobaccoOrderItem orderItem) {
                                jumpToTobaccoOrderDetailPage(orderItem);
                            }
                        });
                        tobaccoOrderAdapter.setOnOrderPayClickListener(new KTobaccoOrderListAdapter.OnOrderPayClickListener() {
                            @Override
                            public void onOrderPay(int position, KTobaccoOrderItem orderItem) {
                                if (orderItem.getIs_pay().equals("0")) {
                                    Toast.makeText(getActivity(), "当前时间烟草公司不允许支付", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                jumpToTobaccoOrderPayPage(tobaccoOrderDetailInfo, orderItem);
                            }
                        });
                    }
                }
            }
        });
    }

    private void jumpToYoumktOrderDetailPage(KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
        KYoumktOrderDetailFragment orderDetailFragment = new KYoumktOrderDetailFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(KYoumktOrderDetailFragment.ARG_KEY_ORDER_ITEM, orderItem);
        orderDetailFragment.setArguments(arg);
        mParent.changeFragment(orderDetailFragment, true);
    }

    private void jumpToTobaccoOrderDetailPage(KTobaccoOrderItem orderItem) {
        KTobaccoOrderDetailFragment orderDetailFragment = new KTobaccoOrderDetailFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(KTobaccoOrderDetailFragment.ARG_KEY_ORDER_ITEM, orderItem);
        orderDetailFragment.setArguments(arg);
        mParent.changeFragment(orderDetailFragment, true);
    }

    private void jumpToTobaccoOrderPayPage(final KTobaccoOrderListResponseBean orderDetailInfo, final KTobaccoOrderItem orderItem) {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        json.put("com_id", orderDetailInfo.getCom_id());
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.ORDER_PAY_MODEL_LIST, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    KOrderPayChannelResponseBean channelInfo = JSON.parseObject(result.getContent(), KOrderPayChannelResponseBean.class);
                    KNayYueOrderPayFragment orderPayFragment = new KNayYueOrderPayFragment();
                    KNanYueOrderPayRequesContextBean orderPayContextInfo = new KNanYueOrderPayRequesContextBean();
                    orderPayContextInfo.setPayChannelInfo(channelInfo);
                    KNanYueOrderPayRequestBean orderPayRequestBean = new KNanYueOrderPayRequestBean();
                    orderPayRequestBean.setAmt(orderItem.getAmt());
                    orderPayRequestBean.setCo_num(orderItem.getCo_num());
                    orderPayRequestBean.setCom_id(orderDetailInfo.getCom_id());
                    orderPayRequestBean.setCom_name(orderDetailInfo.getCom_name());
                    // 烟草订单没有账期
                    orderPayRequestBean.setDays("0");
                    orderPayContextInfo.setOrderPayRequestBean(orderPayRequestBean);
                    Bundle args = new Bundle();
                    args.putSerializable(KNayYueOrderPayFragment.ARG_KEY_ORDER_PAY_INFO, orderPayContextInfo);
                    orderPayFragment.setArguments(args);
                    mParent.changeFragment(orderPayFragment, true);
                }
            }
        });
    }

    private void jumpToYoumktOrderPayPage(final KYoumktOrderListResponseBean orderDetailInfo, final KYoumktOrderListResponseBean.KYoumktOrderItem orderItem) {
        DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(mParent);
            }
        });
        JSONObject json = new JSONObject();
        json.put("com_id", orderItem.getCom_tp_id());
        KHttpClient.singleInstance().postData(mParent, KHttpAdress.ORDER_PAY_MODEL_LIST, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    KOrderPayChannelResponseBean channelInfo = JSON.parseObject(result.getContent(), KOrderPayChannelResponseBean.class);
                    KNayYueOrderPayFragment orderPayFragment = new KNayYueOrderPayFragment();
                    KNanYueOrderPayRequesContextBean orderPayContextInfo = new KNanYueOrderPayRequesContextBean();
                    orderPayContextInfo.setPayChannelInfo(channelInfo);
                    KNanYueOrderPayRequestBean orderPayRequestBean = new KNanYueOrderPayRequestBean();
                    orderPayRequestBean.setAmt(orderItem.getAmt());
                    orderPayRequestBean.setCo_num(orderItem.getCo_num());
                    orderPayRequestBean.setCom_name(orderItem.getCom_name());
                    orderPayRequestBean.setCom_id(orderItem.getCom_tp_id());
                    orderPayRequestBean.setDays(orderItem.getDays());
                    orderPayContextInfo.setOrderPayRequestBean(orderPayRequestBean);
                    Bundle args = new Bundle();
                    args.putSerializable(KNayYueOrderPayFragment.ARG_KEY_ORDER_PAY_INFO, orderPayContextInfo);
                    orderPayFragment.setArguments(args);
                    mParent.changeFragment(orderPayFragment, true);
                }
            }
        });
    }

}
