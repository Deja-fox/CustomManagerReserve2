package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.OrderListResponseBean;
import com.open.imageloader.core.DisplayImageOptions;
import com.open.imageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

// 订单内的商品列表
public class KOrderGoodsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<OrderListResponseBean.OrderBean.GoodsBean> goodsListData = new ArrayList<>();

    public KOrderGoodsListAdapter(Context context) {
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
        @Bind(R.id.imgGoodsImage)
        RoundedImageView imgGoodsImage;
        @Bind(R.id.txtItemName)
        TextView txtItemName;
        @Bind(R.id.txtTotalAmt)
        TextView txtTotalAmt;
        @Bind(R.id.txtCount)
        TextView txtCount;
        @Bind(R.id.txtPrice)
        TextView txtPrice;
        @Bind(R.id.txtSpecification)
        TextView txtSpecification;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_order_goods_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        OrderListResponseBean.OrderBean.GoodsBean item = goodsListData.get(position);
        holder.txtCount.setText(item.getQty_ord());
        holder.txtItemName.setText(item.getItem_name());
        if (TextUtils.isEmpty(item.getUm_name())) {
            holder.txtPrice.setText(Html.fromHtml(item.getPrice() + "元"));
        } else {
            holder.txtPrice.setText(Html.fromHtml(item.getPrice() + "元/" + item.getUm_name()));
        }
        holder.txtSpecification.setText(item.getSpecification());
        holder.txtTotalAmt.setText(Html.fromHtml("总金额 <font color='red'>" + item.getAmt() + "元</font>"));
        String imgUrl = "http://www.youmkt.com/static/upload/" + item.getImg_url() + "middle_" + item.getImage_name();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        KBaseApplication.getInstance().getImageLoader().displayImage(imgUrl, new ImageViewAware(holder.imgGoodsImage), options);
        return convertView;
    }


    public void setGoodsListData(ArrayList<OrderListResponseBean.OrderBean.GoodsBean> goodsListData) {
        this.goodsListData = goodsListData;
    }


}
