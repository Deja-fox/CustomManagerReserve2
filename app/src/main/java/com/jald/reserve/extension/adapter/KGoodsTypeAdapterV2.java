package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.GoodsTypeBean;

import java.util.ArrayList;

public class KGoodsTypeAdapterV2 extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GoodsTypeBean> goodsTypesData = new ArrayList<>();

    public KGoodsTypeAdapterV2(Context context) {
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
        GoodsTypeBean item = goodsTypesData.get(position);
        if (item.isSelected()) {
            convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        TextView typeName = (TextView) convertView.findViewById(R.id.txtTypeName);
        typeName.setText(item.getType_name());
        return convertView;
    }

    public void setSelected(int position) {
        if (this.goodsTypesData != null) {
            for (int i = 0; i < goodsTypesData.size(); ++i) {
                GoodsTypeBean item = goodsTypesData.get(i);
                if (i == position) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setGoodsTypesData(ArrayList<GoodsTypeBean> goodsTypesData) {
        this.goodsTypesData = goodsTypesData;
    }
}
