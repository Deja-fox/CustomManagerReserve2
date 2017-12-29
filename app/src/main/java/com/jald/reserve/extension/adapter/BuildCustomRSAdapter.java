package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.SystemApiSvc;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zbw on 2016/11/16.
 * 说明：建立客户关系列表适配器
 */

public class BuildCustomRSAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CustomListResponseBean.KCustomBean> customListData = new ArrayList<>();

    public BuildCustomRSAdapter(Context context) {
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
        @Bind(R.id.Viewline)
        View Viewline;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.build_custom_rs_lv_item, parent, false);
            BuildCustomRSAdapter.ViewHolder holder = new BuildCustomRSAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        BuildCustomRSAdapter.ViewHolder holder = (BuildCustomRSAdapter.ViewHolder) convertView.getTag();
        final CustomListResponseBean.KCustomBean item = customListData.get(position);
        holder.txtCustName.setText(item.getStore_title());
        holder.txtManager.setText(item.getPrincipal());
        holder.txtTel.setText(item.getMobile());

        holder.btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 建立关系接口
                buildRS(item);
            }
        });
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemApiSvc.startCallUI(context, item.getMobile());
            }
        });

        return convertView;
    }

    public void setCustomListData(ArrayList<CustomListResponseBean.KCustomBean> customListData) {
        this.customListData = customListData;
        notifyDataSetInvalidated();
    }


    /**
     * 建立客户关系请求接口
     */
    private void buildRS(CustomListResponseBean.KCustomBean bean) {
        DialogProvider.showProgressBar(context);
        JSONObject postJson = new JSONObject();
        postJson.put("slsman_tp_id", KBaseApplication.getInstance().getUserInfoStub().getUuid());
        postJson.put("supply_tp_id", KBaseApplication.getInstance().getUserInfoStub().getStpId());
        postJson.put("customer_tp_id", bean.getTp_id());
        postJson.put("from", bean.getFrom());
        postJson.put("mobile", bean.getMobile());
        postJson.put("lice_id", bean.getLice_id());
        postJson.put("store_title", bean.getStore_title());
        postJson.put("principal", bean.getPrincipal());
        postJson.put("function", "manager_add_customers");
        KHttpClient.singleInstance().postData(context, KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                    JSONObject contentJson = JSON.parseObject(jsonObject1.getString("content"));
                    if (contentJson != null) {
                        //请求成功提示
                        Toast.makeText(context, contentJson.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                super.handleBusinessLayerFailure(responseBean, statusCode);
            }

            @Override
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                super.handleHttpLayerFailure(ex, isOnCallback);
            }
        });
    }
}