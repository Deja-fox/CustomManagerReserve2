package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
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

import com.jald.reserve.R;
import com.jald.reserve.extension.adapter.KGoodsSearchListAdapterV2;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.GoodsListResponseBeanV2;
import com.jald.reserve.extension.ui.KGoodsReserveActivity;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.util.ViewUtil;
import com.jald.reserve.widget.SideBar;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 商品搜索页
public class KGoodsSearchFragmentV2 extends Fragment {

    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_ALL_GOODS_LIST_DATA = "KeyAllGoodsListData";

    // 用户筛选完成事件
    public static class GoodsFilterFinishEvent {
        public ArrayList<GoodsListResponseBeanV2.GoodsItem> filteredGoodsList;

        public GoodsFilterFinishEvent(ArrayList<GoodsListResponseBeanV2.GoodsItem> filteredGoodsList) {
            this.filteredGoodsList = filteredGoodsList;
        }
    }

    private KGoodsReserveActivity mParent;

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
    @Bind(R.id.sideBar)
    SideBar sideBar;

    KGoodsSearchListAdapterV2 goodsAdapter;

    CustomListResponseBean.KCustomBean customInfoBean;
    ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsListData;

    OrderAddRequestBean orderAddRequestForm;

    @Override
    public void onAttach(Activity activity) {
        this.mParent = (KGoodsReserveActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        customInfoBean = (CustomListResponseBean.KCustomBean) getArguments().getSerializable(ARGU_KEY_CUSTOM_INFO);
        goodsListData = (ArrayList<GoodsListResponseBeanV2.GoodsItem>) getArguments().getSerializable(ARGU_KEY_ALL_GOODS_LIST_DATA);
        if (goodsListData == null) {
            goodsListData = new ArrayList<>();
        }
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_goods_search, container, false);
        ButterKnife.bind(this, this.mRoot);
        initUI();
        return this.mRoot;
    }


    void initUI() {

        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = goodsAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvGoodsList.setSelection(position);
                }

            }
        });

        this.goodsAdapter = new KGoodsSearchListAdapterV2(getActivity());
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
        this.mParent.hideSearch();
        mParent.changeTitle("商品搜索");
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                String condition = filterStr.toLowerCase();
                ArrayList<GoodsListResponseBeanV2.GoodsItem> filterDateList = new ArrayList<>();
                for (GoodsListResponseBeanV2.GoodsItem item : goodsListData) {
                    boolean hasAdded = false;
                    // 商品名称
                    String itemName = item.getItem_name().toLowerCase();
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
        }).start();
    }

    @Subscriber
    void onPurchaseCarGoodsChangeEvent(KGoodsSelectFragmentV2.PurchaseGoodsChangeEvent e) {
        statisticsGoodsInfo();
    }

    // 统计购买件数和商品数
    void statisticsGoodsInfo() {
        int totalCount = 0;
        BigDecimal totalSum = new BigDecimal("0.0");
        for (GoodsListResponseBeanV2.GoodsItem item : goodsListData) {
            if (item.getPurchaseCount() != 0) {
                totalCount += item.getPurchaseCount();

                BigDecimal count = new BigDecimal(String.valueOf(item.getPurchaseCount()));
                BigDecimal price = new BigDecimal(String.valueOf(item.getPrice()));
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
        for (GoodsListResponseBeanV2.GoodsItem item : goodsListData) {
            if (item.getPurchaseCount() != 0) {
                BigDecimal count = new BigDecimal(String.valueOf(item.getPurchaseCount()));
                BigDecimal price = new BigDecimal(String.valueOf(item.getPrice()));
                BigDecimal amt = count.multiply(price);
                totalSum = totalSum.add(amt);

                OrderAddRequestBean.ShoppingCarItem goodsItem = new OrderAddRequestBean.ShoppingCarItem();
                goodsItem.setId(item.getId());
                goodsItem.setItem_name(item.getItem_name());
                goodsItem.setPrice(String.valueOf(item.getPrice()));
                goodsItem.setQty_ord(String.valueOf(item.getPurchaseCount()));
                goodsItem.setAmt(String.valueOf(amt));
                goodsItem.setDays(item.getDays());
                // 额外
                goodsItem.setUm_name(item.getUm_name());
                goodsItem.setImage_name(item.getImage_name());
                goodsItem.setImg_url(item.getImg_url());
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

        orderAddRequestForm.setItems(goodsItems);

        // 判断账期是否一致给出用户提示
        OrderAddRequestBean.ShoppingCarItem firstItem = goodsItems.get(0);
        String firstDays = firstItem.getDays();
        boolean isDaysSame = true;
        for (OrderAddRequestBean.ShoppingCarItem item : goodsItems) {
            if (!item.getDays().equals(firstDays)) {
                isDaysSame = false;
                break;
            }
        }
        if (!isDaysSame) {
            String tipsMsg = "您选购的商品账期时间不一致，将无法使用账期支付。继续下单请点击确认，如想使用账期支付，请点击取消并根据商品账期分别下单。";
            DialogProvider.alert(getActivity(), "温馨提示", tipsMsg, "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    // 切换到购物车确认页
                    KPurchaseCarFragment purchaseCarFragment = new KPurchaseCarFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(KPurchaseCarFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
                    args.putSerializable(KPurchaseCarFragment.ARGU_KEY_ORDER_INFO, orderAddRequestForm);
                    purchaseCarFragment.setArguments(args);
                    mParent.changeFragment(purchaseCarFragment, true);
                }
            }, "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });
        } else {
            // 切换到购物车确认页
            KPurchaseCarFragment purchaseCarFragment = new KPurchaseCarFragment();
            Bundle args = new Bundle();
            args.putSerializable(KPurchaseCarFragment.ARGU_KEY_CUSTOM_INFO, customInfoBean);
            args.putSerializable(KPurchaseCarFragment.ARGU_KEY_ORDER_INFO, orderAddRequestForm);
            purchaseCarFragment.setArguments(args);
            mParent.changeFragment(purchaseCarFragment, true);
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
