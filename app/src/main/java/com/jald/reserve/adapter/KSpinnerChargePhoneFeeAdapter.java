package com.jald.reserve.adapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.ui.KPhonefeeChargeActivity.ChargeAmountItemBean;

public class KSpinnerChargePhoneFeeAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<ChargeAmountItemBean> amountChoiceList = new ArrayList<ChargeAmountItemBean>();

	public KSpinnerChargePhoneFeeAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return amountChoiceList.size();
	}

	@Override
	public Object getItem(int position) {
		return amountChoiceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.simple_spinner_item_layout, parent, false);
		}
		TextView priceTxt = (TextView) convertView.findViewById(R.id.select_charge_amount);
		TextView ratePriceTxt = (TextView) convertView.findViewById(R.id.ratedPrice);
		ChargeAmountItemBean item = (ChargeAmountItemBean) getItem(position);
		priceTxt.setText(item.getRecharge_amt() + "元");
		BigDecimal price = new BigDecimal(item.getRecharge_amt());
		BigDecimal returnRate = new BigDecimal(item.getReturn_amt());
		DecimalFormat format = new DecimalFormat("0.00");
		String finalPrice = format.format(price.subtract(returnRate));
		StringBuilder sb = new StringBuilder();
		sb.append("优惠价:<font color='#D2691E'>" + finalPrice + "</font>元");
		Spanned str = Html.fromHtml(sb.toString());
		ratePriceTxt.setText(str);
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		}
		CheckedTextView textView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
		ChargeAmountItemBean item = this.amountChoiceList.get(position);
		BigDecimal price = new BigDecimal(item.getRecharge_amt());
		BigDecimal returnRate = new BigDecimal(item.getReturn_amt());
		DecimalFormat format = new DecimalFormat("0.00");
		String finalPrice = format.format(price.subtract(returnRate));
		textView.setText(price + "元    (优惠价:" + finalPrice + "元)");
		return convertView;
	}

	public LayoutInflater getInflater() {
		return inflater;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	public List<ChargeAmountItemBean> getAmountChoiceList() {
		return amountChoiceList;
	}

	public void setAmountChoiceList(List<ChargeAmountItemBean> amountChoiceList) {
		this.amountChoiceList = amountChoiceList;
	}

}
