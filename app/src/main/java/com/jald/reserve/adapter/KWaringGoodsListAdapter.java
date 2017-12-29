package com.jald.reserve.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KOutOfStockGoodsListResponseBean;
import com.jald.reserve.bean.http.response.KOutOfStockGoodsListResponseBean.KOutOfStockGoodsItem;

public class KWaringGoodsListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<KOutOfStockGoodsItem> listData = new ArrayList<KOutOfStockGoodsListResponseBean.KOutOfStockGoodsItem>();

	public KWaringGoodsListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_listview_good_warning_item, parent, false);
		}
		TextView id = (TextView) convertView.findViewById(R.id.item_id);
		TextView name = (TextView) convertView.findViewById(R.id.item_name);
		KOutOfStockGoodsItem item = this.listData.get(position);
		id.setText(item.getItem_id());
		name.setText(item.getItem_name());
		return convertView;
	}

	public void setListData(List<KOutOfStockGoodsItem> listData) {
		this.listData = listData;
	}

}
