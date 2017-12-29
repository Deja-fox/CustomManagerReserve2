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
import com.jald.reserve.extension.adapter.KGoodsListAdapterV2;
import com.jald.reserve.extension.adapter.KGoodsTypeAdapterV2;
import com.jald.reserve.extension.bean.GoodsTypeBean;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.GoodsListResponseBeanV2;
import com.jald.reserve.extension.ui.KGoodsReserveActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CharacterParser;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.util.PinyinComparatorGood;
import com.jald.reserve.util.ViewUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

// 新版订货
public class KGoodsSelectFragmentV2 extends Fragment {

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

    KGoodsTypeAdapterV2 typeAdapter;
    KGoodsListAdapterV2 goodsAdapter;

    ArrayList<GoodsListResponseBeanV2.GoodsItem> allGoodsListData;
    HashMap<GoodsTypeBean, ArrayList<GoodsListResponseBeanV2.GoodsItem>> typeGoodsMap = new HashMap<>();

    boolean isGoodsListLoaded;

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
        this.typeAdapter = new KGoodsTypeAdapterV2(getActivity());
        this.lvGoodsType.setAdapter(typeAdapter);

        this.goodsAdapter = new KGoodsListAdapterV2(getActivity());
        this.lvGoodsList.setAdapter(goodsAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // 显示搜索按钮
        this.mParent.showSearch();
        mParent.changeTitle("商品订购");
        if (!isGoodsListLoaded) {
            loadAllGoodList();
        }
    }


    // 加载所有的商品列表
    void loadAllGoodList() {
        DialogProvider.showProgressBar(getActivity());
        JSONObject postJson = new JSONObject();
        postJson.put("tp_id", customInfoBean.getTp_id());
        postJson.put("stpId",KBaseApplication.getInstance().getUserInfoStub().getStpId());
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.GET_ALL_GOODS_LIST, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    DialogProvider.hideProgressBar();
                    isGoodsListLoaded = true;

                    GoodsListResponseBeanV2 rspBean = JSON.parseObject(result.getContent(), GoodsListResponseBeanV2.class);
                    if (rspBean.getTotal() == 0) {
                        Toast.makeText(getActivity(), "暂无商品可售", Toast.LENGTH_SHORT).show();
                        allGoodsListData = new ArrayList<>();
                        typeGoodsMap.clear();
                    } else {
                        allGoodsListData = rspBean.getList();
                        generatorPinYinIndex(allGoodsListData);
                        Collections.sort(allGoodsListData, new PinyinComparatorGood());
                        buildTypeGoodsMap(rspBean);
                    }
                    // 类别数据
                    ArrayList<GoodsTypeBean> goodsTypeList = new ArrayList<>();
                    for (GoodsTypeBean type : typeGoodsMap.keySet()) {
                        goodsTypeList.add(0, type);
                    }
                    typeAdapter.setGoodsTypesData(goodsTypeList);
                    if (goodsTypeList.size() != 0) {
                        typeAdapter.setSelected(0);
                        typeAdapter.notifyDataSetInvalidated();
                        // 显示第一类商品列表
                        lvGoodsType.performItemClick(typeAdapter.getView(0, null, null), 0, lvGoodsType.getItemIdAtPosition(0));
                    }
                } else {
                    DialogProvider.hideProgressBar();
                }
            }
        });
    }

    private void generatorPinYinIndex(ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsItemsData) {
        if (goodsItemsData == null) {
            return;
        }
        for (int i = 0; i < goodsItemsData.size(); i++) {
            GoodsListResponseBeanV2.GoodsItem goodsItemBean = goodsItemsData.get(i);
            String pinyin = new CharacterParser().getSpelling(goodsItemBean.getItem_name());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                goodsItemBean.setSort_letter(sortString.toUpperCase());
            } else {
                goodsItemBean.setSort_letter("#");
            }
        }
    }


    @OnItemClick(R.id.lvGoodsType)
    void onGoodsTypeItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.typeAdapter.setSelected(position);
        GoodsTypeBean type = (GoodsTypeBean) this.typeAdapter.getItem(position);
        if (typeGoodsMap.get(type) != null) {
            goodsAdapter.setGoodsListData(typeGoodsMap.get(type));
            goodsAdapter.notifyDataSetInvalidated();
        } else {
            goodsAdapter.setGoodsListData(new ArrayList<GoodsListResponseBeanV2.GoodsItem>());
            goodsAdapter.notifyDataSetChanged();
        }
    }


    // 按商品分类分拣商品到字典
    void buildTypeGoodsMap(GoodsListResponseBeanV2 rspBean) {
        ArrayList<GoodsListResponseBeanV2.GoodsItem> list = rspBean.getList();
        if (list == null) {
            typeGoodsMap = new HashMap<>();
        }
        for (GoodsListResponseBeanV2.GoodsItem goods : list) {
            GoodsTypeBean typeKey = new GoodsTypeBean(goods.getType_id(), goods.getType_name());
            if (typeGoodsMap.get(typeKey) == null) {
                ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsList = new ArrayList<>();
                goodsList.add(goods);
                typeGoodsMap.put(typeKey, goodsList);
            } else {
                typeGoodsMap.get(typeKey).add(goods);
            }
        }
    }


    public static class GoodsSearchEvent {
    }

    @Subscriber
    void onGoodsSearchClick(GoodsSearchEvent e) {
        // 切换到搜索页
        KGoodsSearchFragmentV2 searchFragment = new KGoodsSearchFragmentV2();
        Bundle args = new Bundle();
        args.putSerializable(KGoodsSearchFragmentV2.ARGU_KEY_CUSTOM_INFO, customInfoBean);
        args.putSerializable(KGoodsSearchFragmentV2.ARGU_KEY_ALL_GOODS_LIST_DATA, allGoodsListData);
        searchFragment.setArguments(args);
        mParent.changeFragment(searchFragment, true);
    }


    public static class PurchaseGoodsChangeEvent {
    }

    @Subscriber
    void onPurchaseCarGoodsChangeEvent(PurchaseGoodsChangeEvent e) {
        int totalCount = 0;
        BigDecimal totalSum = new BigDecimal("0.0");
        for (ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsList : typeGoodsMap.values()) {
            for (GoodsListResponseBeanV2.GoodsItem item : goodsList) {
                if (item.getPurchaseCount() != 0) {
                    totalCount += item.getPurchaseCount();

                    BigDecimal count = new BigDecimal(String.valueOf(item.getPurchaseCount()));
                    BigDecimal price = new BigDecimal(String.valueOf(item.getPrice()));
                    BigDecimal amt = count.multiply(price);
                    totalSum = totalSum.add(amt);
                }
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
        for (ArrayList<GoodsListResponseBeanV2.GoodsItem> goodsList : typeGoodsMap.values()) {
            for (GoodsListResponseBeanV2.GoodsItem item : goodsList) {
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
