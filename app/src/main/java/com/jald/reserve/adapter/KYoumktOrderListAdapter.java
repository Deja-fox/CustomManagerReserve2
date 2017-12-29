package com.jald.reserve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KYoumktOrderListResponseBean;

import java.util.ArrayList;
import java.util.List;

public class KYoumktOrderListAdapter extends BaseAdapter {

    public static interface OnOrderDetailClickListener {
        public void onOrderDetailClicked(KYoumktOrderListResponseBean.KYoumktOrderItem orderItem);
    }

    public static interface OnOrderPayClickListener {
        public void onOrderPay(int position, KYoumktOrderListResponseBean.KYoumktOrderItem orderItem);
    }

    private LayoutInflater inflater;
    private List<KYoumktOrderListResponseBean.KYoumktOrderItem> orderList = new ArrayList<KYoumktOrderListResponseBean.KYoumktOrderItem>();

    private OnOrderDetailClickListener onOrderDetailClickListener;
    private OnOrderPayClickListener onOrderPayClickListener;

    public KYoumktOrderListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_youmkt_waittopay_list_item, parent, false);
        }
        final Button btnPay = (Button) convertView.findViewById(R.id.btn_pay);
        TextView txtBillDate = (TextView) convertView.findViewById(R.id.born_date);
        TextView txtOrderQty = (TextView) convertView.findViewById(R.id.order_qty);
        TextView txtOrderAmt = (TextView) convertView.findViewById(R.id.order_amt);
        TextView txtStatus = (TextView) convertView.findViewById(R.id.status);
        TextView txtPmtStatus = (TextView) convertView.findViewById(R.id.pmt_status);
        final KYoumktOrderListResponseBean.KYoumktOrderItem item = this.orderList.get(position);
        txtBillDate.setText(item.getBorn_date());
        txtOrderQty.setText(item.getQty());
        txtOrderAmt.setText(item.getAmt() + "元");
        String status = item.getStatus();
        if (status.equals("20")) {
            txtStatus.setText("生效");
        } else if (status.equals("30")) {
            txtStatus.setText("审核");
        } else if (status.equals("40")) {
            txtStatus.setText("确认");
        } else if (status.equals("60")) {
            txtStatus.setText("记账");
        } else if (status.equals("90")) {
            txtStatus.setText("停止");
        } else if (status.equals("01")) {
            txtStatus.setText("计划");
        } else if (status.equals("02")) {
            txtStatus.setText("订购");
        } else if (status.equals("03")) {
            txtStatus.setText("确认");
        } else if (status.equals("0301")) {
            txtStatus.setText("预确认");
        } else if (status.equals("04")) {
            txtStatus.setText("发货");
        } else if (status.equals("08")) {
            txtStatus.setText("停止");
        } else if (status.equals("09")) {
            txtStatus.setText("完成");
        }

        String pmtStatus = item.getPmt_status();
        if (pmtStatus.equals("0")) {
            txtPmtStatus.setText("未付款");
            btnPay.setVisibility(View.VISIBLE);
        } else if (pmtStatus.equals("1")) {
            txtPmtStatus.setText("收款完成");
            btnPay.setVisibility(View.GONE);
        } else if (pmtStatus.equals("01")) {
            txtPmtStatus.setText("挂账");
            btnPay.setVisibility(View.GONE);
        } else if (pmtStatus.equals("02")) {
            txtPmtStatus.setText("未付款");
            btnPay.setVisibility(View.GONE);
        } else if (pmtStatus.equals("03")) {
            txtPmtStatus.setText("收款");
            btnPay.setVisibility(View.GONE);
        } else if (pmtStatus.equals("04")) {
            txtPmtStatus.setText("划账");
            btnPay.setVisibility(View.GONE);
        } else if (pmtStatus.equals("05")) {
            txtPmtStatus.setText("待划账");
            btnPay.setVisibility(View.GONE);
        }
        TextView txtOrderDetail = (TextView) convertView.findViewById(R.id.see_order_detail);
        if (txtOrderDetail != null) {
            txtOrderDetail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderDetailClickListener.onOrderDetailClicked(item);
                }
            });
        }
        if (onOrderPayClickListener != null) {
            btnPay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderPayClickListener.onOrderPay(position, item);
                }
            });
        }
        return convertView;
    }

    public void setOnOrderPayClickListener(OnOrderPayClickListener onOrderPayClickListener) {
        this.onOrderPayClickListener = onOrderPayClickListener;
    }

    public void setOrderList(List<KYoumktOrderListResponseBean.KYoumktOrderItem> orderList) {
        this.orderList = orderList;
    }

    public void setOnOrderDetailClickListener(OnOrderDetailClickListener onOrderDetailClickListener) {
        this.onOrderDetailClickListener = onOrderDetailClickListener;
    }

}
