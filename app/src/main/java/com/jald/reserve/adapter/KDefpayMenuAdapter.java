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
import com.jald.reserve.bean.normal.KDefpayMenuItem;

public class KDefpayMenuAdapter extends BaseAdapter {

	public static int VIEW_TYPE_NORMAL_ITEM = 0;
	public static int VIEW_TYPE_COUNTER_ITEM = 1;

	public static int VIEW_TYPE_COUNT = 2;

	private LayoutInflater inflater;

	private List<KDefpayMenuItem> menuList = new ArrayList<KDefpayMenuItem>();

	public KDefpayMenuAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return this.menuList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.menuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		public ImageView menuIcon;
		public TextView menuTitle;
		@SuppressWarnings("unused")
		public TextView msgBubble;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return VIEW_TYPE_NORMAL_ITEM;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == VIEW_TYPE_COUNTER_ITEM) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.k_defpay_page_menu_item_with_bubble, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.menuIcon = (ImageView) convertView.findViewById(R.id.menu_icon);
				holder.menuTitle = (TextView) convertView.findViewById(R.id.menu_title);
				holder.msgBubble = (TextView) convertView.findViewById(R.id.menu_bubble);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			KDefpayMenuItem menuItem = menuList.get(position);
			holder.menuIcon.setImageResource(menuItem.getIconResId());
			holder.menuTitle.setText(menuItem.getTitle());
			return convertView;
		} else {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.k_defpay_page_menu_item, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.menuIcon = (ImageView) convertView.findViewById(R.id.menu_icon);
				holder.menuTitle = (TextView) convertView.findViewById(R.id.menu_title);
				convertView.setTag(holder);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			KDefpayMenuItem menuItem = menuList.get(position);
			holder.menuIcon.setImageResource(menuItem.getIconResId());
			holder.menuTitle.setText(menuItem.getTitle());
			return convertView;
		}
	}

	public List<KDefpayMenuItem> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<KDefpayMenuItem> menuList) {
		this.menuList = menuList;
	}

}
