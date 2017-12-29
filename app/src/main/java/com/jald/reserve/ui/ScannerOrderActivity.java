package com.jald.reserve.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.bean.request.ScannerOrderBean;
import com.jald.reserve.extension.ui.KCustomManagerMainActivity;
import com.jald.reserve.extension.ui.fragment.KOrderListFragment;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zbw on 2017/5/9.
 * 说明：二维码扫描下单
 */

public class ScannerOrderActivity extends KBaseActivity {


    public static String EXTRA_USER_NAME = "extra_user_name";
    public static String EXTRA_USER_AC_ID = "extra_user_ac_id";
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.num_ed)
    EditText numEdit;
    @Bind(R.id.zq_rg)
    RadioGroup zqRadioGroup;
    @Bind(R.id.zq_no)
    RadioButton zqNoDay;
    @Bind(R.id.zq_30)
    RadioButton zqThreeDay;
    @Bind(R.id.zq_60)
    RadioButton zqSixDay;
    @Bind(R.id.zq_90)
    RadioButton zqNineDay;
    @Bind(R.id.confirm_btn)
    Button confirmBtn;
    private String user_name;//用户名
    private String user_tp_id;//用户推送标识
    private ScannerOrderBean orderInfo;//提交订单信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_order);
        ButterKnife.bind(this);
        user_name = getIntent().getStringExtra(EXTRA_USER_NAME);
        user_tp_id = getIntent().getStringExtra(EXTRA_USER_AC_ID);
        initView();

    }


    private void initView() {
        if (TextUtils.isEmpty(user_name)) {
            userNameTv.setText("客户");
        } else {
            userNameTv.setText(user_name);
        }
        // 两位小数输入
        numEdit.addTextChangedListener(mWatcher);
    }

    /**
     * 控制两位小数输入
     */
    private TextWatcher mWatcher = new TextWatcher() {
        private boolean isChanged = false;

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (isChanged) {// ----->如果字符未改变则返回
                return;
            }
            isChanged = true;
            if (TextUtils.isEmpty(s.toString().trim())) {
                s = "0";
            }
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    numEdit.setText(s);
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                numEdit.setText(s);
            }
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    numEdit.setText(s.subSequence(0, 1));
                }
            }
            numEdit.setSelection(numEdit.length());
            isChanged = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @OnClick(R.id.confirm_btn)
    void onChargeSubmitClick(View view) {
        String chargeAmt = this.numEdit.getText().toString().trim();
        if (chargeAmt.equals("") || (Double.parseDouble(chargeAmt)) == 0) {
            Toast.makeText(this, "请输入订单金额", Toast.LENGTH_SHORT).show();
            return;
        }
        //成功
        ArrayList<ScannerOrderBean.ShoppingCarItem> goodsItems = new ArrayList<>();
        ScannerOrderBean.ShoppingCarItem goodsItem = new ScannerOrderBean.ShoppingCarItem();
        goodsItem.setPid("0");//id
        goodsItem.setGoods_title("扫码下单虚拟商品");//标题
        goodsItem.setPrice(chargeAmt);//单价
        goodsItem.setGoods_num("1");//数量
        goodsItems.add(goodsItem);


        orderInfo = new ScannerOrderBean();
//        orderInfo.setTp_id("8744558554");//测试用户
        orderInfo.setTp_id(KBaseApplication.getInstance().getUserInfoStub().getTp_id());//客户经理tp_id
        orderInfo.setsTpId(KBaseApplication.getInstance().getUserInfoStub().getStpId());
        orderInfo.setAmount(chargeAmt);
        orderInfo.setFunction("create_scan_order");
//        orderInfo.setC_tp_id("6622225880");//测试用户
        orderInfo.setC_tp_id(user_tp_id);//客户tp_id
        int checkedId = zqRadioGroup.getCheckedRadioButtonId();
        if (checkedId == zqNoDay.getId()) {
            orderInfo.setZq_days("0");
        } else if (checkedId == zqThreeDay.getId()) {
            orderInfo.setZq_days("30");
        } else if (checkedId == zqSixDay.getId()) {
            orderInfo.setZq_days("60");
        } else if (checkedId == zqNineDay.getId()) {
            orderInfo.setZq_days("90");
        } else {
            orderInfo.setZq_days("0");
        }
        orderInfo.setGoods_list(goodsItems);
        submitOrder();
    }

    /**
     * 提交订单
     */
    private void submitOrder() {
        DialogProvider.showProgressBar(this);
        KHttpClient.singleInstance().postData(this, KHttpAdress.COMMON_INFO, orderInfo, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    DialogProvider.hideProgressBar();
                    Toast.makeText(ScannerOrderActivity.this, "订单提交成功", Toast.LENGTH_SHORT).show();
                    // 跳转到主页
                    Intent intent = new Intent(ScannerOrderActivity.this, KCustomManagerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //刷新
                    EventBus.getDefault().post(new KOrderListFragment.RefreshEvent());
                }
            }
        });
    }

}