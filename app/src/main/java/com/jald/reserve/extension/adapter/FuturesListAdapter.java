package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.FuturesResponseBean;
import com.jald.reserve.extension.ui.FuturesDetailActivity;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FuturesListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<FuturesResponseBean.FuturesBean> customListData = new ArrayList<>();
    private CustomListResponseBean.KCustomBean customInfoBean;

    public FuturesListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return customListData.size();
    }

    @Override
    public Object getItem(int position) {
        return customListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        @Bind(R.id.txtCustName)
        TextView txtCustName;
        @Bind(R.id.btnSeeDetail)
        LinearLayout btnSeeDetail;
        @Bind(R.id.txtManager)
        TextView txtManager;
        @Bind(R.id.tvSumGoods)
        TextView tvSumGoods;
        @Bind(R.id.tvSumPrice)
        TextView tvSumPrice;
        @Bind(R.id.pb_progressbar)
        ProgressBar pb_progressbar;
        @Bind(R.id.itemLin)
        LinearLayout itemLin;
        @Bind(R.id.timeTV)
        TextView timeTV;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.futures_list_item, parent, false);
            FuturesListAdapter.ViewHolder holder = new FuturesListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        FuturesListAdapter.ViewHolder holder = (FuturesListAdapter.ViewHolder) convertView.getTag();
        final FuturesResponseBean.FuturesBean item = customListData.get(position);
        int max = new BigDecimal(item.getFeature_price()).intValue();
        int progress = new BigDecimal(item.getFeature_completed_price()).intValue();
        holder.pb_progressbar.setMax(max);
        holder.pb_progressbar.setProgress(progress);
        holder.timeTV.setText("确认时间：" + item.getAgree_time());
        holder.txtCustName.setText(item.getMarket().getTitle());//设置名称
        holder.txtManager.setText(item.getMarket().getC_content() + "月");//设置月数
        holder.tvSumGoods.setText(item.getMarket().getFeature_goods().size() + "种");//设置商品总数
        holder.tvSumPrice.setText("￥" + item.getMarket().getMax_price());//设置总额度
        holder.itemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                Intent intent = new Intent(context, FuturesDetailActivity.class);
                intent.putExtra(FuturesDetailActivity.ARGU_KEY_CUSTOM_INFO, customInfoBean);
                //期货产品
                intent.putExtra(FuturesDetailActivity.ARGU_KEY_AGREEMENT_INFO, item);
                context.startActivity(intent);

            }
        });
        return convertView;
    }

    public ArrayList<FuturesResponseBean.FuturesBean> getCustomListData() {
        return customListData;
    }

    public void setCustomListData(ArrayList<FuturesResponseBean.FuturesBean> customListData) {
        this.customListData = customListData;
    }

    public CustomListResponseBean.KCustomBean getCustomInfoBean() {
        return customInfoBean;
    }

    public void setCustomInfoBean(CustomListResponseBean.KCustomBean customInfoBean) {
        this.customInfoBean = customInfoBean;
    }


}
