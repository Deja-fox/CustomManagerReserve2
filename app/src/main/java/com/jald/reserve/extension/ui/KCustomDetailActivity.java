package com.jald.reserve.extension.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.ui.KBaseActivity;
import com.jald.reserve.ui.KLocationSelectActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 客户详情
 */
public class KCustomDetailActivity extends KBaseActivity {

    public static final String INTENT_KEY_CUSTOM_INFO = "KeyCustomInfo";

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.btnGoodsReserve)
    Button btnGoodsReserve;
    @Bind(R.id.btnAgreement)
    Button btnAgreement;
    @Bind(R.id.btnFutures)
    Button btnFutures;
    @Bind(R.id.txtCustName)
    TextView txtCustName;
    @Bind(R.id.txtLiceId)
    TextView txtLiceId;
    @Bind(R.id.txtManager)
    TextView txtManager;
    @Bind(R.id.txtTel)
    TextView txtTel;
    @Bind(R.id.txtAddress)
    TextView txtAddress;
    @Bind(R.id.location_btn)
    ImageView locationBtn;

    CustomListResponseBean.KCustomBean customInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_custom_detail);
        customInfo = (CustomListResponseBean.KCustomBean) getIntent().getSerializableExtra(INTENT_KEY_CUSTOM_INFO);
        if (customInfo == null) {
            Toast.makeText(this, "传递的客户信息为空", Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        txtCustName.setText(customInfo.getCust_name());
        txtLiceId.setText(customInfo.getLice_id());
        if (customInfo.getManager() == null || customInfo.getManager().trim().equals("")) {
            txtManager.setText("暂无");
        } else {
            txtManager.setText(customInfo.getManager());
        }
        if (customInfo.getAddress() == null || customInfo.getAddress().trim().equals("")) {
            txtAddress.setText("暂无");
        } else {
            txtAddress.setText(customInfo.getAddress());
        }
        if (customInfo.getTel() == null || customInfo.getTel().trim().equals("")) {
            txtTel.setText("暂无");
        } else {
            txtTel.setText(customInfo.getTel());
        }
    }

    @OnClick(R.id.btnGoodsReserve)
    void onGoodsReserveClick(View v) {
        Intent intent = new Intent(this, KGoodsReserveActivity.class);
        intent.putExtra(KGoodsReserveActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
        startActivity(intent);
    }

    @OnClick(R.id.location_btn)
    void onLocationBtnClick(View v) {
        Intent intent = new Intent(this, KLocationSelectActivity.class);
        intent.putExtra(KLocationSelectActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
        startActivity(intent);
    }

    /**
     * 期货采集
     *
     * @param v
     */
    @OnClick(R.id.btnFutures)
    void onFuturesClick(View v) {
        Intent intent = new Intent(this, FuturesListActivity.class);
        intent.putExtra(FuturesListActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
        startActivity(intent);
    }

    /**
     * 协议采集
     *
     * @param v
     */
    @OnClick(R.id.btnAgreement)
    void onAgreementClick(View v) {
        Intent intent = new Intent(this, AgreementListActivity.class);
        intent.putExtra(AgreementListActivity.INTENT_KEY_CUSTOM_INFO, customInfo);
        startActivity(intent);
    }

}
