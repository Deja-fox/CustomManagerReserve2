package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KNanYueLoanPayRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KNanYueLoanListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.ui.KNanYueLoanPayActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KeyboardUtil;
import com.jald.reserve.util.ViewUtil;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class KNanYueLoanPayFragment extends Fragment {

    public static final String TAG = KNanYueLoanPayFragment.class.getSimpleName();

    public static final String ARGS_KEY_LOAN_PAY_ITEM = "ArgsKeyLoanPayItem";
    private KNanYueLoanPayActivity mParent;

    private View mRoot;

    @Bind(R.id.payAmt)
    EditText payAmt;
    @Bind(R.id.smsCode)
    EditText smsCode;
    @Bind(R.id.get_sms_code_btn)
    Button btnGetSmsCode;
    @Bind(R.id.submit)
    Button submit;

    private CountDownTimer countDownTimer;


    private KNanYueLoanListResponseBean.KNanYueLoanItem loanPayItem;


    // 相关表单数据
    private String flowNo;

    private KNanYueLoanPayRequestBean formData;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mParent = (KNanYueLoanPayActivity) getActivity();

        if (getArguments() == null) {
            Toast.makeText(mParent, "无法获得当前的贷款信息", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.loanPayItem = (KNanYueLoanListResponseBean.KNanYueLoanItem) getArguments().getSerializable(ARGS_KEY_LOAN_PAY_ITEM);
            if (this.loanPayItem == null) {
                Toast.makeText(mParent, "无法获得当前的贷款信息", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mRoot != null) {
            ViewUtil.detachFromParent(this.mRoot);
            return this.mRoot;
        }
        this.mRoot = inflater.inflate(R.layout.k_fragment_nanyue_loan_pay, container, false);
        ButterKnife.bind(this, mRoot);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fillFormData(this.loanPayItem);
    }

    private void fillFormData(KNanYueLoanListResponseBean.KNanYueLoanItem loanPayItem) {
        if (loanPayItem == null) {
            Log.e(TAG, "没有相关的贷款信息,请检查代码是否有错误");
        } else {
            this.payAmt.setText(loanPayItem.getTotal_balance());
        }
    }


    @OnClick(R.id.get_sms_code_btn)
    void onGetSmsCodeClick(View view) {
        startTimer();
        JSONObject requestJSON = new JSONObject();
        // 短信类型
        requestJSON.put("sms_type", "PTP100601");
        KHttpClient httpTools = KHttpClient.singleInstance();
        httpTools.postData(getActivity(), KHttpAdress.NANYUE_PAY_SMS, requestJSON.toJSONString(), KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (result.getContent() == null) {
                    DialogProvider.alert(mParent, "获取短信验证码错误:响应的数据无法解析");
                } else {
                    JSONObject contentJSON = JSON.parseObject(result.getContent());
                    if (contentJSON == null || contentJSON.equals("")) {
                        DialogProvider.alert(mParent, "获取短信验证码错误:响应的数据无法解析");
                    } else {
                        Toast.makeText(getActivity(), "发送成功,请注意查收", Toast.LENGTH_SHORT).show();
                        flowNo = contentJSON.getString("flow_no");
                    }
                }
            }
        });
    }

    @OnClick(R.id.submit)
    void onLoanPaySubmitClick(View view) {
        if (inputCheck()) {
            KeyboardUtil.hide(getActivity());
            BigDecimal total = new BigDecimal(formData.getAmt()).add(new BigDecimal(formData.getFee_amt()));
            DialogProvider.alert(mParent, "温馨提示", "还款总金额:" + total.toString() + "元\r\n还贷金额:" + formData.getAmt() + "元\r\n还服务费:" + formData.getFee_amt() + "元", "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            KHttpClient.singleInstance().cancel(getActivity());
                        }
                    });
                    KHttpClient httpTools = KHttpClient.singleInstance();
                    httpTools.postData(getActivity(), KHttpAdress.NANYUE_LOAN_PAY, formData, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                        @Override
                        public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                            DialogProvider.hideProgressBar();
                            if (result.getContent() == null) {
                                DialogProvider.alert(mParent, "还款错误:响应的数据无法解析");
                                return;
                            } else {
                                DialogProvider.alert(mParent, "温馨提示", "恭喜您还款成功", "确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogProvider.hideAlertDialog();
                                        mParent.finish();
                                    }
                                });
                            }
                        }

                        @Override
                        protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                            super.handleBusinessLayerFailure(responseBean, statusCode);
                        }
                    });
                }
            }, "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });

        }
    }

    private boolean inputCheck() {
        formData = new KNanYueLoanPayRequestBean();
        // 01烟草还款 02高速还款
        formData.setType("01");
        formData.setAccount_no(loanPayItem.getAccount_no());
        formData.setLoan_id(loanPayItem.getLoan_id());

        String totalPayAmt = this.payAmt.getText().toString();
        if (totalPayAmt == null || totalPayAmt.trim().equals("")) {
            Toast.makeText(getActivity(), "请输入还款金额", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String totalFee = loanPayItem.getTotal_fee_hs();
            BigDecimal shouldPayLoanAmt = new BigDecimal(totalPayAmt).subtract(new BigDecimal(totalFee));
            int ret = shouldPayLoanAmt.compareTo(BigDecimal.ZERO);
            if (ret >= 0) {
                formData.setAmt(shouldPayLoanAmt.toString());
                formData.setFee_amt(totalFee);
            } else {
                formData.setAmt("0");
                formData.setFee_amt(totalPayAmt);
            }
        }
        String smsCode = this.smsCode.getText().toString();
        if (smsCode == null || smsCode.trim().equals("")) {
            Toast.makeText(getActivity(), "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            formData.setSms_code(smsCode);
        }
        if (this.flowNo == null || this.flowNo.equals("")) {
            Toast.makeText(getActivity(), "短信流水号为空,请尝试重新获取短信验证码", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            formData.setFlow_no(this.flowNo);
        }
        return true;
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}