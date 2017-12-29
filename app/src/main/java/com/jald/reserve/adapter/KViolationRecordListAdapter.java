package com.jald.reserve.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KViolationRecordQueryResponseBean.KViolationRecordBean;

public class KViolationRecordListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<KViolationRecordBean> violationRecordList = new ArrayList<KViolationRecordBean>();

	public KViolationRecordListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return violationRecordList.size();
	}

	@Override
	public Object getItem(int position) {
		return violationRecordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final KViolationRecordBean item = this.violationRecordList.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_listview_violation_record_item, parent, false);
		}
		TextView violationName = (TextView) convertView.findViewById(R.id.violation_name);
		LinearLayout canPayContainer = (LinearLayout) convertView.findViewById(R.id.checkbox_container);
		final CheckBox canPayCheckbox = (CheckBox) convertView.findViewById(R.id.canpay_checkbox);

		violationName.setText(item.getRegulation_name());
		String point = item.getPorint();
		if (point != null && !point.equals("") && !point.equals("0")) {
			canPayCheckbox.setBackgroundResource(R.drawable.icon_forbid_gray);
			canPayCheckbox.setChecked(false);
			canPayContainer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "该违章已被扣分,不能代缴罚款", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			canPayCheckbox.setBackgroundResource(R.drawable.checkbox_bg_selector_blue);
			canPayCheckbox.setChecked(item.isSelected());
			canPayContainer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					item.setSelected(!item.isSelected());
					canPayCheckbox.setChecked(item.isSelected());
				}
			});
		}
		return convertView;
	}

	public List<KViolationRecordBean> getViolationRecordList() {
		return this.violationRecordList;
	}

	public void setViolationRecordList(List<KViolationRecordBean> violationRecordList) {
		this.violationRecordList = violationRecordList;
	}

}
