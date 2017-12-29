package com.jald.reserve.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KAccountInfoResponseBean;
import com.jald.reserve.bean.http.response.KBankCardListResponseBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import java.io.Serializable;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KProxyAccountActivity extends KBaseActivity {

    private static final int REQUEST_CODE_ACCOUNT_CHARGE = 11;

    @Bind(R.id.nanyueEAccountBalance)
    TextView proxyAccountBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_proxy_account);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            this.loadAccountInfo();
        }
    }


    @OnClick(R.id.nanYueEAccountCharge)
    void onAccountChargeClick(View v) {
        onAccountChargeMenuClicked();
    }

    @OnClick(R.id.nanyueBankCard)
    void onMyBankCardClick(View v) {
        Intent intent = new Intent(this, KMyBankCardActivity.class);
        startActivity(intent);
    }

    private void loadAccountInfo() {
        DialogProvider.showProgressBar(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(KProxyAccountActivity.this);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(this, KHttpAdress.ACCOUNT_QUERY, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(),
                new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KAccountInfoResponseBean responseBean = JSON.parseObject(result.getContent(), KAccountInfoResponseBean.class);
                            String balance = responseBean.getGeneration_amt();
                            if (balance == null || balance.equals("")) {
                                proxyAccountBalance.setText("--");
                            } else if (balance.equals("0")) {
                                proxyAccountBalance.setText("0.00");
                            } else {
                                proxyAccountBalance.setText(balance);
                            }
                            if (responseBean.getAuthorize_amt() != null) {
                                // 信用额度,待用
                            }
                        }
                    }
                });
    }


    private void onAccountChargeMenuClicked() {
        DialogProvider.showProgressBar(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(KProxyAccountActivity.this);
            }
        });
        JSONObject json = new JSONObject();
        Random random = new Random();
        json.put("random", random.nextInt(99999999));
        KHttpClient.singleInstance().postData(this, KHttpAdress.BANKS_QUERY, json.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(),
                new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            KBankCardListResponseBean responseBean = JSON.parseObject(result.getContent(), KBankCardListResponseBean.class);
                            if (responseBean.getTotal() == 0) {
                                DialogProvider.alert(KProxyAccountActivity.this, "温馨提示", "您还没有绑定任何银行卡,请先到\"我的银行卡\"模块进行绑定", "确定");
                            } else {
                                Intent intent = new Intent(KProxyAccountActivity.this, KAccountChargeActivity.class);
                                intent.putExtra(KAccountChargeActivity.INTENT_KEY_BINED_CARD_LIST, (Serializable) responseBean.getList());
                                startActivityForResult(intent, REQUEST_CODE_ACCOUNT_CHARGE);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACCOUNT_CHARGE) {
            if (resultCode == RESULT_OK) {
                loadAccountInfo();
            }
        }
    }


}
