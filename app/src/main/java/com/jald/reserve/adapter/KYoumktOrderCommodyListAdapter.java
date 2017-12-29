package com.jald.reserve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KYoumktOrderListResponseBean;

import java.util.List;

public class KYoumktOrderCommodyListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<KYoumktOrderListResponseBean.KYoumktOrderItem.KYoumktCommodityItem> commdyList;

    public KYoumktOrderCommodyListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return commdyList.size();
    }

    @Override
    public Object getItem(int position) {
        return commdyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.k_listview_commdy_item, parent, false);
            viewHolder.commodityName = (TextView) convertView.findViewById(R.id.commodityName);
            viewHolder.commodityMoney = (TextView) convertView.findViewById(R.id.commodityMoney);
            viewHolder.commodityId = (TextView) convertView.findViewById(R.id.commodityId);
            viewHolder.commodityTradePrice = (TextView) convertView.findViewById(R.id.commodityTradePrice);
            viewHolder.commodityOrderCount = (TextView) convertView.findViewById(R.id.commodityOrderCount);
            viewHolder.commodityRequireCount = (TextView) convertView.findViewById(R.id.commodityRequireCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        KYoumktOrderListResponseBean.KYoumktOrderItem.KYoumktCommodityItem item = commdyList.get(position);
        if (item.getItem_id() != null) {
            viewHolder.commodityId.setText(item.getItem_id().trim());
        }else{
            viewHolder.commodityId.setText("");
        }
        viewHolder.commodityName.setText(item.getItem_name().trim());
        viewHolder.commodityMoney.setText(item.getAmt().trim() + "元");
        viewHolder.commodityTradePrice.setText(item.getPrice().trim() + "元");
        viewHolder.commodityOrderCount.setText(item.getQty_ord().trim());
        viewHolder.commodityRequireCount.setText(item.getQty_req().trim());

        return convertView;
    }

    static class ViewHolder {
        TextView commodityName;
        TextView commodityMoney;
        TextView commodityId;
        TextView commodityTradePrice;
        TextView commodityOrderCount;
        TextView commodityRequireCount;
    }

    public void setItemsList(List<KYoumktOrderListResponseBean.KYoumktOrderItem.KYoumktCommodityItem> itemsList) {
        this.commdyList = itemsList;
    }

}
