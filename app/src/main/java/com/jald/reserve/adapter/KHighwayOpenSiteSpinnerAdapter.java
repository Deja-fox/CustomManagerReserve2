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
import com.jald.reserve.bean.normal.KHighwayOpenSiteBean;

public class KHighwayOpenSiteSpinnerAdapter extends BaseAdapter {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CONTENT_ITEM = 1;
	private static final int VIEW_TYPE_COUNT = 2;

	public static final String HEADER_TITLE_ID = "00000000";

	private LayoutInflater inflater;
	private Context context;
	private List<KHighwayOpenSiteBean> openSiteList = new ArrayList<KHighwayOpenSiteBean>();

	public KHighwayOpenSiteSpinnerAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return openSiteList.size();
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		KHighwayOpenSiteBean item = (KHighwayOpenSiteBean) getItem(position);
		if (item.getSiteId().equals(KHighwayOpenSiteBean.EMPTY_SITE)) {
			return VIEW_TYPE_HEADER;
		} else {
			return VIEW_TYPE_CONTENT_ITEM;
		}
	}

	@Override
	public Object getItem(int position) {
		return openSiteList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_highway_opensite_spinner_item_layout, parent, false);
		}
		TextView siteName = (TextView) convertView.findViewById(R.id.opensite_name);
		siteName.setText(openSiteList.get(position).getSiteName());
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
		convertView = inflater.inflate(R.layout.k_highway_opensite_spinner_dropdown_item_layout, parent, false);
		TextView siteName = (TextView) convertView.findViewById(R.id.opensite_name);
		KHighwayOpenSiteBean bean = openSiteList.get(position);
		siteName.setText(bean.getSiteName());
		return convertView;
	}

	public List<KHighwayOpenSiteBean> getOpenSiteList() {
		return openSiteList;
	}

	public void setOpenSiteList(List<KHighwayOpenSiteBean> openSiteList) {
		this.openSiteList = openSiteList;
	}

}
