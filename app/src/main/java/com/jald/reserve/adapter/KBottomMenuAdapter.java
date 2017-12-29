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
import com.jald.reserve.bean.normal.KBottomTabItem;

public class KBottomMenuAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<KBottomTabItem> tabItemList = new ArrayList<KBottomTabItem>();

	public KBottomMenuAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return tabItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return tabItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHoler {
		public ImageView tabIcon;
		public TextView tabLabel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.self_tab_bottom_nav, parent, false);
			ViewHoler holder = new ViewHoler();
			holder.tabIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tabLabel = (TextView) convertView.findViewById(R.id.tv_icon);
			convertView.setTag(holder);
		}
		ViewHoler holder = (ViewHoler) convertView.getTag();
		KBottomTabItem item = this.tabItemList.get(position);
		holder.tabIcon.setImageResource(item.getTabIcoResId());
		holder.tabLabel.setText(item.getTabLabel());
		if (item.isSelected()) {
			holder.tabIcon.setSelected(true);
			holder.tabLabel.setSelected(true);
		} else {
			holder.tabIcon.setSelected(false);
			holder.tabLabel.setSelected(false);
		}
		return convertView;
	}

	public void setSelection(int index) {
		for (int i = 0; i < this.tabItemList.size(); ++i) {
			this.tabItemList.get(i).setSelected(false);
		}
		this.tabItemList.get(index).setSelected(true);
		notifyDataSetChanged();
	}

	public List<KBottomTabItem> getTabItemList() {
		return tabItemList;
	}

	public void setTabItemList(List<KBottomTabItem> tabItemList) {
		this.tabItemList = tabItemList;
	}

}
