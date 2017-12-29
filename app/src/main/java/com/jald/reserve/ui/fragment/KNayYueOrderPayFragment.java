package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KNanYueOrderPayRequesContextBean;
import com.jald.reserve.bean.http.request.KSmsInfoRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KOrderPayChannelResponseBean.OrderPayChannel;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.StringUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class KNayYueOrderPayFragment extends Fragment {

    public static final String ARG_KEY_ORDER_PAY_INFO = "argOrderPayInfo";


    private View mRoot;
    private CountDownTimer countDownTimer;

    private KNanYueOrderPayRequesContextBean orderPayContextInfo;

    @ViewInject(R.id.co_num)
    private TextView txtOrderNumber;

    @ViewInject(R.id.com_name)
    private TextView txtComName;

    @ViewInject(R.id.amt)
    private TextView txtAmt;

    @ViewInject(R.id.pay_channel_container)
    private ViewGroup PayChannelContainer;

    @ViewInject(R.id.pay_pannel)
    private ViewGroup payContainer;

    @ViewInject(R.id.nanyue_tips)
    private ViewGroup nanyueTips;

    @ViewInject(R.id.txtPayStyleTip)
    private TextView txtPayStypeTip;

    @ViewInject(R.id.tel_num)
    private TextView txtPhoneNumber;

    @ViewInject(R.id.pay_password)
    private EditText edtPayPassword;

    @ViewInject(R.id.sms_code)
    private EditText edtSmsCode;

    @ViewInject(R.id.get_sms_code_btn)
    private Button btnGetSmsCode;

    @ViewInject(R.id.payStyle)
    private RadioGroup payStyle;

    @ViewInject(R.id.submit)
    private Button btnNanYuePaySubmit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.orderPayContextInfo = (KNanYueOrderPayRequesContextBean) getArguments().getSerializable(ARG_KEY_ORDER_PAY_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRoot = inflater.inflate(R.layout.k_fragment_nanyue_order_pay, container, false);
        x.view().inject(this, this.mRoot);
        initUI();
        return this.mRoot;
    }

    private void initUI() {
        txtOrderNumber.setText(orderPayContextInfo.getOrderPayRequestBean().getCo_num());
        txtComName.setText(orderPayContextInfo.getOrderPayRequestBean().getCom_name());
        txtAmt.setText(orderPayContextInfo.getOrderPayRequestBean().getAmt());
        txtPhoneNumber.setText(KBaseApplication.getInstance().getUserInfoStub().getTelephone());

        List<OrderPayChannel> payChannles = orderPayContextInfo.getPayChannelInfo().getList();
        if (payChannles == null || payChannles.size() == 0) {
            PayChannelContainer.setVisibility(View.GONE);
            payContainer.setVisibility(View.GONE);
            nanyueTips.setVisibility(View.VISIBLE);
        } else {
            PayChannelContainer.setVisibility(View.VISIBLE);
            payContainer.setVisibility(View.VISIBLE);
            nanyueTips.setVisibility(View.GONE);

            // 构建支付形式列表
            payStyle.removeAllViews();
            for (int i = 0; i < payChannles.size(); ++i) {
                OrderPayChannel channel = payChannles.get(i);
                RadioButton chioce = new RadioButton(getActivity());
                RadioGroup.LayoutParams lparam = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                chioce.setLayoutParams(lparam);
                chioce.setTextColor(getResources().getColor(R.color.text));
                chioce.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                chioce.setText(channel.getName());
                chioce.setTag(channel.getCode());
                // 默认选中00贷款支付(0:贷款支付,1:白条支付,2:货到付款)
                int channelCode = Integer.parseInt(channel.getCode().trim());
                chioce.setId(channelCode);
                if (channelCode == 0) {
                    chioce.setChecked(true);
                }
                // 业务逻辑：days为0或者空就隐藏白条支付
                String days = orderPayContextInfo.getOrderPayRequestBean().getDays();
                if (channelCode == 1) {
                    if (days == null || days.trim().equals("")) {
                        chioce.setVisibility(View.GONE);
                    }
                }
                payStyle.addView(chioce);
            }
            // 默认是默认支付方式(贷款支付)的提示
            txtPayStypeTip.setText("温馨提示：请于支付成功当日22:00前到融资还款模块进行还款，逾期将产生相应的利息。");
            payStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case 0:
                            txtPayStypeTip.setText("温馨提示：请于支付成功当日22:00前到融资还款模块进行还款，逾期将产生相应的利息。");
                            break;
                        case 1:
                            txtPayStypeTip.setText("温馨提示：请于账期结束日22:00前到融资还款模块进行还款，逾期将产生相应的利息。");
                            break;
                        case 2:
                            txtPayStypeTip.setText("温馨提示：请于货物签收当日22:00前到融资还款模块进行还款，逾期将产生相应的利息。");
                            break;
                    }
                }
            });
        }
    }

    private boolean inputCheck() {
        RadioButton selected = (RadioButton) this.mRoot.findViewById(payStyle.getCheckedRadioButtonId());
        orderPayContextInfo.getOrderPayRequestBean().setPay_mode((String) selected.getTag());

        if (orderPayContextInfo.getOrderPayRequestBean().getPay_mode() == null || orderPayContextInfo.getOrderPayRequestBean().getPay_mode().equals("")) {
            Toast.makeText(getActivity(), "请选择一个支付方式", Toast.LENGTH_SHORT).show();
            return false;
        }
        String smsCode = edtSmsCode.getText().toString().trim();
        if (StringUtil.isStrEmpty(smsCode)) {
            Toast.makeText(getActivity(), "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (smsCode.length() != 6) {
            Toast.makeText(getActivity(), "短信验证码为6位", Toast.LENGTH_SHORT).show();
            return false;
        }
        orderPayContextInfo.getOrderPayRequestBean().setSms_code(smsCode);
        String payPwd = edtPayPassword.getText().toString().trim();
        if (payPwd.equals("")) {
            Toast.makeText(getActivity(), "请输入支付密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (payPwd.length() < 6) {
            Toast.makeText(getActivity(), "支付密码为6位以上", Toast.LENGTH_SHORT).show();
            return false;
        }
        orderPayContextInfo.getOrderPayRequestBean().setPassword(MD5Tools.MD5(payPwd));
        return true;
    }


    @Event(R.id.submit)
    private void onSubmit(View view) {
        if (!inputCheck()) {
            return;
        }
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
        KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.NANYUE_PAY, orderPayContextInfo.getOrderPayRequestBean(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

    @Event(R.id.get_sms_code_btn)
    private void onGetSmsCodeClick(View view) {
        String phoneNumber = this.txtPhoneNumber.getText().toString().trim();
        startTimer();
        KSmsInfoRequestBean bean = new KSmsInfoRequestBean(phoneNumber, "04");
        KHttpClient httpTools = KHttpClient.singleInstance();
        httpTools.postData(getActivity(), KHttpAdress.SEND_SMS, bean, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                Toast.makeText(getActivity(), "发送成功,请注意查收", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(180000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btnGetSmsCode.setText(millisUntilFinished / 1000 + "秒");
                    btnGetSmsCode.setClickable(false);
                }

                @Override
                public void onFinish() {
                    btnGetSmsCode.setText("获取验证码");
                    btnGetSmsCode.setClickable(true);
                }
            };
        } else {
            countDownTimer.cancel();
        }
        countDownTimer.start();
    }
}
