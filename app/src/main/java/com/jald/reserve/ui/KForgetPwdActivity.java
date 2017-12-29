package com.jald.reserve.ui;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.BuildConfig;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KResetLoginPwdRequestBean;
import com.jald.reserve.bean.http.request.KSmsInfoRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KUserInfoQueryResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.RSAUtil;
import com.jald.reserve.util.StringUtil;
import com.jald.reserve.util.ValueUtil;
import com.jald.reserve.util.ViewUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KForgetPwdActivity extends KBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_activity_forget_pwd);

        if (savedInstanceState == null) {
            changeFragment(new KForgetPwdFirstFragment(), false);
        }
    }

    public void changeFragment(Fragment fragment, boolean isAddToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        if (isAddToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


    // FirstPage
    public static class KForgetPwdFirstFragment extends Fragment {

        @Bind(R.id.edtUserId)
        EditText edtUserId;
        @Bind(R.id.btnNextStep)
        Button btnNextStep;
        private View mRoot;

        private KForgetPwdActivity mParent;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.mParent = (KForgetPwdActivity) activity;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (this.mRoot != null) {
                return ViewUtil.detachFromParent(this.mRoot);
            }
            this.mRoot = inflater.inflate(R.layout.k_forget_pwd_firsts_step, container, false);
            ButterKnife.bind(this, this.mRoot);
            return this.mRoot;
        }

        @OnClick(R.id.btnNextStep)
        void onNextStepClick(View v) {
            String userID = edtUserId.getText().toString().trim();
            if (userID.equals("")) {
                Toast.makeText(mParent, "请输入手机号或烟草专卖证号", Toast.LENGTH_LONG).show();
                return;
            }
            DialogProvider.showProgressBar(mParent, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            JSONObject postJson = new JSONObject();
            postJson.put("user_id", userID);
            // UUID:system
            KUserInfoStub userInfoStub = new KUserInfoStub();
            KHttpClient.singleInstance().postData(mParent, KHttpAdress.USER_INFO_QUERY, postJson, userInfoStub, new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        KUserInfoQueryResponseBean rspBean = JSON.parseObject(result.getContent(), KUserInfoQueryResponseBean.class);
                        // 跳转到下一个页面
                        KForgetPwdSecondFragment secondFragment = new KForgetPwdSecondFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(KForgetPwdSecondFragment.ARG_KEY_USER_QUERY_INFO_BEAN, rspBean);
                        secondFragment.setArguments(args);
                        mParent.changeFragment(secondFragment, true);
                    }
                }
            });
        }
    }


    // SecondPage
    public static class KForgetPwdSecondFragment extends Fragment {

        public static final String ARG_KEY_USER_QUERY_INFO_BEAN = "ArgKeyUserQueryInfoBean";
        @Bind(R.id.txtCustName)
        TextView txtCustName;
        @Bind(R.id.txtIdNumber)
        TextView txtIdNumber;
        @Bind(R.id.txtManager)
        TextView txtManager;
        @Bind(R.id.txtCoNum)
        TextView txtCoNum;
        @Bind(R.id.txtBornDate)
        TextView txtBornDate;
        @Bind(R.id.edtTelephone)
        EditText edtTelephone;
        @Bind(R.id.edtSmsCode)
        EditText edtSmsCode;
        @Bind(R.id.btnGetSmsCode)
        Button btnGetSmsCode;
        @Bind(R.id.edtAmt)
        EditText edtAmt;
        @Bind(R.id.edtPassword)
        EditText edtPassword;
        @Bind(R.id.btnSubmit)
        Button btnSubmit;
        @Bind(R.id.coNumContainer)
        LinearLayout coNumContainer;
        @Bind(R.id.bornDateContainer)
        LinearLayout bornDateContainer;
        @Bind(R.id.amtContainer)
        LinearLayout amtContainer;
        @Bind(R.id.edtRePassword)
        EditText edtRePassword;

        private View mRoot;
        private KForgetPwdActivity mParent;

        private KUserInfoQueryResponseBean userQueryInfoBean;

        private CountDownTimer countDownTimer;

        private KResetLoginPwdRequestBean requestFormData;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.mParent = (KForgetPwdActivity) activity;
            if (getArguments() != null) {
                this.userQueryInfoBean = (KUserInfoQueryResponseBean) getArguments().getSerializable(ARG_KEY_USER_QUERY_INFO_BEAN);
            }
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (this.mRoot != null) {
                return ViewUtil.detachFromParent(this.mRoot);
            }
            this.mRoot = inflater.inflate(R.layout.k_forget_pwd_second_step, container, false);
            ButterKnife.bind(this, this.mRoot);
            initUI();
            return this.mRoot;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            initUI();
        }

        private void initUI() {
            if (this.userQueryInfoBean != null) {
                if (userQueryInfoBean.getCust_name() != null) {
                    txtCustName.setText(userQueryInfoBean.getCust_name());
                }
                if (userQueryInfoBean.getId_number() != null) {
                    txtIdNumber.setText(userQueryInfoBean.getId_number());
                }
                if (userQueryInfoBean.getBorn_date() != null) {
                    txtBornDate.setText(userQueryInfoBean.getBorn_date());
                }
                if (userQueryInfoBean.getCo_num() != null) {
                    txtCoNum.setText(userQueryInfoBean.getCo_num());
                }
                if (userQueryInfoBean.getManager() != null) {
                    txtManager.setText(userQueryInfoBean.getManager());
                }
                if (userQueryInfoBean.getTelephone() != null) {
                    edtTelephone.getText().clear();
                    edtTelephone.getText().append(userQueryInfoBean.getTelephone());
                    // 有手机号的时候订单金额设置为0
                    edtAmt.getText().clear();
                    edtAmt.getText().append("0");
                    // 隐藏订单先关的东西
                    coNumContainer.setVisibility(View.GONE);
                    amtContainer.setVisibility(View.GONE);
                    bornDateContainer.setVisibility(View.GONE);
                } else {
                    coNumContainer.setVisibility(View.VISIBLE);
                    amtContainer.setVisibility(View.VISIBLE);
                    bornDateContainer.setVisibility(View.VISIBLE);
                }
            }
        }

        @OnClick(R.id.btnSubmit)
        void onSubmitClick(View v) {
            if (inputCheck()) {
                KHttpClient httpTools = KHttpClient.singleInstance();
                KUserInfoStub userInfoStub = new KUserInfoStub();
                userInfoStub.setUuid(userQueryInfoBean.getUuid());
                httpTools.postData(getActivity(), KHttpAdress.RESET_LOGIN_PWD, requestFormData, userInfoStub, new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        if (isReturnSuccess) {
                            Toast.makeText(getActivity(), "修改登录密码成功", Toast.LENGTH_SHORT).show();
                            mParent.finish();
                        }
                    }
                });
            }
        }

        private boolean inputCheck() {
            requestFormData = new KResetLoginPwdRequestBean();
            String tel = edtTelephone.getText().toString().trim();
            String smsCode = edtSmsCode.getText().toString().trim();
            String amt = edtAmt.getText().toString().trim();
            if (amt.equals("")) {
                amt = "0";
            }
            String password = edtPassword.getText().toString().trim();
            String rePassword = edtRePassword.getText().toString().trim();
            String key = MD5Tools.MD5(tel + smsCode + amt + MD5Tools.MD5(password));
            if (tel.equals("")) {
                Toast.makeText(getActivity(), "请输入手机号", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (smsCode.equals("")) {
                Toast.makeText(getActivity(), "请输入短信验证码", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (password.equals("")) {
                Toast.makeText(getActivity(), "请设置登录密码", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (password.length() < 6) {
                Toast.makeText(getActivity(), "登录密码的长度应至少6位", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (rePassword.equals("")) {
                Toast.makeText(getActivity(), "请再次输入登录密码", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!password.equals(rePassword)) {
                Toast.makeText(getActivity(), "两次输入的登录密码不一致", Toast.LENGTH_SHORT).show();
                return false;
            }

            requestFormData.setTel(tel);
            try {
                requestFormData.setSms_code(RSAUtil.encryptByPublicKey(smsCode));
            } catch (Exception e) {
                Toast.makeText(getActivity(), "加密短信验证码异常", Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                requestFormData.setAmt(RSAUtil.encryptByPublicKey(amt));
            } catch (Exception e) {
                Toast.makeText(getActivity(), "加密订单金额异常", Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                requestFormData.setPassword(RSAUtil.encryptByPublicKey(MD5Tools.MD5(password)));
            } catch (Exception e) {
                Toast.makeText(getActivity(), "加密登录密码异常", Toast.LENGTH_SHORT).show();
                return false;
            }
            requestFormData.setKey(key);
            return true;
        }


        @OnClick(R.id.btnGetSmsCode)
        void onGetSmsCodeClick(View view) {
            String phoneNumber = this.edtTelephone.getText().toString().trim();
            if (StringUtil.isStrEmpty(phoneNumber)) {
                Toast.makeText(getActivity(), "请填写手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!ValueUtil.isMobileNO(phoneNumber)) {
                Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            startTimer();
            KSmsInfoRequestBean bean = new KSmsInfoRequestBean(phoneNumber, "02");
            KHttpClient httpTools = KHttpClient.singleInstance();
            KUserInfoStub userInfoStub = new KUserInfoStub();
            userInfoStub.setUuid(userQueryInfoBean.getUuid());
            httpTools.postData(getActivity(), KHttpAdress.SEND_SMS, bean, userInfoStub, new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    Toast.makeText(getActivity(), "发送成功,请注意查收", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void startTimer() {
            if (BuildConfig.DEBUG) {
                btnGetSmsCode.setText("获取验证码");
                btnGetSmsCode.setClickable(true);
                return;
            }
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
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }

}


