package com.jald.reserve.extension.ui;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.extension.adapter.KOrderGoodsListAdapter;
import com.jald.reserve.extension.bean.response.OrderListResponseBean;
import com.jald.reserve.ui.KBaseActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderDetailActivity extends KBaseActivity {

    public static final String INTENT_KEY_ORDER_INFO = "KeyOrderInfo";

    @Bind(R.id.lvOrderGoodsList)
    ListView lvOrderGoodsList;

    OrderListResponseBean.OrderBean orderInfo;
    KOrderGoodsListAdapter orderGoodsAdapter;
    ArrayList<OrderListResponseBean.OrderBean.GoodsBean> goodsListData = new ArrayList<>();
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtOrderBornDate)
    TextView txtOrderBornDate;
    @Bind(R.id.txtManagerName)
    TextView txtManagerName;
    @Bind(R.id.txtTel)
    TextView txtTel;
    @Bind(R.id.txtCustName)
    TextView txtCustName;
    @Bind(R.id.txtComName)
    TextView txtComName;


    @Bind(R.id.time)
    TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderInfo = (OrderListResponseBean.OrderBean) getIntent().getSerializableExtra(INTENT_KEY_ORDER_INFO);
        if (orderInfo == null) {
            Toast.makeText(this, "没有传递订单信息到本页", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(R.layout.k_activity_order_detail);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {



        txtOrderId.setText(orderInfo.getCo_num());
        txtOrderBornDate.setText(orderInfo.getBorn_date());

        txtManagerName.setText(orderInfo.getManager());
        txtTel.setText(orderInfo.getTel());
        txtCustName.setText("买家:"+orderInfo.getCust_name());
        txtComName.setText("卖家："+orderInfo.getCom_name());

        this.orderGoodsAdapter = new KOrderGoodsListAdapter(this);
        this.orderGoodsAdapter.setGoodsListData(orderInfo.getItems());
        this.lvOrderGoodsList.setAdapter(orderGoodsAdapter);

        time.setText("下单时间:"+orderInfo.getCrt_date());
    }


}
