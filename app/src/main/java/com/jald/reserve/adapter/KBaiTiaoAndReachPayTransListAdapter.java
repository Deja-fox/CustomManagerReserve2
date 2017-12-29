package com.jald.reserve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaiTiaoAndReachPayTransListResponseBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KBaiTiaoAndReachPayTransListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem> transList = new ArrayList<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem>();

    public KBaiTiaoAndReachPayTransListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return transList.size();
    }

    @Override
    public Object getItem(int position) {
        return transList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {
        @Bind(R.id.trans_id)
        TextView transId;
        @Bind(R.id.trans_no)
        TextView transNo;
        @Bind(R.id.co_num)
        TextView coNum;
        @Bind(R.id.amt)
        TextView amt;
        @Bind(R.id.last_process_date)
        TextView lastProcessDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_baitiao_and_reachpay_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem item = this.transList.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.transId.setText(item.getTrans_id());
        holder.transNo.setText(item.getTrans_no());
        holder.coNum.setText(item.getCo_num());
        holder.amt.setText(item.getAmt());
        holder.lastProcessDate.setText(item.getLast_process_date());
        return convertView;
    }


    public List<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem> getTransList() {
        return transList;
    }

    public void setTransList(List<KBaiTiaoAndReachPayTransListResponseBean.KBaiTiaoAndReachPayTransItem> transList) {
        this.transList = transList;
    }


}