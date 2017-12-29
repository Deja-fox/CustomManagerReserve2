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
import com.jald.reserve.bean.http.response.KCommodityOrderRankResponseBean;
import com.jald.reserve.bean.http.response.KCommodityOrderRankResponseBean.KCommodityOrderInfo;

public class KCommodityOrderRankAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private KCommodityOrderRankResponseBean commodityRankInfo;
	private List<KCommodityOrderInfo> rankList;

	public KCommodityOrderRankAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return rankList.size();
	}

	@Override
	public Object getItem(int position) {
		return rankList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.k_grid_item_com_rank, parent, false);
		}
		((TextView) convertView).setText((1 + position) + ":" + rankList.get(position).getItem_name());
		return convertView;
	}

	public KCommodityOrderRankResponseBean getCommodityRankInfo() {
		return commodityRankInfo;
	}

	public void setCommodityRankInfo(KCommodityOrderRankResponseBean commodityRankInfo) {
		this.commodityRankInfo = commodityRankInfo;
		this.rankList = this.commodityRankInfo.getList();
		if (this.rankList == null) {
			this.rankList = new ArrayList<KCommodityOrderRankResponseBean.KCommodityOrderInfo>();
		}
	}

}
