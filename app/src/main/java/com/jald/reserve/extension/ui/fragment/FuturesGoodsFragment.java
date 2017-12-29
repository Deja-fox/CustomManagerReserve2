package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.FuturesGoodsListAdapter;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.FuturesResponseBean;
import com.jald.reserve.extension.ui.FuturesGoodsActivity;
import com.jald.reserve.extension.ui.KCustomManagerMainActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 期货产品提交页
 */

public class FuturesGoodsFragment extends Fragment {

    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_AGREEMENT_INFO = "KeyAgreementInfo";

    // 用户筛选完成事件
    public static class GoodsFilterFinishEvent {
        public ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> filteredGoodsList;

        public GoodsFilterFinishEvent(ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> filteredGoodsList) {
            this.filteredGoodsList = filteredGoodsList;
        }
    }

    private FuturesGoodsActivity mParent;

    View mRoot;

    @Bind(R.id.searchLayout)
    LinearLayout searchLayout;
    @Bind(R.id.searchEditText)
    EditText searchEditText;
    @Bind(R.id.lvGoodsList)
    ListView lvGoodsList;
    @Bind(R.id.btnNextStep)
    Button btnNextStep;
    @Bind(R.id.txtTotalPurchaseCount)
    TextView txtTotalPurchaseCount;
    @Bind(R.id.txtTotalPurchaseSum)
    TextView txtTotalPurchaseSum;
    @Bind(R.id.dialog)
    TextView dialog;

    FuturesGoodsListAdapter goodsAdapter;

    CustomListResponseBean.KCustomBean customInfoBean;
    private FuturesResponseBean.FuturesBean getBean;
    private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData = new ArrayList<>();
    OrderAddRequestBean orderAddRequestForm;

