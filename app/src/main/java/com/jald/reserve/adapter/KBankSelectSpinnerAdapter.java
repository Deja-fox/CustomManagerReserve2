package com.jald.reserve.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.normal.KBankItemBean;

public class KBankSelectSpinnerAdapter extends BaseAdapter {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CONTENT_ITEM = 1;
	private static final int VIEW_TYPE_COUNT = 2;

	public static final String HEADER_TITLE_ID = "00000000";

	private Context context;
	private LayoutInflater inflater;
	private List<KBankItemBean> bankList = new ArrayList<KBankItemBean>();

	public KBankSelectSpinnerAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return bankList.size();
	}

	@Override
	public Object getItem(int position) {
		return bankList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		KBankItemBean item = (KBankItemBean) getItem(position);
		if (item.getBank_id().equals(HEADER_TITLE_ID)) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_CONTENT_ITEM;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_bank_card_select_spinner_item, parent, false);
		}
		TextView bankName = (TextView) convertView.findViewById(R.id.bank_name);
		ImageView logo = (ImageView) convertView.findViewById(R.id.bank_logo);
		KBankItemBean selectedItem = (KBankItemBean) getItem(position);
		String imgName = "bank_" + selectedItem.getBank_id();
		int imgId = context.getResources().getIdentifier(imgName, "drawable", context.getPackageName());
		if (imgId == 0) {
			logo.setVisibility(View.GONE);
		} else {
			logo.setVisibility(View.VISIBLE);
			logo.setImageResource(imgId);
		}
		bankName.setText(selectedItem.getBank_name());
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		if (viewType == VIEW_TYPE_HEADER) {
			convertView = inflater.inflate(R.layout.k_bank_card_select_spinner_dropdown_header_item, parent, false);
			return convertView;
		} else {
			convertView = inflater.inflate(R.layout.k_bank_card_select_spinner_dropdown_item, parent, false);
			KBankItemBean item = (KBankItemBean) getItem(position);
			String imgName = "bank_" + item.getBank_id();
			int imgId = context.getResources().getIdentifier(imgName, "drawable", context.getPackageName());
			ImageView logo = (ImageView) convertView.findViewById(R.id.bank_logo);
			TextView bankName = (TextView) convertView.findViewById(R.id.bank_name);
			logo.setImageResource(imgId);
			bankName.setText(item.getBank_name());
			return convertView;
		}
	}

	public void setBankList(List<KBankItemBean> bankList) {
		this.bankList = bankList;
	}

}