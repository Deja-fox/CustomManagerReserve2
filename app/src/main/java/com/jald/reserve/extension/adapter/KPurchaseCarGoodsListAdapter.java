package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.extension.bean.request.OrderAddRequestBean;
import com.open.imageloader.core.DisplayImageOptions;
import com.open.imageloader.core.imageaware.ImageViewAware;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

// 购物车内的商品列表
public class KPurchaseCarGoodsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<OrderAddRequestBean.ShoppingCarItem> goodsListData;

    public KPurchaseCarGoodsListAdapter(Context context) {
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
        @Bind(R.id.txtBaiTiaoDays)
        TextView txtBaiTiaoDays;
        @Bind(R.id.JiaoBiaoContainer)
        RelativeLayout JiaoBiaoContainer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.k_listview_purchase_car_goods_item, parent, false);
        ViewHolder holder = new ViewHolder(convertView);
        OrderAddRequestBean.ShoppingCarItem item = goodsListData.get(position);
        if (item.getDays() == null || new BigDecimal(item.getDays()).intValue() == 0) {
            holder.JiaoBiaoContainer.setVisibility(View.GONE);
        } else {
            holder.txtBaiTiaoDays.setText(item.getDays() + "天");
            holder.JiaoBiaoContainer.setVisibility(View.VISIBLE);
        }
        holder.txtCount.setText("x" + item.getQty_ord());
        holder.txtItemName.setText(item.getItem_name());
        holder.txtPrice.setText(Html.fromHtml(item.getPrice() + "元/" + item.getUm_name()));
        holder.txtTotalAmt.setText(Html.fromHtml("总金额<font color='red'>" + item.getAmt() + "元</font>"));
        String imgUrl = "http://www.youmkt.com/static/upload/" + item.getImg_url() + "middle_" + item.getImage_name();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        KBaseApplication.getInstance().getImageLoader().displayImage(imgUrl, new ImageViewAware(holder.imgGoodsImage), options);
        return convertView;
    }

    public ArrayList<OrderAddRequestBean.ShoppingCarItem> getGoodsListData() {
        return goodsListData;
    }

    public void setGoodsListData(ArrayList<OrderAddRequestBean.ShoppingCarItem> goodsListData) {
        this.goodsListData = goodsListData;
    }

}
