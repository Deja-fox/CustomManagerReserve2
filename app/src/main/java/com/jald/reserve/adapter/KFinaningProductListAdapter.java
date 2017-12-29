package com.jald.reserve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;

public class KFinaningProductListAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	public KFinaningProductListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_listview_financing_item, parent, false);
		}
		TextView pdName = (TextView) convertView.findViewById(R.id.pd_name);
		TextView pdRate = (TextView) convertView.findViewById(R.id.pd_rate);
		TextView pdDateSpan = (TextView) convertView.findViewById(R.id.pd_date_span);
		if (position == 0) {
			pdName.setText("三个月定期产品");
			pdDateSpan.setText("3个月");
			pdRate.setText("5.5%");
		} else if (position == 1) {
			pdName.setText("六个月定期产品");
			pdDateSpan.setText("6个月");
			pdRate.setText("6.0%");
		} else if (position == 2) {
			pdName.setText("十二个月定期产品");
			pdDateSpan.setText("12个月");
			pdRate.setText("7.5%");
		}
		return convertView;
	}

}
