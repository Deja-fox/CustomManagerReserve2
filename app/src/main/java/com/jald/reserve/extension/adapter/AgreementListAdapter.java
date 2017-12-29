package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.AgreementDetailActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AgreementListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AgreementListResponseBean.AgreementBean> customListData = new ArrayList<>();
    private CustomListResponseBean.KCustomBean customInfoBean;

    public AgreementListAdapter(Context context) {
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
        @Bind(R.id.itemLin)
        LinearLayout itemLin;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.agreement_list_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final AgreementListResponseBean.AgreementBean item = customListData.get(position);

        holder.txtCustName.setText(item.getTitle());//设置名称
        holder.txtManager.setText(item.getC_content() + "月");//设置月数
        holder.tvSumGoods.setText(item.getFeature_goods().size() + "种");//设置商品总数
        holder.tvSumPrice.setText("￥" + item.getMax_price());//设置总额度
        holder.itemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                Intent intent = new Intent(context, AgreementDetailActivity.class);
                intent.putExtra(AgreementDetailActivity.ARGU_KEY_CUSTOM_INFO, customInfoBean);
                //期货产品
                intent.putExtra(AgreementDetailActivity.ARGU_KEY_AGREEMENT_INFO, item);
                context.startActivity(intent);

            }
        });
        return convertView;
    }

    public ArrayList<AgreementListResponseBean.AgreementBean> getCustomListData() {
        return customListData;
    }

    public void setCustomListData(ArrayList<AgreementListResponseBean.AgreementBean> customListData) {
        this.customListData = customListData;
    }

    public CustomListResponseBean.KCustomBean getCustomInfoBean() {
        return customInfoBean;
    }

    public void setCustomInfoBean(CustomListResponseBean.KCustomBean customInfoBean) {
        this.customInfoBean = customInfoBean;
    }


}
