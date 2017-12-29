package com.jald.reserve.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.adapter.KDefpayMenuAdapter;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KSildeAdResponseBean;
import com.jald.reserve.bean.normal.KDefpayMenuItem;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KAccountChargeActivity;
import com.jald.reserve.ui.KBillCenterActivity;
import com.jald.reserve.ui.KLoginPageActivity;
import com.jald.reserve.ui.KMyBankCardActivity;
import com.jald.reserve.ui.KPhonefeeChargeActivity;
import com.jald.reserve.ui.KTrafficFineActivity;
import com.jald.reserve.ui.KWaitToPayOrderActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.AdsView;
import com.jald.reserve.widget.ExtendedGridView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


public class KDefpayFragment extends Fragment {

    public static final String TAG = KDefpayFragment.class.getSimpleName();

    @ViewInject(R.id.top_ad_view)
    private AdsView adsView;

    @ViewInject(R.id.menu_grid)
    private ExtendedGridView mainMenuGrid;

    @ViewInject(R.id.menu_grid_proxy_funs)
    private ExtendedGridView secondaryMenuGrid;

    private List<KDefpayMenuItem> mainMenuList;
    private KDefpayMenuAdapter mainMenuAdapter;

    private List<KDefpayMenuItem> secondaryMenuList;
    private KDefpayMenuAdapter secondaryMenuAdapter;

    private View mRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return mRoot;
        }
        mRoot = inflater.inflate(R.layout.k_fragment_defpay_main_page, container, false);
        x.view().inject(this, mRoot);
        this.adsView.getLayoutParams().height = (int) (getResources().getDisplayMetrics().widthPixels * 0.45);
        this.adsView.setAdsList(null);

        this.mainMenuAdapter = new KDefpayMenuAdapter(getActivity());
        this.mainMenuList = buildMainMenuList();
        this.mainMenuAdapter.setMenuList(mainMenuList);
        this.mainMenuGrid.setAdapter(mainMenuAdapter);

        this.secondaryMenuAdapter = new KDefpayMenuAdapter(getActivity());
        this.secondaryMenuList = buildSecondaryMenuList();
        this.secondaryMenuAdapter.setMenuList(secondaryMenuList);
        this.secondaryMenuGrid.setAdapter(secondaryMenuAdapter);

        this.mainMenuGrid.setOnItemClickListener(onMainMenuItemClickListener);
        this.secondaryMenuGrid.setOnItemClickListener(onSecondaryMenuItemClickListener);
        this.loadAdList();
        return mRoot;
    }

    private void loadAdList() {
        final KBaseApplication app = KBaseApplication.getInstance();
        if (app.getAdsBean() != null) {
            this.adsView.setAdsList(app.getAdsBean().getList());
            return;
        }
        JSONObject param = new JSONObject();
        param.put("random", String.valueOf(new Random().nextLong()));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.AD_LIST, param, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            protected void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                KSildeAdResponseBean adResponseBean = JSON.parseObject(result.getContent(), KSildeAdResponseBean.class);
                app.setAdsBean(adResponseBean);
                adsView.setAdsList(adResponseBean.getList());
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "获取广告列表失败" + "(" + ex.getMessage() + ")");
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                Log.e(TAG,"获取广告列表失败" + statusCode);
            }
        });
    }

    private OnItemClickListener onMainMenuItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!checkLogin()) {
                Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            KDefpayMenuItem clickedItem = mainMenuList.get(position);
            if (clickedItem.getTitle().trim().equals("我的账单")) {
                Intent intent = new Intent(getActivity(), KBillCenterActivity.class);
                startActivity(intent);
            } else if (clickedItem.getTitle().trim().equals("我的银行卡")) {
                Intent intent = new Intent(getActivity(), KMyBankCardActivity.class);
                startActivity(intent);
            } else if (clickedItem.getTitle().trim().equals("账户充值")) {
                onAccountChargeMenuClicked();
            } else if (clickedItem.getTitle().trim().equals("账户信息")) {
            } else if (clickedItem.getTitle().trim().equals("待支付订单")) {
                Intent intent = new Intent(getActivity(), KWaitToPayOrderActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "该功能正在维护中", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnItemClickListener onSecondaryMenuItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!checkLogin()) {
                Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            KDefpayMenuItem clickedItem = secondaryMenuList.get(position);
            if (clickedItem.getTitle().trim().equals("话费充值")) {
                Intent intent = new Intent(getActivity(), KPhonefeeChargeActivity.class);
                startActivity(intent);
            } else if (clickedItem.getTitle().trim().equals("交通罚款")) {
                Intent intent = new Intent(getActivity(), KTrafficFineActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "该功能正在维护中", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void onAccountChargeMenuClicked() {
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.BANKS_QUERY, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    KBankCardListResponseBean responseBean = JSON.parseObject(result.getContent(), KBankCardListResponseBean.class);
                    if (responseBean.getTotal() == 0) {
                        DialogProvider.alert(getActivity(), "温馨提示", "您还没有绑定任何银行卡,请先到\"我的银行卡\"模块进行绑定", "确定");
                    } else {
                        Intent intent = new Intent(getActivity(), KAccountChargeActivity.class);
                        intent.putExtra(KAccountChargeActivity.INTENT_KEY_BINED_CARD_LIST, (Serializable) responseBean.getList());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private List<KDefpayMenuItem> buildMainMenuList() {
        List<KDefpayMenuItem> menuList = new ArrayList<KDefpayMenuItem>();
        menuList.add(new KDefpayMenuItem(R.drawable.menu_account_info, "账户信息"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_wait_pay_bill, "待支付订单"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_account_charge, "账户充值"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_refund, "还款"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_mybill, "我的账单"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_mycard, "我的银行卡"));
        return menuList;
    }

    private List<KDefpayMenuItem> buildSecondaryMenuList() {
        List<KDefpayMenuItem> menuList = new ArrayList<KDefpayMenuItem>();
        menuList.add(new KDefpayMenuItem(R.drawable.menu_phone_charge, "话费充值"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_water_pay, "水电煤缴费"));
        menuList.add(new KDefpayMenuItem(R.drawable.menu_traffic_pay, "交通罚款"));
        return menuList;
    }

    private boolean checkLogin() {
        if (KBaseApplication.getInstance().getUserInfoStub() == null) {
            Intent intent = new Intent(getActivity(), KLoginPageActivity.class);
            getActivity().startActivity(intent);
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        adsView.pauseAutoChange();
        super.onPause();
    }

    @Override
    public void onResume() {
        adsView.startAutoChange();
        super.onResume();
    }
}
