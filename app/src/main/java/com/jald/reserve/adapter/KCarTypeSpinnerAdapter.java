package com.jald.reserve.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.normal.KCarTypeBean;

public class KCarTypeSpinnerAdapter extends BaseAdapter {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CONTENT_ITEM = 1;
	private static final int VIEW_TYPE_COUNT = 1;

	public static final String HEADER_TITLE_ID = "00000000";

	private LayoutInflater inflater;
	private Context context;
	private List<KCarTypeBean> carTypeList = new ArrayList<KCarTypeBean>();

	public KCarTypeSpinnerAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return carTypeList.size();
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		KCarTypeBean item = (KCarTypeBean) getItem(position);
		if (item.getTypeId().equals(KCarTypeBean.EMPTY_TYPE)) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_CONTENT_ITEM;
		}
	}

	@Override
	public Object getItem(int position) {
		return carTypeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_car_type_spinner_item_layout, parent, false);
		}
		TextView siteName = (TextView) convertView.findViewById(R.id.car_type_name);
		siteName.setText(carTypeList.get(position).getTypeName());
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		if (viewType == VIEW_TYPE_HEADER) {
			convertView = new View(context);
			convertView.setLayoutParams(new AbsListView.LayoutParams(0, 0));
			return convertView;
		}
		convertView = inflater.inflate(R.layout.k_car_type_spinner_dropdown_item_layout, parent, false);
		TextView siteName = (TextView) convertView.findViewById(R.id.car_type_name);
		KCarTypeBean bean = carTypeList.get(position);
		siteName.setText(bean.getTypeName());
		return convertView;
	}

	public List<KCarTypeBean> getCarTypeList() {
		return carTypeList;
	}

	public void setCarTypeList(List<KCarTypeBean> carTypeList) {
		this.carTypeList = carTypeList;
	}

}
