package com.jald.reserve.extension.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.extension.adapter.FuturesDetailListAdapter;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.bean.response.FuturesResponseBean;
import com.jald.reserve.ui.KBaseActivity;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 期货额度详情页
 */
public class FuturesDetailActivity extends KBaseActivity {
    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_AGREEMENT_INFO = "KeyAgreementInfo";

    @Bind(R.id.txtCustName)
    TextView txtCustName;
    @Bind(R.id.txtTotalAmt)
    TextView txtTotalAmt;
    @Bind(R.id.lvFuturesList)
    ListView lvFuturesList;
    @Bind(R.id.tvSum)
    TextView tvSum;
    @Bind(R.id.tvDescribe)
    TextView tvDescribe;
    @Bind(R.id.timeTv)
    TextView timeTv;
    @Bind(R.id.limitProgress)
    ProgressBar limitProgress;
    @Bind(R.id.btnOrderSubmit)
    Button btnOrderSubmit;

    private CustomListResponseBean.KCustomBean customInfoBean;
    private FuturesDetailListAdapter goodsAdapter;

    private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData = new ArrayList<>();
    private FuturesResponseBean.FuturesBean getBean;
    //弹出协议内容
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futures_detail);
        customInfoBean = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(ARGU_KEY_CUSTOM_INFO);
        if (customInfoBean == null) {
            Toast.makeText(this, "没有传递客户信息", Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        loadAllGoodList();
    }

    /**
     * 根据接口设置界面
     */
    private void initUI() {
        txtCustName.setText(getBean.getMarket().getTitle());//名称
        int max = new BigDecimal(getBean.getFeature_price()).intValue();
        int progress = new BigDecimal(getBean.getFeature_completed_price()).intValue();
        limitProgress.setMax(max);
        limitProgress.setProgress(progress);
        timeTv.setText("确认时间：" + getBean.getAgree_time());
        txtTotalAmt.setText(getBean.getMarket().getC_content() + "月");//月数
        tvSum.setText("￥" + getBean.getMarket().getMax_price());//总额
        tvDescribe.setText("点击查看内容详情");//描述
//        tvDescribe.setText(Html.fromHtml(getBean.getMarket().getContent()));//描述
        goodsAdapter = new FuturesDetailListAdapter(FuturesDetailActivity.this);
        goodsAdapter.setGoodsListData(goodsListData);
        lvFuturesList.setAdapter(goodsAdapter);
        //加载popupwindow
        View view = View.inflate(FuturesDetailActivity.this, R.layout.poupwindows_detail, null);

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置popupwindow点击窗体外可以消失;但必须要给popupwindow设置背景
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //监听
        ImageView close = (ImageView) view.findViewById(R.id.close);
        TextView poupDetail = (TextView) view.findViewById(R.id.poupDetail);
        poupDetail.setText(Html.fromHtml(getBean.getMarket().getContent()));//描述
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @OnClick(R.id.tvDescribe)
    void onDetailClick(View view) {
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    //获取期货详情数据
    void loadAllGoodList() {
        getBean = (FuturesResponseBean.FuturesBean) getIntent().getSerializableExtra(ARGU_KEY_AGREEMENT_INFO);
        if (getBean == null) {
            Toast.makeText(this, "获取商品信息失败", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            goodsListData = getBean.getMarket().getFeature_goods();
            if (goodsListData == null || goodsListData.size() == 0) {
                Toast.makeText(FuturesDetailActivity.this, "暂无商品", Toast.LENGTH_SHORT).show();
                goodsListData = new ArrayList<>();
            }
            initUI();
        }
    }

    /**
     * 跳转期货产品提交页
     *
     * @param view
     */
    @OnClick(R.id.btnOrderSubmit)
    void onOrderSubmitClick(View view) {
        Intent intent = new Intent(this, FuturesGoodsActivity.class);
        intent.putExtra(FuturesGoodsActivity.INTENT_KEY_CUSTOM_INFO, customInfoBean);
        //期货产品信息
        intent.putExtra(FuturesGoodsActivity.ARGU_KEY_AGREEMENT_INFO, getBean);
        startActivity(intent);
    }
}
