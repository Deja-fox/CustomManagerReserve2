package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.GoodsTypeResponseBean;

import java.util.ArrayList;

public class KGoodsTypeAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodsTypeResponseBean.GoodsTypeBean> goodsTypesData = new ArrayList<>();

    public KGoodsTypeAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return goodsTypesData.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsTypesData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_goods_filtert_type_item, parent, false);
        }
        GoodsTypeResponseBean.GoodsTypeBean item = goodsTypesData.get(position);
        if (item.isSelected()) {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        TextView typeName = (TextView) convertView.findViewById(R.id.txtTypeName);
        typeName.setText(item.getName());
        return convertView;
    }

    public void setSelected(int position) {
        if (this.goodsTypesData != null) {
            for (int i = 0; i < goodsTypesData.size(); ++i) {
                GoodsTypeResponseBean.GoodsTypeBean item = goodsTypesData.get(i);
                if (i == position) {
                    item.setIsSelected(true);
                } else {
                    item.setIsSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setGoodsTypesData(ArrayList<GoodsTypeResponseBean.GoodsTypeBean> goodsTypesData) {
        this.goodsTypesData = goodsTypesData;
    }
}
