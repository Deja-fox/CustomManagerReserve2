package com.jald.reserve.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoWaitToCommetOrderListAdapter;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class KWaitToCommetOrderFragment extends Fragment {

    public static final String ARG_KEY_TOBOCCO_WAIT_RECEIVE_ORDER_LIST_INFO = "keyToboccoWaitToReciveOrderListInfo";

    public static interface OnOrderCommetClickCallback {
        public void onOrderCommet(KTobaccoOrderListResponseBean orderDetailInfo, int position, KTobaccoOrderItem orderItem);
    }

    private View mRoot;

    @ViewInject(R.id.bill_list)
    private ListView billList;

    private KTobaccoWaitToCommetOrderListAdapter waittoCommetOrderAdapter;
    private KTobaccoOrderListResponseBean orderDetailInfo;
    private ArrayList<KTobaccoOrderItem> waittoReceiveOrderListData;

    private KTobaccoWaitToCommetOrderListAdapter.OnOrderDetailClickListener onOrderDetailClickListener;
    private OnOrderCommetClickCallback onOrderCommetClickCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.orderDetailInfo = (KTobaccoOrderListResponseBean) getArguments().getSerializable(ARG_KEY_TOBOCCO_WAIT_RECEIVE_ORDER_LIST_INFO);
        this.waittoReceiveOrderListData = orderDetailInfo.getList();
        if (this.waittoReceiveOrderListData == null) {
            this.waittoReceiveOrderListData = new ArrayList<KTobaccoOrderItem>();
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_wati_to_commet_order, container, false);
        x.view().inject(this, this.mRoot);
        this.waittoCommetOrderAdapter = new KTobaccoWaitToCommetOrderListAdapter(getActivity());
        this.waittoCommetOrderAdapter.setBillList(waittoReceiveOrderListData);
        this.billList.setAdapter(waittoCommetOrderAdapter);
        this.waittoCommetOrderAdapter.setOnOrderDetailClickListener(new KTobaccoWaitToCommetOrderListAdapter.OnOrderDetailClickListener() {
            @Override
            public void onOrderDetailClicked(KTobaccoOrderItem orderItem) {
                if (onOrderDetailClickListener != null) {
                    onOrderDetailClickListener.onOrderDetailClicked(orderItem);
                }
            }
        });
        this.waittoCommetOrderAdapter.setOnOrderCommetClickListener(new KTobaccoWaitToCommetOrderListAdapter.OnOrderCommetClickListener() {
            @Override
            public void onOrderCommet(int position, KTobaccoOrderItem orderItem) {
                if (onOrderCommetClickCallback != null) {
                    onOrderCommetClickCallback.onOrderCommet(orderDetailInfo, position, orderItem);
                    waittoCommetOrderAdapter.notifyDataSetChanged();
                }
            }
        });
        return this.mRoot;
    }

    public void setOnOrderCommetClickCallback(OnOrderCommetClickCallback onOrderCommetClickCallback) {
        this.onOrderCommetClickCallback = onOrderCommetClickCallback;
    }

    public void setOnOrderDetailClickListener(KTobaccoWaitToCommetOrderListAdapter.OnOrderDetailClickListener onOrderDetailClickListener) {
        this.onOrderDetailClickListener = onOrderDetailClickListener;
    }

}
