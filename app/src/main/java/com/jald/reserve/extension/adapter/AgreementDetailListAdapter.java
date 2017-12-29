package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
import com.open.imageloader.core.DisplayImageOptions;
import com.open.imageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 期货详情商品列表适配器
 */
public class AgreementDetailListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData = new ArrayList<>();

    public AgreementDetailListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class ViewHolder {
        @Bind(R.id.goodsImage)
        RoundedImageView goodsImage;
        @Bind(R.id.txtItemName)
        TextView txtItemName;
        @Bind(R.id.txtItemId)
        TextView txtItemId;
        @Bind(R.id.txtSpecification)
        TextView txtSpecification;
        @Bind(R.id.txtPriceAndUnit)
        TextView txtPriceAndUnit;
        @Bind(R.id.tvSum)
        TextView tvSum;


        ImageViewAware goodsImageAware;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            goodsImageAware = new ImageViewAware(goodsImage);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.agreement_detail_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(convertView);
        final AgreementListResponseBean.AgreementBean.AgreementGoodsBean item = goodsListData.get(position);
        holder.tvSum.setText(item.getGoods_num());//数量
        holder.txtItemName.setText(item.getTitle());//标题
        holder.txtPriceAndUnit.setText(item.getGoods_price() + "元/" + item.getGoods_unit());//单位
        if (item.getSpecification() == null || item.getSpecification().trim().equals("")) {
            holder.txtSpecification.setText("暂无");//规格
        } else {
            holder.txtSpecification.setText(item.getSpecification());//规格
        }
        if (item.getBar_code() == null || item.getBar_code().trim().equals("")) {
            holder.txtItemId.setText("暂无");//条码
        } else {
            holder.txtItemId.setText(item.getBar_code());//条码
        }
        String imgUrl = item.getCover();//图片
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        KBaseApplication.getInstance().getImageLoader().displayImage(imgUrl, holder.goodsImageAware, options);
        return convertView;
    }


    public void setGoodsListData(ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData) {
        this.goodsListData = goodsListData;
    }


}
