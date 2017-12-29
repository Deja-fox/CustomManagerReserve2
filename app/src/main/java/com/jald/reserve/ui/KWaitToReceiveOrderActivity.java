package com.jald.reserve.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KWaitToReceiveOrderListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.fragment.KTobaccoOrderDetailFragment;
import com.jald.reserve.ui.fragment.KWaitToReceiveOrderDetailFragment;
import com.jald.reserve.util.DialogProvider;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;
import org.xutils.x;

import java.util.Random;

@ContentView(R.layout.k_activity_wait_to_receive_order)
public class KWaitToReceiveOrderActivity extends KBaseActivity {

    public static final int REQUEST_CODE_SET_MYSTORE_LOC = 0x99;

    @ViewInject(R.id.title)
    private TextView txtTitle;
    @ViewInject(R.id.btnRefresh)
    private ImageButton btnRefreshOrder;
    @ViewInject(R.id.btnGetLocation)
    private ViewGroup btnGetLocation;
    @ViewInject(R.id.txtMyStoreLoction)
    private TextView txtMyStoreLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        EventBus.getDefault().register(this);

        if (KBaseApplication.getInstance().getUserInfoStub() != null) {
            String lat = KBaseApplication.getInstance().getUserInfoStub().getUser_lat();
            String lng = KBaseApplication.getInstance().getUserInfoStub().getUser_lng();
            if (lat != null && lng != null && !lat.equals("") && !lng.equals("")) {
                txtMyStoreLocation.setText("店铺位置:已设置,点击重设");
            } else {
                txtMyStoreLocation.setText("店铺位置:未设置,点击设置");
            }
        }
        this.getBillListInfo();
    }


    private void getBillListInfo() {
        DialogProvider.showProgressBar(this, new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(KWaitToReceiveOrderActivity.this);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(KWaitToReceiveOrderActivity.this, KHttpAdress.WAIT_TO_RECEIVE_ORDER_QUERY, json.toJSONString(),
                KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KWaitToReceiveOrderListResponseBean waitToReceiveOrderInfo = JSON.parseObject(result.getContent(),
                                    KWaitToReceiveOrderListResponseBean.class);
                            if (waitToReceiveOrderInfo == null || waitToReceiveOrderInfo.getList() == null || waitToReceiveOrderInfo.getList().size() == 0) {
                                Toast.makeText(KWaitToReceiveOrderActivity.this, "暂无订单", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                jumpToOrderListPage(waitToReceiveOrderInfo);
                            }
                        }
                    }
                }

        );
    }

    private void jumpToOrderListPage(final KWaitToReceiveOrderListResponseBean waitToReceiveOrderInfo) {
    }

    private void jumpToOrderDetailPage(KWaitToReceiveOrderListResponseBean.KWaitToReceiveOrderItem orderItem) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Bundle arg = new Bundle();
        arg.putSerializable(KTobaccoOrderDetailFragment.ARG_KEY_ORDER_ITEM, orderItem);
        KWaitToReceiveOrderDetailFragment orderDetailFragment = new KWaitToReceiveOrderDetailFragment();
        orderDetailFragment.setArguments(arg);
        transaction.replace(R.id.container, orderDetailFragment, "waitToReceiveOrderDetailFragment");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }


    @Event(R.id.btnGetLocation)
    private void onGetLocationClick(View v) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_MYSTORE_LOC) {
            if (resultCode == RESULT_OK) {
                txtMyStoreLocation.setText("店铺位置：已设置,点击重设");
                // Broadcast location change event
                EventBus.getDefault().post(new EventOnMyStoreLocationChange());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class EventOnMyStoreLocationChange {
        public EventOnMyStoreLocationChange() {
        }
    }

    @Event(R.id.btnRefresh)
    private void onRefreshOrderClick(View v) {
        this.getBillListInfo();
    }


    public static class EventRrefreshBtnVisibility {
        public int visibility = View.GONE;

        public EventRrefreshBtnVisibility(Integer visibility) {
            this.visibility = visibility;
        }
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void onSetRefrehBtnVisibility(EventRrefreshBtnVisibility visibilityEvent) {
        btnRefreshOrder.setVisibility(visibilityEvent.visibility);
    }

    public static class EventStoreLocBtnVisibility {
        public int visibility = View.GONE;

        public EventStoreLocBtnVisibility(Integer visibility) {
            this.visibility = visibility;
        }

    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void onSetStoreLocBtnVisibility(EventStoreLocBtnVisibility visibilityEvent) {
        btnGetLocation.setVisibility(visibilityEvent.visibility);
        if (visibilityEvent.visibility == View.GONE) {
            txtTitle.setVisibility(View.VISIBLE);
        } else {
            txtTitle.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
