package com.jald.reserve.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KAllTransBillResponseBean.KTransBillItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KCommonTransBillListAdapter extends BaseAdapter {

    public static final String TAG = KCommonTransBillListAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<KTransBillItem> billListData = new ArrayList<>();

    public KCommonTransBillListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.billListData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.billListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView txtTransType;
        public TextView txtTransDate;
        public TextView txtTransAmount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.all_trans_bill_listview_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.txtTransType = (TextView) convertView.findViewById(R.id.trans_type);
            holder.txtTransDate = (TextView) convertView.findViewById(R.id.trans_date);
            holder.txtTransAmount = (TextView) convertView.findViewById(R.id.trans_amount);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        KTransBillItem item = this.billListData.get(position);
        String transType = item.getTrans_type().trim();
        String transAmount = item.getTrans_amount();
        if (transType.equals("31")) {
            holder.txtTransType.setText("银联充值");
        } else if (transType.equals("35")) {
            holder.txtTransType.setText("话费充值");
        } else if (transType.equals("36")) {
            holder.txtTransType.setText("话费充值返现");
        } else if (transType.equals("39")) {
            holder.txtTransType.setText("银联充值手续费");
        } else if (transType.equals("37")) {
            holder.txtTransType.setText("交通罚款支付");
        } else if (transType.equals("38")) {
            holder.txtTransType.setText("预存款到系统账户");
        } else if (transType.equals("40")) {
            holder.txtTransType.setText("支付货款");
        } else if (transType.equals("41")) {
            holder.txtTransType.setText("偿还南粤贷款");
        } else if (transType.equals("42")) {
            holder.txtTransType.setText("贷款账户充值");
        } else if (transType.equals("43")) {
            holder.txtTransType.setText("南粤贷款交易");
        } else {
            holder.txtTransType.setText("未知交易类型");
        }
        holder.txtTransAmount.setText(transAmount + "元");
        String dateStr = item.getTrans_date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE);
        try {
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("yyyy-MM-dd HH:mm");
            dateStr = sdf.format(date);
            holder.txtTransDate.setText(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "解析日期格式失败,需要修改代码");
        }
        return convertView;
    }

    public void setBillListData(List<KTransBillItem> billListData) {
        this.billListData = billListData;
    }

}
