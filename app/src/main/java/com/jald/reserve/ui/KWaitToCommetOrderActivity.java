package com.jald.reserve.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoWaitToCommetOrderListAdapter;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;
import com.jald.reserve.ui.fragment.KTobaccoOrderDetailFragment;
import com.jald.reserve.ui.fragment.KWaitToCommetOrderFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.k_activity_wait_to_commet_order)
public class KWaitToCommetOrderActivity extends KBaseActivity {

    @ViewInject(R.id.title)
    private TextView txtTitle;

    private KWaitToCommetOrderFragment waitToCommmetOrderFragment;
    private KTobaccoOrderDetailFragment orderDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        this.waitToCommmetOrderFragment = new KWaitToCommetOrderFragment();
        this.orderDetailFragment = new KTobaccoOrderDetailFragment();

        // 正式版发布的时候先隐藏假数据
        //this.getDebugBillListInfo();
    }


    private void getDebugBillListInfo() {
        // 假数据
        KTobaccoOrderListResponseBean tobaccoBillListInfo = new KTobaccoOrderListResponseBean();
        tobaccoBillListInfo.setCig_lice_id("370211103832");
        tobaccoBillListInfo.setCom_id("ZGYC510322102516");
        tobaccoBillListInfo.setCom_name("日照烟草专卖局");
        tobaccoBillListInfo.setCust_name("张继文");
        tobaccoBillListInfo.setTotal(2);
        ArrayList<KTobaccoOrderItem> list = new ArrayList<KTobaccoOrderItem>();
        // 第一项
        KTobaccoOrderItem item = new KTobaccoOrderItem();
        item.setCo_num("201508160925");
        item.setCrt_date("20150816");
        item.setBorn_date("20150816");
        item.setIss_date("20150818");
        item.setStatus("40");
        item.setPmt_status("1");
        item.setAmt("1885.5");
        item.setIs_pay("1");
        item.setQty("100");

        ArrayList<KTobaccoOrderItem.KTobaccoCommodityItem> goodsList = new ArrayList<KTobaccoOrderItem.KTobaccoCommodityItem>();
        KTobaccoOrderItem.KTobaccoCommodityItem googItem = new KTobaccoOrderItem.KTobaccoCommodityItem();
        googItem.setAmt("2000");
        googItem.setItem_id("00000000016");
        googItem.setItem_name("黄鹤楼");
        googItem.setPrice("100");
        googItem.setQty_lmt("100");
        googItem.setQty_ord("20");
        googItem.setQty_req("20");
        goodsList.add(googItem);
        item.setItems(goodsList);
        list.add(item);
        // 第二项
        KTobaccoOrderItem item2 = new KTobaccoOrderItem();
        item2.setAmt("25000");
        item2.setBorn_date("20150817");
        item2.setCo_num("00000001");
        item2.setCrt_date("20150817");
        item2.setIs_pay("1");
        item2.setIss_date("20150819");
        item2.setStatus("40");
        item2.setPmt_status("1");
        item2.setQty("50");
        ArrayList<KTobaccoOrderItem.KTobaccoCommodityItem> goodsList2 = new ArrayList<KTobaccoOrderItem.KTobaccoCommodityItem>();
        KTobaccoOrderItem.KTobaccoCommodityItem googItem2 = new KTobaccoOrderItem.KTobaccoCommodityItem();
        googItem2.setAmt("1000");
        googItem2.setItem_id("00000000019");
        googItem2.setItem_name("哈德门");
        googItem2.setPrice("20");
        googItem2.setQty_lmt("500");
        googItem2.setQty_ord("50");
        googItem2.setQty_req("50");
        goodsList.add(googItem2);
        item2.setItems(goodsList2);
        list.add(item2);
        tobaccoBillListInfo.setList(list);
        jumpToOrderListPage(tobaccoBillListInfo);
    }


    private void jumpToOrderListPage(final KTobaccoOrderListResponseBean tobaccoBillListInfo) {
        Bundle args = new Bundle();
        args.putSerializable(KWaitToCommetOrderFragment.ARG_KEY_TOBOCCO_WAIT_RECEIVE_ORDER_LIST_INFO, tobaccoBillListInfo);
        waitToCommmetOrderFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, waitToCommmetOrderFragment, "waitToCommmetOrderFragment");
        transaction.commitAllowingStateLoss();
        waitToCommmetOrderFragment.setOnOrderDetailClickListener(new KTobaccoWaitToCommetOrderListAdapter.OnOrderDetailClickListener() {
            @Override
            public void onOrderDetailClicked(KTobaccoOrderItem orderItem) {
                jumpToOrderDetailPage(orderItem);
            }
        });
        waitToCommmetOrderFragment.setOnOrderCommetClickCallback(new KWaitToCommetOrderFragment.OnOrderCommetClickCallback() {
            @Override
            public void onOrderCommet(KTobaccoOrderListResponseBean orderDetailInfo, int position, KTobaccoOrderItem orderItem) {
                orderItem.setIsCommeted(true);
            }
        });
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

}
