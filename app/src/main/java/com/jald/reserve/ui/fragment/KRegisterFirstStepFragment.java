package com.jald.reserve.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.BuildConfig;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KSmsInfoRequestBean;
import com.jald.reserve.bean.http.request.KUserRegisterRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KRegisterPageActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.StringUtil;
import com.jald.reserve.util.ValueUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KRegisterFirstStepFragment extends Fragment {

    private View mRoot;

    @ViewInject(R.id.register_phonenum_edittext)
    private EditText edtPhoneNumber;

    @ViewInject(R.id.register_smscode_edittext)
    private EditText edtSmsCode;

    @ViewInject(R.id.register_pwd_edittext)
    private EditText edtPassword;

    @ViewInject(R.id.get_sms_code_btn)
    private Button btnGetSmsCode;

    @ViewInject(R.id.register_btn)
    private Button btnRegister;

    private String phoneNumber;
    private String smsCode;
    private String pwd;

    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.k_fragment_register_first_step, container, false);
        x.view().inject(this, mRoot);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            makeDummyData();
        }
    }

    private void makeDummyData() {
        Random r = new Random();
        long ran = r.nextLong();
        String tel = "1304748" + ran;
        tel = tel.substring(0, 11);
        this.edtPhoneNumber.setText(tel);
        this.edtPassword.setText("123456");
    }

    @Event(R.id.get_sms_code_btn)
    private void onGetSmsCodeClick(View view) {
        this.phoneNumber = this.edtPhoneNumber.getText().toString().trim();
        if (StringUtil.isStrEmpty(phoneNumber)) {
            Toast.makeText(getActivity(), "请填写手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValueUtil.isMobileNO(this.phoneNumber)) {
            Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        startTimer();
        KSmsInfoRequestBean bean = new KSmsInfoRequestBean(this.phoneNumber, "06");
        KHttpClient httpTools = KHttpClient.singleInstance();
        httpTools.postData(getActivity(), KHttpAdress.SEND_SMS, bean, new KUserInfoStub(), new KHttpCallBack() {
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

    @Event(R.id.register_btn)
    private void onRegisterClick(View view) {
        if (checkInputField()) {
            hideSoftInputKeyBoard();
            final KUserInfoStub registerContextBean = new KUserInfoStub();
            registerContextBean.setUuid(this.phoneNumber);
            registerContextBean.setSmsCode(this.smsCode);
            KUserRegisterRequestBean postBean = new KUserRegisterRequestBean();
            postBean.setPassword(MD5Tools.MD5(this.pwd));
            postBean.setSms_code(this.smsCode);
            DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.USER_REGISTER, postBean, registerContextBean, new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        JSONObject retJson = JSON.parseObject(result.getContent());
                        String uuid = retJson.getString("uuid");
                        String tempPwd = retJson.getString("temp_password");
                        registerContextBean.setUuid(uuid);
                        registerContextBean.setTempPassword(tempPwd);
                        ((KRegisterPageActivity) getActivity()).onNextStepClicked(registerContextBean);
                    }
                }
            });
        }
    }

    private boolean checkInputField() {
        this.phoneNumber = this.edtPhoneNumber.getText().toString().trim();
        this.smsCode = this.edtSmsCode.getText().toString().trim();
        this.pwd = this.edtPassword.getText().toString().trim();
        if (StringUtil.isStrEmpty(phoneNumber)) {
            Toast.makeText(getActivity(), "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValueUtil.isMobileNO(this.phoneNumber)) {
            Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isStrEmpty(this.smsCode)) {
            Toast.makeText(getActivity(), "请输入短信验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isStrEmpty(this.pwd)) {
            Toast.makeText(getActivity(), "请输入登录密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]{5,15}$");
        Matcher m = p.matcher(this.pwd);
        if (!m.matches()) {
            Toast.makeText(getActivity(), "登录密码的格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hideSoftInputKeyBoard() {
        InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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