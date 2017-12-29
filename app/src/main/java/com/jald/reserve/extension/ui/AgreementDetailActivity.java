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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.adapter.AgreementDetailListAdapter;
import com.jald.reserve.extension.bean.response.AgreementListResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.extension.ui.fragment.KOrderListFragment;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KBaseActivity;
import com.jald.reserve.util.DialogProvider;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 协议提交页
 */
public class AgreementDetailActivity extends KBaseActivity {
    public static final String ARGU_KEY_CUSTOM_INFO = "KeyCustomInfo";
    public static final String ARGU_KEY_AGREEMENT_INFO = "KeyAgreementInfo";

    @Bind(R.id.txtCustName)
    TextView txtCustName;
    @Bind(R.id.txtTotalAmt)
    TextView txtTotalAmt;
    @Bind(R.id.lvFuturesList)
    ListView lvFuturesList;
    @Bind(R.id.btnOrderSubmit)
    Button btnOrderSubmit;
    @Bind(R.id.tvSum)
    TextView tvSum;
    @Bind(R.id.tvDescribe)
    TextView tvDescribe;

    private CustomListResponseBean.KCustomBean customInfoBean;
    private AgreementDetailListAdapter goodsAdapter;

    private ArrayList<AgreementListResponseBean.AgreementBean.AgreementGoodsBean> goodsListData = new ArrayList<>();
    private AgreementListResponseBean.AgreementBean getBean;
    private KHttpClient httpClient = null;
    //弹出协议内容
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_detail);
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
        txtCustName.setText(getBean.getTitle());//名称
        txtTotalAmt.setText(getBean.getC_content() + "月");//月数
        tvSum.setText("￥" + getBean.getMax_price());//总额
        tvDescribe.setText("点击查看内容详情");//描述
//        tvDescribe.setText(Html.fromHtml(getBean.getContent()));//描述
        goodsAdapter = new AgreementDetailListAdapter(AgreementDetailActivity.this);
        goodsAdapter.setGoodsListData(goodsListData);
        lvFuturesList.setAdapter(goodsAdapter);
        //加载popupwindow
        View view = View.inflate(AgreementDetailActivity.this, R.layout.poupwindows_detail, null);

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置popupwindow点击窗体外可以消失;但必须要给popupwindow设置背景
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //监听
        ImageView close = (ImageView) view.findViewById(R.id.close);
        TextView poupDetail = (TextView) view.findViewById(R.id.poupDetail);
        poupDetail.setText(Html.fromHtml(getBean.getContent()));//描述
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

    /**
     * 提交协议
     *
     * @param view
     */
    @OnClick(R.id.btnOrderSubmit)
    void onOrderSubmitClick(View view) {
        if (httpClient == null) {
            httpClient = KHttpClient.singleInstance();
        } else {
            httpClient.cancel(AgreementDetailActivity.this);
            httpClient = null;
            httpClient = KHttpClient.singleInstance();
        }
        DialogProvider.showProgressBar(AgreementDetailActivity.this);
        JSONObject postJson = new JSONObject();
        postJson.put("manager_tp_id", KBaseApplication.getInstance().getUserInfoStub().getUuid());
        postJson.put("customer_tp_id", customInfoBean.getTp_id());
        postJson.put("feature_id", getBean.getId());
        postJson.put("function", "send_feature_to_customer");
        httpClient.postData(AgreementDetailActivity.this, KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                    String contentJson = jsonObject1.getString("content");
                    if (contentJson != null) {
                        AgreementListResponseBean rspBean = JSON.parseObject(contentJson, AgreementListResponseBean.class);
                        if (rspBean.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            //协议发送成功
                            Toast.makeText(AgreementDetailActivity.this, "协议签订成功，等待客户确认", Toast.LENGTH_SHORT).show();
                            // 跳转到主页
                            Intent intent = new Intent(AgreementDetailActivity.this, KCustomManagerMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //刷新
                            EventBus.getDefault().post(new KOrderListFragment.RefreshEvent());
                        } else {
                            Toast.makeText(AgreementDetailActivity.this, rspBean.getRet_msg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AgreementDetailActivity.this, result.getRet_msg(), Toast.LENGTH_SHORT).show();
                }
                DialogProvider.hideProgressBar();
            }
        });
    }

    //获取期货详情数据
    void loadAllGoodList() {
        getBean = (AgreementListResponseBean.AgreementBean) getIntent().getSerializableExtra(ARGU_KEY_AGREEMENT_INFO);
        if (getBean == null) {
            Toast.makeText(this, "获取商品信息失败", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            goodsListData = getBean.getFeature_goods();
            if (goodsListData == null || goodsListData.size() == 0) {
                Toast.makeText(AgreementDetailActivity.this, "暂无商品", Toast.LENGTH_SHORT).show();
                goodsListData = new ArrayList<>();
            }
            initUI();
        }
    }
}
