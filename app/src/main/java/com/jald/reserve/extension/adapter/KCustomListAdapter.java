package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.KCustomDetailActivity;
import com.jald.reserve.util.SystemApiSvc;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KCustomListAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CustomListResponseBean.KCustomBean> customListData = new ArrayList<>();

    public KCustomListAdapter(Context context) {
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
        @Bind(R.id.txtTel)
        TextView txtTel;
        @Bind(R.id.btnCall)
        ImageView btnCall;
        @Bind(R.id.txtLetter)
        TextView txtLetter;
        @Bind(R.id.Viewline)
        View Viewline;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_custom_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final CustomListResponseBean.KCustomBean item = customListData.get(position);

        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            holder.txtLetter.setVisibility(View.VISIBLE);
            holder.txtLetter.setText(item.getSort_letter());
            holder.Viewline.setVisibility(View.GONE);
        } else {
            holder.txtLetter.setVisibility(View.GONE);
            holder.Viewline.setVisibility(View.VISIBLE);
        }
        holder.txtCustName.setText(item.getCust_name());
        holder.txtManager.setText(item.getManager());
        holder.txtTel.setText(item.getTel());

        holder.btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, KCustomDetailActivity.class);
                intent.putExtra(KCustomDetailActivity.INTENT_KEY_CUSTOM_INFO, item);
                context.startActivity(intent);
            }
        });
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemApiSvc.startCallUI(context, item.getTel());
            }
        });

        return convertView;
    }

    public ArrayList<CustomListResponseBean.KCustomBean> getCustomListData() {
        return customListData;
    }

    public void setCustomListData(ArrayList<CustomListResponseBean.KCustomBean> customListData) {
        this.customListData = customListData;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = customListData.get(i).getSort_letter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        CustomListResponseBean.KCustomBean item = customListData.get(position);
        return item.getSort_letter().charAt(0);
    }
}
