package com.jald.reserve.extension.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.bean.response.OrderListResponseBean;
import com.jald.reserve.extension.ui.OrderDetailActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.SystemApiSvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KOrderListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<OrderListResponseBean.OrderBean> orderListData = new ArrayList<OrderListResponseBean.OrderBean>();

    public KOrderListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderListData.size();
    }

    @Override
    public Object getItem(int position) {
        return orderListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        @Bind(R.id.txtOrderId)
        TextView txtOrderId;
        @Bind(R.id.txtIsConfirmed)
        TextView txtIsConfirmed;
        @Bind(R.id.txtCustName)
        TextView txtCutName;
        @Bind(R.id.btnJumpToDetail)
        LinearLayout btnJumpToDetail;
        @Bind(R.id.txtAmt)
        TextView txtAmt;
        @Bind(R.id.btnDelOrder)
        TextView btnDelOrder;
        @Bind(R.id.btnContactCustom)
        TextView btnContactCustom;
        @Bind(R.id.zq_line_1)
        ImageView zqLine1;
        @Bind(R.id.zq_line_2)
        ImageView zqLine2;
        @Bind(R.id.qh_line_1)
        ImageView qhLine1;
        @Bind(R.id.qh_line_2)
        ImageView qhLine2;
        @Bind(R.id.zq_tv)
        TextView zqTv;
        @Bind(R.id.qh_tv)
        TextView qhTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.k_listview_order_item, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final OrderListResponseBean.OrderBean item = orderListData.get(position);
        holder.txtOrderId.setText(item.getCo_num());
        holder.txtCutName.setText(item.getCust_name());
        holder.txtAmt.setText(item.getAmt() + "元");
        //新增账期对应状态
        String days = item.getDays();//账期天数
        //根据账期天数调整布局
        if (days == null || new BigDecimal(days).intValue() == 0) {
            //账期天数为0
            holder.zqLine1.setVisibility(View.GONE);
            holder.zqLine2.setVisibility(View.GONE);
            holder.zqTv.setVisibility(View.GONE);
        } else {
            //账期天数不为0
            holder.zqLine1.setVisibility(View.VISIBLE);
            holder.zqLine2.setVisibility(View.VISIBLE);
            holder.qhLine1.setVisibility(View.GONE);
            holder.qhLine2.setVisibility(View.GONE);
            holder.zqTv.setVisibility(View.VISIBLE);
            holder.qhTv.setVisibility(View.GONE);
            holder.zqTv.setText("（账期:" + days + "天）");
        }
        //TODO 期货状态
        String isQh = item.getIs_feature();
        String moths = "0";
        if (item.getUser_feature() != null) {
            moths = item.getUser_feature().getFeature_month();//期货月数
        }
        //根据账期天数调整布局
        if (TextUtils.isEmpty(isQh) || new BigDecimal(isQh).intValue() == 0) {
            //期货月数为0
            holder.qhLine1.setVisibility(View.GONE);
            holder.qhLine2.setVisibility(View.GONE);
            holder.qhTv.setVisibility(View.GONE);
        } else {
            //期货月数不为0
            if (moths == null || new BigDecimal(moths).intValue() == 0) {
                //期货月数为0
                holder.qhLine1.setVisibility(View.GONE);
                holder.qhLine2.setVisibility(View.GONE);
                holder.qhTv.setVisibility(View.GONE);
            } else {
                holder.qhLine1.setVisibility(View.VISIBLE);
                holder.qhLine2.setVisibility(View.VISIBLE);
                holder.zqLine1.setVisibility(View.GONE);
                holder.zqLine2.setVisibility(View.GONE);
                holder.zqTv.setVisibility(View.GONE);
                holder.qhTv.setVisibility(View.VISIBLE);
                holder.qhTv.setText("（未来提货权：" + moths + "月）");
            }
        }
        //根据status填充状态
        String status = item.getStatus();//订单状态
        if (!TextUtils.isEmpty(status)) {
            //不为空'10' => '等待买家付款', 待支付'30' => '等待卖家发货',  已支付'50' => 已完成
            int sta = new BigDecimal(status).intValue();
            switch (sta) {
                case 10:
                    holder.txtIsConfirmed.setVisibility(View.VISIBLE);
                    holder.txtIsConfirmed.setText("待支付");
                    holder.btnDelOrder.setVisibility(View.VISIBLE);
                    break;
                case 30:
                    holder.txtIsConfirmed.setVisibility(View.VISIBLE);
                    holder.txtIsConfirmed.setText("已支付");
                    holder.btnDelOrder.setVisibility(View.GONE);
                    break;
                case 40:
                    holder.txtIsConfirmed.setVisibility(View.VISIBLE);
                    holder.txtIsConfirmed.setText("已发货");
                    holder.btnDelOrder.setVisibility(View.GONE);
                    break;
                case 50:
                    holder.txtIsConfirmed.setVisibility(View.VISIBLE);
                    holder.txtIsConfirmed.setText("已完成");
                    holder.btnDelOrder.setVisibility(View.GONE);
                    break;
                default:
                    holder.txtIsConfirmed.setVisibility(View.GONE);
                    holder.btnDelOrder.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.txtIsConfirmed.setVisibility(View.GONE);
            holder.btnDelOrder.setVisibility(View.GONE);
        }
        holder.btnJumpToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.INTENT_KEY_ORDER_INFO, item);
                context.startActivity(intent);
            }
        });
        holder.btnContactCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemApiSvc.startCallUI(context, item.getTel());
            }
        });
        holder.btnDelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProvider.alert(context, "温馨提示", "您确定要删除本订单吗?", "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProvider.hideAlertDialog();
                        doDeleteOrder(item);

                    }
                }, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProvider.hideAlertDialog();
                    }
                });
            }
        });
        return convertView;
    }

    void doDeleteOrder(final OrderListResponseBean.OrderBean order) {
        DialogProvider.showProgressBar(context);
        JSONObject postJson = new JSONObject();
        if (TextUtils.isEmpty(order.getIs_feature()) || new BigDecimal(order.getIs_feature()).intValue() == 0) {
            postJson.put("co_num", order.getCo_num());
        } else {
            postJson.put("co_num", order.getCo_num());
            if (order.getUser_feature() != null) {
                postJson.put("user_feature_id", order.getUser_feature().getId());
            }
        }
        KHttpClient.singleInstance().postData(context, KHttpAdress.SLSMAN_ORDER_DELETE, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    DialogProvider.hideProgressBar();
                    Toast.makeText(context, "订单删除成功", Toast.LENGTH_SHORT).show();
                    orderListData.remove(order);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public ArrayList<OrderListResponseBean.OrderBean> getOrderListData() {
        return orderListData;
    }

    public void setOrderListData(ArrayList<OrderListResponseBean.OrderBean> orderListData) {
        this.orderListData = orderListData;
    }


}