    @Override
    public void onAttach(Activity activity) {
        this.mParent = (FuturesGoodsActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        customInfoBean = (CustomListResponseBean.KCustomBean) getArguments().getSerializable(ARGU_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(getActivity(), "没有传递客户信息", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.fragment_futures_goods, container, false);
        ButterKnife.bind(this, this.mRoot);
        EventBus.getDefault().register(this);
        //加载商品
        loadAllGoodList();
        return this.mRoot;
    }


    void initUI() {
        this.goodsAdapter = new FuturesGoodsListAdapter(getActivity());
        this.goodsAdapter.setGoodsListData(goodsListData);
        this.lvGoodsList.setAdapter(goodsAdapter);

        this.statisticsGoodsInfo();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterData(s.toString());
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                filterData("");
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // 隐藏搜索按钮
        mParent.changeTitle("未来提货权商品采集");
    }


    private void filterData(String filterStr) {
        if (TextUtils.isEmpty(filterStr)) {
            this.goodsAdapter.setGoodsListData(goodsListData);
            this.goodsAdapter.notifyDataSetInvalidated();
        } else {
            filterInBackground(filterStr);
        }
    }


    @Subscriber
    public void handleCustomFilterFinish(GoodsFilterFinishEvent filterFinishEvent) {
        this.goodsAdapter.setGoodsListData(filterFinishEvent.filteredGoodsList);
        this.goodsAdapter.notifyDataSetInvalidated();
    }

    private void filterInBackground(final String filterStr) {
        String condition = filterStr.toLowerCase();
        ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> filterDateList = new ArrayList<>();
        for (AgreementListResponseBean.AgreementBean.AgreementGoodsBean item : goodsListData) {
            boolean hasAdded = false;
            // 商品名称
            String itemName = item.getTitle().toLowerCase();
            if (itemName.contains(condition) || new CharacterParser().getSpelling(itemName).contains(condition)) {
                filterDateList.add(item);
                hasAdded = true;
            }
            // 条码
            String itemId = item.getItem_id().toLowerCase();
            if (itemId.contains(condition) || new CharacterParser().getSpelling(itemId).contains(condition)) {
                if (!hasAdded) {
                    filterDateList.add(item);
                }
            }
        }
        EventBus.getDefault().post(new GoodsFilterFinishEvent(filterDateList));
    }

    @Subscriber
    void onPurchaseCarGoodsChangeEvent(KGoodsSelectFragmentV2.PurchaseGoodsChangeEvent e) {
        statisticsGoodsInfo();
    }

    // 统计购买件数和商品数
    void statisticsGoodsInfo() {
        int totalCount = 0;
        BigDecimal totalSum = new BigDecimal("0.0");
        for (AgreementListResponseBean.AgreementBean.AgreementGoodsBean item : goodsListData) {
            if (item.getPurchaseCount() != 0) {
                totalCount += item.getPurchaseCount();

                BigDecimal count = new BigDecimal(String.valueOf(item.getPurchaseCount()));
                BigDecimal price = new BigDecimal(String.valueOf(item.getGoods_price()));
                BigDecimal amt = count.multiply(price);
                totalSum = totalSum.add(amt);
            }
        }
        txtTotalPurchaseCount.setText("共" + totalCount + "件");
        txtTotalPurchaseSum.setText("合计" + totalSum.toString() + "元");
    }


    @OnClick(R.id.btnNextStep)
    void onNextStepClick(View v) {
        // 生成购物车
        KeyboardUtil.hide(getActivity());
        BigDecimal totalSum = new BigDecimal("0.0");
        ArrayList<OrderAddRequestBean.ShoppingCarItem> goodsItems = new ArrayList<>();
        for (AgreementListResponseBean.AgreementBean.AgreementGoodsBean item : goodsListData) {
            if (item.getPurchaseCount() != 0) {
                BigDecimal count = new BigDecimal(String.valueOf(item.getPurchaseCount()));
                BigDecimal price = new BigDecimal(String.valueOf(item.getGoods_price()));
                BigDecimal amt = count.multiply(price);
                totalSum = totalSum.add(amt);

                OrderAddRequestBean.ShoppingCarItem goodsItem = new OrderAddRequestBean.ShoppingCarItem();
                goodsItem.setId(item.getId());
                goodsItem.setItem_name(item.getTitle());
                goodsItem.setPrice(String.valueOf(item.getGoods_price()));
                goodsItem.setQty_ord(String.valueOf(item.getPurchaseCount()));
                goodsItem.setAmt(String.valueOf(amt));
                goodsItem.setUm_name(item.getGoods_unit());
                goodsItem.setImg_url(item.getCover());
                goodsItems.add(goodsItem);
            }
        }
        if (goodsItems.size() == 0) {
            Toast.makeText(getActivity(), "您还没有选择商品", Toast.LENGTH_SHORT).show();
            return;
        }
        orderAddRequestForm = new OrderAddRequestBean();
        orderAddRequestForm.setTp_id(customInfoBean.getTp_id());
        orderAddRequestForm.setAmt(totalSum.toString());
        orderAddRequestForm.setUser_feature_id(getBean.getId());//协议订单id
        orderAddRequestForm.setItems(goodsItems);
        orderAddRequestForm.setStpId(KBaseApplication.getInstance().getUserInfoStub().getStpId());
        submitOrders();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 提交商品
     */
    void submitOrders() {
        DialogProvider.showProgressBar(getActivity());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.ORDER_ADD, orderAddRequestForm, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
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

    // 加载所有的商品列表
    void loadAllGoodList() {
        getBean = (FuturesResponseBean.FuturesBean) getArguments().getSerializable(ARGU_KEY_AGREEMENT_INFO);
        if (getBean == null) {
            Toast.makeText(getActivity(), "获取商品信息失败", Toast.LENGTH_SHORT).show();
            return;
        } else {
            goodsListData = getBean.getMarket().getFeature_goods();
            if (goodsListData == null || goodsListData.size() == 0) {
                Toast.makeText(getActivity(), "暂无商品", Toast.LENGTH_SHORT).show();
                goodsListData = new ArrayList<>();
            }
            initUI();
        }
    }

}
