package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jald.reserve.BuildConfig;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KUserUpdateRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.ui.KRegisterPageActivity;
import com.jald.reserve.util.CheckUtil;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;


import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Random;

public class KRegisterSecondStepFragment extends Fragment {

    public static final String TAG = KRegisterSecondStepFragment.class.getSimpleName();

    public static final String KEY_ARGU_USER_INFO_STUB = "KeyArguUserInfoStub";

    private View mRoot;

    private KRegisterPageActivity parent;

    @ViewInject(R.id.info_complete_identify_edittext)
    private EditText edtIdNumber;

    @ViewInject(R.id.info_complete_realname_edittext)
    private EditText edtRealName;

    @ViewInject(R.id.info_complete_lice_id_edittext)
    private EditText edtLiceId;

    @ViewInject(R.id.info_complete_paypwd_edittext)
    private EditText edtPayPwd;

    @ViewInject(R.id.info_complete_repaypwd_edittext)
    private EditText edtRePayPwd;

    @ViewInject(R.id.info_complete_submit_btn)
    private Button btnSubmit;

    @ViewInject(R.id.chkIsOpenNanYueEAccout)
    private CheckBox chkIsOpenNanYueEAccount;

    private String idNumber;
    private String realName;
    private String liceId;
    private String payPwd;
    private String rePayPwd;
    private boolean isOpenNanYueEAccount;

    private KUserInfoStub userInfoStub;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = (KRegisterPageActivity) activity;

        this.userInfoStub = (KUserInfoStub) getArguments().getSerializable(KEY_ARGU_USER_INFO_STUB);
        if (this.userInfoStub == null) {
            Log.e(TAG,"调用完善用户信息页,但是没有传递KUserInfoStub信息");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.k_fragment_register_second_step, container, false);
        x.view().inject(this, mRoot);
        if (BuildConfig.DEBUG) {
            makeDummyData();
        }
        return mRoot;
    }

    private void makeDummyData() {
        this.edtIdNumber.setText("370881198909282014");
        this.edtRealName.setText("孔令宽");
        Random r = new Random();
        String t = r.nextLong() + "45645454554554545454";
        this.edtLiceId.setText(t.substring(0, 12));
        this.edtPayPwd.setText("12345678");
        this.edtRePayPwd.setText("12345678");
    }

    @Event(R.id.chkIsOpenNanYueEAccoutWrapper)
    private void onChkIsOpenNanYueEAccountClick(View v) {
        this.chkIsOpenNanYueEAccount.setChecked(!chkIsOpenNanYueEAccount.isChecked());
    }


    @Event(R.id.info_complete_submit_btn)
    private void onSubmitClick(View view) {
        if (checkInputField()) {
            hideSoftInputKeyBoard();
            DialogProvider.showProgressBar(getActivity(), new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    KHttpClient.singleInstance().cancel(getActivity());
                }
            });
            KUserUpdateRequestBean infoUpdatePostBean = new KUserUpdateRequestBean();
            infoUpdatePostBean.setIs_reg("1");
            infoUpdatePostBean.setPay_pwd(MD5Tools.MD5(this.payPwd));
            infoUpdatePostBean.setId_number(this.idNumber);
            infoUpdatePostBean.setManager(this.realName);
            infoUpdatePostBean.setLice_id(this.liceId);
            infoUpdatePostBean.setIs_reg_nanyue(isOpenNanYueEAccount ? "1" : "0");
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.USER_UPDATE, infoUpdatePostBean, userInfoStub, new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    DialogProvider.hideProgressBar();
                    if (isReturnSuccess) {
                        if (isOpenNanYueEAccount) {
                            com.alibaba.fastjson.JSONObject retJSON = JSON.parseObject(result.getContent());
                            if (retJSON != null) {
                                String nanYueEAccountStatus = retJSON.getString("user_flag");
                                if (nanYueEAccountStatus == null || nanYueEAccountStatus.equals("")) {
                                    DialogProvider.alert(getActivity(), "用户信息完善成功,但是南粤电子账户开户失败,原因：服务器返回的数据无法解析,您可以登录后在账户管理模块重新尝试开户");
                                }
                                if (nanYueEAccountStatus.equals("0") || nanYueEAccountStatus.equals("2")) {
                                    // 切换到南粤电子账户管理页来设置电子账户密码
                                    Bundle args = new Bundle();
                                    args.putSerializable(KRegisterThirdNanYueEAccountActiveFragment.KEY_ARGU_OP_TYPE, KRegisterThirdNanYueEAccountActiveFragment.OP_TYPE_SETPWD);
                                    args.putSerializable(KRegisterThirdNanYueEAccountActiveFragment.KEY_ARGU_USER_INFO_STUB, userInfoStub);
                                    KRegisterThirdNanYueEAccountActiveFragment eAccountManageroFragment = new KRegisterThirdNanYueEAccountActiveFragment();
                                    eAccountManageroFragment.setArguments(args);
                                    parent.changeFragment(eAccountManageroFragment);
                                } else if (nanYueEAccountStatus.equals("3")) {
                                    // 跳转到南粤电子账户管理页进行绑卡操作
                                    Bundle args = new Bundle();
                                    args.putSerializable(KRegisterThirdNanYueEAccountActiveFragment.KEY_ARGU_OP_TYPE, KRegisterThirdNanYueEAccountActiveFragment.OP_TYPE_BIND_CARD);
                                    args.putSerializable(KRegisterThirdNanYueEAccountActiveFragment.KEY_ARGU_USER_INFO_STUB, userInfoStub);
                                    KRegisterThirdNanYueEAccountActiveFragment eAccountManageroFragment = new KRegisterThirdNanYueEAccountActiveFragment();
                                    eAccountManageroFragment.setArguments(args);
                                    parent.changeFragment(eAccountManageroFragment);
                                } else if (nanYueEAccountStatus.equals("1") || nanYueEAccountStatus.equals("4")) {
                                    // 南粤电子账户不需要任何设置
                                    Toast.makeText(getActivity(), "恭喜您注册成功", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }
                            } else {
                                DialogProvider.alert(getActivity(), "用户信息完善成功,但是南粤电子账户开户失败,原因：服务器返回的数据无法解析,您可以登录后在账户管理模块重新尝试开户");
                            }
                        } else {
                            Toast.makeText(getActivity(), "恭喜您注册成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }
                }
            });
        }
    }

    private boolean checkInputField() {
        this.idNumber = edtIdNumber.getText().toString().trim();
        this.realName = edtRealName.getText().toString().trim();
        this.liceId = edtLiceId.getText().toString().trim();
        this.payPwd = edtPayPwd.getText().toString().trim();
        this.rePayPwd = edtRePayPwd.getText().toString().trim();
        this.isOpenNanYueEAccount = chkIsOpenNanYueEAccount.isChecked();
        if (!CheckUtil.vaildateIdCard(idNumber)) {
            Toast.makeText(getActivity(), "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.realName.equals("")) {
            Toast.makeText(getActivity(), "请输入真实姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.liceId.equals("")) {
            Toast.makeText(getActivity(), "请输入烟草专卖证号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.payPwd.equals("")) {
            Toast.makeText(getActivity(), "请输入支付密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.payPwd.length() < 6) {
            Toast.makeText(getActivity(), "支付密码过短", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.rePayPwd.equals("")) {
            Toast.makeText(getActivity(), "请再次输入支付密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!this.rePayPwd.equals(this.payPwd)) {
            Toast.makeText(getActivity(), "两次输入的支付密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hideSoftInputKeyBoard() {
        InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}