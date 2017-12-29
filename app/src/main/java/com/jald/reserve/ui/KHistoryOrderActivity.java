package com.jald.reserve.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoOrderListAdapter.OnOrderDetailClickListener;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequesContextBean;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KOrderPayChannelResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.fragment.KHistoryOrderFragment;
import com.jald.reserve.ui.fragment.KHistoryOrderFragment.IHistoryOrderContainer;
import com.jald.reserve.ui.fragment.KHistoryOrderFragment.OnOrderPayClickCallback;
import com.jald.reserve.ui.fragment.KNayYueOrderPayFragment;
import com.jald.reserve.ui.fragment.KTobaccoOrderDetailFragment;
import com.jald.reserve.util.DialogProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KHistoryOrderActivity extends KBaseActivity implements IHistoryOrderContainer {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.btn_search_tobacco_order)
    RelativeLayout btnSearchTobaccoOrder;
    @Bind(R.id.container)
    FrameLayout container;

    private KHistoryOrderFragment historyOrderFragment;
    private KTobaccoOrderDetailFragment orderDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_history_order);
        ButterKnife.bind(this);

        this.btnSearchTobaccoOrder.setVisibility(View.GONE);
        this.historyOrderFragment = new KHistoryOrderFragment();
        this.orderDetailFragment = new KTobaccoOrderDetailFragment();

        jumpToHistoryOrderListPage();

    }


    private void jumpToHistoryOrderListPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, historyOrderFragment, "historyOrderFragment");
        transaction.commitAllowingStateLoss();
        historyOrderFragment.setOnOrderDetailClickListener(new OnOrderDetailClickListener() {
            @Override
            public void onOrderDetailClicked(KTobaccoOrderItem orderItem) {
                jumpToOrderDetailPage(orderItem);
            }
        });
        historyOrderFragment.setOnOrderPayClickCallback(new OnOrderPayClickCallback() {
            @Override
            public void onOrderPay(KTobaccoOrderListResponseBean orderDetailInfo, int position, KTobaccoOrderItem orderItem) {
                jumpToOrderPayPage(orderDetailInfo, orderItem);
            }
        });
    }

    @Override
    public void showSearch(final OnClickListener clickReceiver) {
        this.btnSearchTobaccoOrder.setVisibility(View.VISIBLE);
        this.btnSearchTobaccoOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickReceiver != null) {
                    clickReceiver.onClick(v);
                }
            }
        });
    }

    @Override
    public void hideSearch() {
        this.btnSearchTobaccoOrder.setVisibility(View.GONE);
    }

    private void jumpToOrderDetailPage(KTobaccoOrderItem orderItem) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Bundle arg = new Bundle();
        arg.putSerializable(KTobaccoOrderDetailFragment.ARG_KEY_ORDER_ITEM, orderItem);
        orderDetailFragment.setArguments(arg);
        transaction.replace(R.id.container, orderDetailFragment, "orderDetailFragment");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void jumpToOrderPayPage(final KTobaccoOrderListResponseBean orderDetailInfo, final KTobaccoOrderItem orderItem) {
        DialogProvider.showProgressBar(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(KHistoryOrderActivity.this);
            }
        });
        JSONObject json = new JSONObject();
        json.put("com_id", orderDetailInfo.getCom_id());
        KHttpClient.singleInstance().postData(this, KHttpAdress.ORDER_PAY_MODEL_LIST, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
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
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    transaction.replace(R.id.container, orderPayFragment, "orderPayFragment");
                    transaction.addToBackStack(null);
                    transaction.commitAllowingStateLoss();
                }
            }
        });
    }


    public void changeFragment(Fragment fragment, boolean isAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        if (isAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


}
