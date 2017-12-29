package com.jald.reserve.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.adapter.KTobaccoOrderCommodyListAdapter;
import com.jald.reserve.bean.http.response.KTobaccoOrderListResponseBean.KTobaccoOrderItem;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class KTobaccoOrderDetailFragment extends Fragment {

	public static final String ARG_KEY_ORDER_ITEM = "argKeyorderItem";

	private View mRoot;

	@ViewInject(R.id.orderId)
	private TextView txtOrderNumber;

	@ViewInject(R.id.orderDate)
	private TextView txtBornDate;

	@ViewInject(R.id.orderCount)
	private TextView txtOrderQty;

	@ViewInject(R.id.orderMoney)
	private TextView txtOrderAmt;

	@ViewInject(R.id.commodityList)
	private ListView listCommodityList;

	private KTobaccoOrderCommodyListAdapter adapter;

	private KTobaccoOrderItem orderItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.orderItem = (KTobaccoOrderItem) getArguments().getSerializable(ARG_KEY_ORDER_ITEM);
		this.mRoot = inflater.inflate(R.layout.k_fragment_tobocco_order_detail, container, false);
		x.view().inject(this, this.mRoot);

		this.txtOrderNumber.setText(orderItem.getCo_num());
		this.txtBornDate.setText(orderItem.getBorn_date());
		this.txtOrderQty.setText(orderItem.getQty() + "条");
		this.txtOrderAmt.setText(orderItem.getAmt() + "元");

		this.adapter = new KTobaccoOrderCommodyListAdapter(getActivity());
		this.adapter.setItemsList(this.orderItem.getItems());
		this.listCommodityList.setAdapter(adapter);

		return this.mRoot;
	}
}
