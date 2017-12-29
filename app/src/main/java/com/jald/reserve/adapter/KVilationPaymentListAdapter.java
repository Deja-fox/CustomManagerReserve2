package com.jald.reserve.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KViolationPaymentQueryResponseBean.ViolationPaymentItem;

public class KVilationPaymentListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<ViolationPaymentItem> paymentList;

	public KVilationPaymentListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return paymentList.size();
	}

	@Override
	public Object getItem(int position) {
		return paymentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_listitem_violation_payment_item, parent, false);
		}
		TextView regulationName = (TextView) convertView.findViewById(R.id.regulation_name);
		TextView fineAmount = (TextView) convertView.findViewById(R.id.fine_amount);
		TextView payCharge = (TextView) convertView.findViewById(R.id.pay_charge);
		TextView mlateFee = (TextView) convertView.findViewById(R.id.mlate_fee);
		ViolationPaymentItem item = this.paymentList.get(position);
		regulationName.setText(item.getRegulation_name());
		fineAmount.setText(item.getFine_amount());
		payCharge.setText(item.getPay_charge());
		mlateFee.setText(item.getMlate_fee());

		return convertView;
	}

	public List<ViolationPaymentItem> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<ViolationPaymentItem> paymentList) {
		this.paymentList = paymentList;
	}

}
