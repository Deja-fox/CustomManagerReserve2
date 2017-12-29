package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.KPurchaseCarGoodsListAdapter;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.KCustomManagerMainActivity;
import com.jald.reserve.extension.ui.KGoodsReserveActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KPurchaseCarFragment extends Fragment {

    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_ORDER_INFO = "ArgKey_OrderInfo";


    private KGoodsReserveActivity mParent;

    @Bind(R.id.txtCustName)
    TextView txtCustName;
    @Bind(R.id.txtTotalAmt)
    TextView txtTotalAmt;
    @Bind(R.id.lvPurchaseList)
    ListView lvPurchaseList;
    @Bind(R.id.btnOrderSubmit)
    Button btnOrderSubmit;

    private View mRoot;

    private KPurchaseCarGoodsListAdapter goodsListAdapter;

    private CustomListResponseBean.KCustomBean customInfoBean;
    private OrderAddRequestBean orderInfo;

    @Override
    public void onAttach(Activity activity) {
        this.mParent = (KGoodsReserveActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.customInfoBean = (CustomListResponseBean.KCustomBean) getArguments().getSerializable(ARGU_KEY_CUSTOM_INFO);
        this.orderInfo = (OrderAddRequestBean) getArguments().getSerializable(ARGU_KEY_ORDER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_purchase_car, container, false);
        ButterKnife.bind(this, this.mRoot);
        initUI();
        return this.mRoot;
    }

    private void initUI() {
        this.txtCustName.setText(customInfoBean.getCust_name());
        this.txtTotalAmt.setText(Html.fromHtml("<font color='red'>" + orderInfo.getAmt() + "元</font>"));
        this.goodsListAdapter = new KPurchaseCarGoodsListAdapter(getActivity());
        this.goodsListAdapter.setGoodsListData(orderInfo.getItems());
        this.lvPurchaseList.setAdapter(this.goodsListAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mParent.hideSearch();
        mParent.changeTitle("购物车");
    }

    @OnClick(R.id.btnOrderSubmit)
    void onOrderSubmitClick(View view) {
        DialogProvider.showProgressBar(getActivity());
        orderInfo.setStpId(KBaseApplication.getInstance().getUserInfoStub().getStpId());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.ORDER_ADD, orderInfo, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    DialogProvider.hideProgressBar();
                    Toast.makeText(getActivity(), "订单提交成功", Toast.LENGTH_SHORT).show();
                    // 跳转到主页
                    Intent intent = new Intent(getActivity(), KCustomManagerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //刷新
                    EventBus.getDefault().post(new KOrderListFragment.RefreshEvent());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
