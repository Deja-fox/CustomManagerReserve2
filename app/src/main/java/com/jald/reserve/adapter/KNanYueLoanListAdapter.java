package com.jald.reserve.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KNanYueLoanListResponseBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KNanYueLoanListAdapter extends BaseAdapter {

    public static final String TAG = KNanYueLoanListAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<KNanYueLoanListResponseBean.KNanYueLoanItem> loanListData = new ArrayList<KNanYueLoanListResponseBean.KNanYueLoanItem>();

    public KNanYueLoanListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.loanListData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.loanListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        @Bind(R.id.account_no)
        TextView accountNo;
        @Bind(R.id.trans_time)
        TextView transTime;
        @Bind(R.id.loan_amt)
        TextView loanAmt;
        @Bind(R.id.total_balance)
        TextView totalBalance;
        @Bind(R.id.expiration_date)
        TextView expirationDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_nayue_loan_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        KNanYueLoanListResponseBean.KNanYueLoanItem item = this.loanListData.get(position);
        String accountNo = item.getAccount_no() == null ? "" : item.getAccount_no();
        String transTime = item.getTrans_time() == null ? "" : item.getTrans_time();
        String loanAmt = item.getLoan_amt() == null ? "" : item.getLoan_amt() + "元";
        String totalBalance = item.getTotal_balance() == null ? "" : item.getTotal_balance() + "元";
        String expirationDate = item.getExpiration_date() == null ? "" : item.getExpiration_date();

        holder.accountNo.setText(accountNo);
        holder.loanAmt.setText(loanAmt);
        holder.totalBalance.setText(totalBalance);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE);
        try {
            String dateStr;
            Date date = sdf.parse(transTime);
            sdf.applyPattern("yyyy-MM-dd HH:mm");
            dateStr = sdf.format(date);
            holder.transTime.setText(dateStr);
        } catch (Exception e) {
            Log.e(TAG, "解析日期格式失败");
            holder.transTime.setText(transTime);
        }

        sdf.applyPattern("yyyyMMdd");
        try {
            String dateStr;
            Date date = sdf.parse(expirationDate);
            sdf.applyPattern("yyyy-MM-dd");
            dateStr = sdf.format(date);
            holder.expirationDate.setText(dateStr);
        } catch (Exception e) {
            Log.e(TAG, "解析日期格式失败");
            holder.expirationDate.setText(expirationDate);
        }
        return convertView;
    }


    public void setLoanListData(List<KNanYueLoanListResponseBean.KNanYueLoanItem> loanListData) {
        this.loanListData = loanListData;
    }


}
