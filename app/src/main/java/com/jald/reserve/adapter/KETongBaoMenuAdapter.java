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
import com.jald.reserve.bean.normal.KETongBaoMenuItem;

public class KETongBaoMenuAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<KETongBaoMenuItem> menuList = new ArrayList<KETongBaoMenuItem>();

	public KETongBaoMenuAdapter(Context context) {
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_etongbao_menu_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.menuIcon = (ImageView) convertView.findViewById(R.id.menu_icon);
			holder.menuTitle = (TextView) convertView.findViewById(R.id.menu_title);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		KETongBaoMenuItem menuItem = menuList.get(position);
		holder.menuIcon.setImageResource(menuItem.getIconResId());
		holder.menuTitle.setText(menuItem.getTitle());
		return convertView;
	}

	public List<KETongBaoMenuItem> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<KETongBaoMenuItem> menuList) {
		this.menuList = menuList;
	}

}
