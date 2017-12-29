package com.jald.reserve.extension.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.KGoodsListAdapter;
import com.jald.reserve.extension.adapter.KGoodsTypeAdapter;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.GoodsListResponseBean;
import com.jald.reserve.extension.bean.response.GoodsTypeResponseBean;
import com.jald.reserve.extension.ui.KGoodsReserveActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.util.PinyinComparator;
import com.jald.reserve.util.ValueUtil;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class KGoodsSelectFragment extends Fragment {

    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";

    private KGoodsReserveActivity mParent;

    private View mRoot;

    @Bind(R.id.lvGoodsType)
    ListView lvGoodsType;
    @Bind(R.id.lvGoodsList)
    ListView lvGoodsList;
    @Bind(R.id.btnNextStep)
    Button btnNextStep;
    @Bind(R.id.txtTotalPurchaseCount)
    TextView txtTotalPurchaseCount;
    @Bind(R.id.txtTotalPurchaseSum)
    TextView txtTotalPurchaseSum;

    private CustomListResponseBean.KCustomBean customInfoBean;

    KGoodsTypeAdapter typeAdapter;
    ArrayList<GoodsTypeResponseBean.GoodsTypeBean> goodsTypesData = new ArrayList<>();
    KGoodsListAdapter goodsAdapter;
    HashMap<String, ArrayList<GoodsListResponseBean.GoodsItem>> typeGoodsMap = new HashMap<>();

    boolean isGoodsTypeListLoaded;

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
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            return ViewUtil.detachFromParent(this.mRoot);
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_goods_select, container, false);
        ButterKnife.bind(this, this.mRoot);
        initUI();
        return this.mRoot;
    }


    void initUI() {
        this.typeAdapter = new KGoodsTypeAdapter(getActivity());
        this.goodsAdapter = new KGoodsListAdapter(getActivity());
        this.lvGoodsType.setAdapter(typeAdapter);
        this.lvGoodsList.setAdapter(goodsAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mParent.changeTitle("商品订购");
        if (!isGoodsTypeListLoaded) {
            loadGoodsTypeList();
        }
    }

    void loadGoodsTypeList() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("random", ValueUtil.getRandomString(6));
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GOODS_TYPE_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    isGoodsTypeListLoaded = true;
                    DialogProvider.hideProgressBar();
                    GoodsTypeResponseBean rspBean = JSON.parseObject(result.getContent(), GoodsTypeResponseBean.class);
                    if (rspBean.getTotal() == 0) {
                        Toast.makeText(getActivity(), "暂无商品分类信息", Toast.LENGTH_SHORT).show();
                    } else {
                        goodsTypesData = rspBean.getList();
                        typeAdapter.setGoodsTypesData(goodsTypesData);
                        typeAdapter.setSelected(0);
                        typeAdapter.notifyDataSetChanged();
                        // 加载第一类商品列表
                        lvGoodsType.performItemClick(typeAdapter.getView(0, null, null), 0, lvGoodsType.getItemIdAtPosition(0));
                    }
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }

    @OnItemClick(R.id.lvGoodsType)
    void onGoodsTypeItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.typeAdapter.setSelected(position);
        String typeId = this.goodsTypesData.get(position).getId();
        if (typeGoodsMap.get(typeId) != null) {
            goodsAdapter.setGoodsListData(typeGoodsMap.get(typeId));
            goodsAdapter.notifyDataSetInvalidated();
            return;
        } else {
            // 先清空后加载
            goodsAdapter.setGoodsListData(new ArrayList<GoodsListResponseBean.GoodsItem>());
            goodsAdapter.notifyDataSetChanged();
            this.loadGoodListByType(typeId);
        }
    }


    void loadGoodListByType(final String typeId) {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("type_id", typeId);
        postJson.put("tp_id", customInfoBean.getTp_id());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GET_GOODS_LIST_BY_TYPE, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    DialogProvider.hideProgressBar();
                    GoodsListResponseBean rspBean = JSON.parseObject(result.getContent(), GoodsListResponseBean.class);
                    if (rspBean.getTotal() == 0) {
                        Toast.makeText(getActivity(), "该分类下暂无商品", Toast.LENGTH_SHORT).show();
                        typeGoodsMap.put(typeId, new ArrayList<GoodsListResponseBean.GoodsItem>());
                    } else {
                        ArrayList<GoodsListResponseBean.GoodsItem> goodsListData = rspBean.getList();
                        typeGoodsMap.put(typeId, goodsListData);
                    }
                    goodsAdapter.setGoodsListData(typeGoodsMap.get(typeId));
                    goodsAdapter.notifyDataSetInvalidated();
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }


    public static class PurchaseGoodsChangeEvent {
    }

    @Subscriber
    void onPurchaseCarGoodsChangeEvent(PurchaseGoodsChangeEvent e) {
        int totalCount = 0;
        double totalSum = 0.0;
        for (ArrayList<GoodsListResponseBean.GoodsItem> goodsList : typeGoodsMap.values()) {
            for (GoodsListResponseBean.GoodsItem item : goodsList) {
                if (item.getPurchaseCount() != 0) {
                    totalCount += item.getPurchaseCount();
                    totalSum += (item.getPurchaseCount() * item.getPrice());
                }
            }
        }
        txtTotalPurchaseCount.setText("共" + totalCount + "件");
        txtTotalPurchaseSum.setText("合计" + totalSum + "元");
    }


    @OnClick(R.id.btnNextStep)
    void onNextStepClick(View v) {
        // 生成购物车
        KeyboardUtil.hide(getActivity());
        double totalSum = 0.0;
        ArrayList<OrderAddRequestBean.ShoppingCarItem> goodsItems = new ArrayList<>();
        for (ArrayList<GoodsListResponseBean.GoodsItem> goodsList : typeGoodsMap.values()) {
            for (GoodsListResponseBean.GoodsItem item : goodsList) {
                if (item.getPurchaseCount() != 0) {
                    double amt = item.getPurchaseCount() * item.getPrice();
                    totalSum += amt;
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
        }
        if (goodsItems.size() == 0) {
            Toast.makeText(getActivity(), "您还没有选择商品", Toast.LENGTH_SHORT).show();
            return;
        }
        orderAddRequestForm = new OrderAddRequestBean();
        orderAddRequestForm.setTp_id(customInfoBean.getTp_id());
        orderAddRequestForm.setAmt(String.valueOf(totalSum));
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
