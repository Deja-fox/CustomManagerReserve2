package com.jald.reserve.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KTelChargeBillResponseBean.TelChargeBillItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KTelChargeBillListAdapter extends BaseAdapter {

    public static final String TAG = KTelChargeBillListAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<TelChargeBillItem> billListData = new ArrayList<TelChargeBillItem>();

    public KTelChargeBillListAdapter(Context context) {
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
        public ImageView bossLogo;
        public TextView chargeAmountInfo;
        public TextView chargeDate;
        public TextView status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tel_charge_bill_listview_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.bossLogo = (ImageView) convertView.findViewById(R.id.logo);
            holder.chargeAmountInfo = (TextView) convertView.findViewById(R.id.charge_amount_info);
            holder.chargeDate = (TextView) convertView.findViewById(R.id.charge_date);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        TelChargeBillItem item = this.billListData.get(position);
        String boss = item.getBoss().trim();
        if (boss.contains("联通")) {
            holder.bossLogo.setImageResource(R.drawable.logo_china_unicom);
        } else if (boss.contains("移动")) {
            holder.bossLogo.setImageResource(R.drawable.logo_china_mobile);
        } else {
            holder.bossLogo.setImageResource(R.drawable.logo_china_telecom);
        }
        String amount = item.getRecharge_amt();
        String telNo = item.getTel();
        holder.chargeAmountInfo.setText("充值 " + amount + "元" + "-" + telNo);
        String dateStr = item.getTrans_date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
        try {
            Date date = sdf.parse(dateStr);
            sdf.applyPattern("yyyy-MM-dd");
            dateStr = sdf.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "解析日期格式失败,需要修改代码");
        }
        holder.chargeDate.setText(dateStr);
        String statusCode = item.getStatus().trim();
        if (statusCode.equals("00")) {
            // 收单成功=>进行中
            holder.status.setText("进行中");
        } else if (statusCode.equals("01")) {
            holder.status.setText("充值失败");
        } else if (statusCode.equals("02")) {
            holder.status.setText("充值成功");
        } else {
            holder.status.setText("充值失败");
        }
        return convertView;
    }

    public void setBillListData(List<TelChargeBillItem> billListData) {
        this.billListData = billListData;
    }

}
