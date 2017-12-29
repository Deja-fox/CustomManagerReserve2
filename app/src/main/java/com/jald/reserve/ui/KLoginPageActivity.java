package com.jald.reserve.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.request.KLoginRequestBean;
import com.jald.reserve.bean.http.request.KUserUpdateRequestBean;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KLoginResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.CheckUtil;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.MD5Tools;
import com.jald.reserve.util.RSAUtil;
import com.jald.reserve.util.StringUtil;
import com.jald.reserve.util.ValueUtil;
import com.jald.reserve.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.k_activity_login_layout)
public class KLoginPageActivity extends KBaseActivity {

    public static final String TAG = KLoginPageActivity.class.getSimpleName();

    @ViewInject(R.id.title)
    private TextView mTitle;

    private KLoginFirstStepFragment mLoginFirstStepFragment;
    private KLoginSecondFragment mLoginSecondStepFragment;
    private Fragment mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        this.mLoginFirstStepFragment = new KLoginFirstStepFragment();
        this.mLoginSecondStepFragment = new KLoginSecondFragment();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, this.mLoginFirstStepFragment, "LoginFirstStepFragment");
            setActiveFragment(mLoginFirstStepFragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public void setActiveFragment(Fragment activeFragment) {
        this.mActiveFragment = activeFragment;
    }

    public void changeTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setTitleVisibility(boolean isShow) {
        if (isShow) {
            mTitle.setVisibility(View.VISIBLE);
        } else {
            mTitle.setVisibility(View.GONE);
        }
    }

    public void doFinishWithResultOK(String datalist) {
        Intent intent=new Intent();
        intent.putExtra("datalist",datalist);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void doFinishWithResultCanceled() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void jumpToSecondStepFragment(KUserInfoStub firstStepResultBean) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable(KLoginSecondFragment.KEY_ARGU_USER_INFO_STUB, firstStepResultBean);
        this.mLoginSecondStepFragment.setArguments(args);
        this.mActiveFragment = this.mLoginSecondStepFragment;
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, this.mLoginSecondStepFragment, "LoginSecondStepFragment");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void onLoginSecondStepSuccess() {
        doFinishWithResultOK("");
    }

    @Override
    public void onBackPressed() {
        if (this.mActiveFragment == mLoginFirstStepFragment) {
            DialogProvider.alert(this, "温馨提示", "确定要放弃登录吗?", "确定", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    doFinishWithResultCanceled();
                    return;
                }
            }, "取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                    return;
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    public static class KLoginFirstStepFragment extends Fragment {

        public static final int REQUEST_CODE_JUMP_TO_INFOCOMPLETE = 1;

        public static final String PUSH_ID = "HFHGFHDGHTGWUJGDHGDFGRFGDTFDD";

        private KLoginPageActivity mParent;
        private View mRoot;

        @ViewInject(R.id.login_loginname_edittext)
        private EditText edtUserName;

        @ViewInject(R.id.login_password_edittext)
        private EditText edtPassword;

        @ViewInject(R.id.login_btn)
        private Button btnLogin;

        @ViewInject(R.id.register_btn)
        private Button btnRegister;

        @ViewInject(R.id.btnFrogetPwd)
        private TextView btnFrogetPwd;

        private String loginName;
        private String loginPassword;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.mParent = (KLoginPageActivity) activity;
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
                return mRoot;
            }
            this.mRoot = inflater.inflate(R.layout.k_fragment_login_first_step, container, false);
            x.view().inject(this, this.mRoot);
            SharedPreferences shared = getActivity().getPreferences(Context.MODE_PRIVATE);
            String lastLoginString = shared.getString("loginName", "");
            edtUserName.getEditableText().clear();
            edtUserName.getEditableText().append(lastLoginString);
            return this.mRoot;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            mParent.setActiveFragment(this);
            mParent.setTitleVisibility(true);
            mParent.changeTitle("用户登录");
        }

        @Event(R.id.login_btn)
        private void onLoginClickListener(View v) {
            if (checkInputFiled()) {
                hideSoftInputKeyBoard();
                KUserInfoStub user;
                KLoginRequestBean bean = new KLoginRequestBean();
                try {
                    user = new KUserInfoStub(this.loginName, this.loginPassword);
                    String tempKeyString = ValueUtil.getRandomString(8);
                    user.setTempPassword(tempKeyString);
                    bean.setPassword(RSAUtil.encryptByPublicKey(MD5Tools.MD5(loginPassword)));
                    bean.setKey(RSAUtil.encryptByPublicKey(tempKeyString));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                bean.setPush_id(PUSH_ID);
                SharedPreferences shared = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("loginName", this.loginName);
                editor.commit();
                DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        KHttpClient.singleInstance().cancel(mParent);
                    }
                });
                KHttpClient httpTools = KHttpClient.singleInstance();
                httpTools.postData(mParent, KHttpAdress.LOGIN, bean, user, new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                            switch (command) {
                                case KHttpAdress.LOGIN:
                                    KLoginResponseBean loginResult = JSON.parseObject(result.getContent(), KLoginResponseBean.class);
                                    final KUserInfoStub loginInfo = new KUserInfoStub(loginName, loginPassword, loginResult);
                                    String liceId = loginInfo.getLice_id();
                                    // 没有完善资料或者没有专卖证号,都去完善资料
                                    if (loginInfo.getState().trim().equals("02") || liceId == null || liceId.trim().equals("")) {
                                        DialogProvider.alert(mParent, "温馨提示", "您还没有完善个人资料,完善后方可使用本软件。", "去完善", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.jumpToSecondStepFragment(loginInfo);
                                            }
                                        }, "取消", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                            }
                                        });
                                        return;
                                    } else if (loginInfo.getState().trim().equals("03")) {
                                        DialogProvider.alert(getActivity(), "温馨提示", "您的账号处于冻结状态,如有问题,请联系相关人员", "确定", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                KBaseApplication.getInstance().logout();
                                                DialogProvider.hideAlertDialog();
                                                mParent.doFinishWithResultCanceled();
                                            }
                                        });
                                    } else {











                                        KBaseApplication.getInstance().setUserInfoStub(loginInfo);

                                        getSpyList(loginInfo);

                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    ;
                });
            }
        }



        void getSpyList(final KUserInfoStub loginInfo) {
            DialogProvider.showProgressBar(getActivity());
            JSONObject postJson = new JSONObject();
            postJson.put("tp_id", loginInfo.getTp_id());//测试用例370724106417
            postJson.put("function", "get_supplier_list_by_manager");
            KHttpClient.singleInstance().postData(getActivity(), KHttpAdress.COMMON_INFO, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
                @Override
                public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                    if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        JSONObject jsonObject1 = JSON.parseObject(result.getContent());
                        JSONObject contentJson = JSON.parseObject(jsonObject1.getString("content"));
                        if (contentJson != null) {
                            if ((contentJson.getString("ret_code")).equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                                try {
                                    JSONArray data = new JSONArray(contentJson.getString("data"));

                                    if(data.length()>0){
                                        loginInfo.setStplist(data.toString());

                                        boolean ishasmain=false;
                                        for(int i=0;i<data.length();i++){

                                            //自营店铺初始化
                                            if(data.getJSONObject(i).optString("is_propietary").equals("1")){


                                                loginInfo.setStpId(data.getJSONObject(i).optString("supplier_tp_id"));
                                                loginInfo.setMtpId(data.getJSONObject(i).optString("client_manager_tp_id"));
                                                loginInfo.setSupplier_note(data.getJSONObject(i).optString("supplier_note"));
                                                KBaseApplication.getInstance().setUserInfoStub(loginInfo);
                                                ishasmain=true;

                                            }

                                        }

                                        if(!ishasmain){

                                            loginInfo.setStpId(data.getJSONObject(0).optString("supplier_tp_id"));
                                            loginInfo.setMtpId(data.getJSONObject(0).optString("client_manager_tp_id"));
                                            loginInfo.setSupplier_note(data.getJSONObject(0).optString("supplier_note"));
                                            KBaseApplication.getInstance().setUserInfoStub(loginInfo);

                                        }

                                    }


                                    mParent.doFinishWithResultOK(data.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Toast.makeText(getActivity(), contentJson.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), result.getRet_msg(), Toast.LENGTH_SHORT).show();
                    }
                    DialogProvider.hideProgressBar();
                }

                @Override
                protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
                    super.handleBusinessLayerFailure(responseBean, statusCode);
                }

                @Override
                protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                    super.handleHttpLayerFailure(ex, isOnCallback);
                }
            });
        }





        @Event(R.id.register_btn)
        private void onRegisterClickListener(View v) {
            hideSoftInputKeyBoard();
            Intent intent = new Intent(getActivity(), KRegisterPageActivity.class);
            startActivity(intent);
        }

        @Event(R.id.btnFrogetPwd)
        private void onForgetPwdClick(View v) {
            hideSoftInputKeyBoard();
            Intent intent = new Intent(getActivity(), KForgetPwdActivity.class);
            startActivity(intent);
        }

        private boolean checkInputFiled() {
            this.loginName = edtUserName.getText().toString().trim();
            this.loginPassword = edtPassword.getText().toString().trim();
            if (StringUtil.isStrEmpty(loginName)) {
                Toast.makeText(getActivity(), "请输入用户名", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (StringUtil.isStrEmpty(loginPassword)) {
                Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }


        private void hideSoftInputKeyBoard() {
            InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static class KLoginSecondFragment extends Fragment {

        public static final String KEY_ARGU_USER_INFO_STUB = "KeyArguUserInfoStub";

        // 是否允许在完善信息的时候设置支付密码
        private boolean enableSetPayPassword = false;

        private View mRoot;
        private KLoginPageActivity mParent;

        @ViewInject(R.id.info_complete_identify_edittext)
        private EditText edtIdNumber;

        @ViewInject(R.id.info_complete_realname_edittext)
        private EditText edtRealName;

        @ViewInject(R.id.info_complete_lice_id_edittext)
        private EditText edtLiceId;

        @ViewInject(R.id.setPwdTip)
        private View payPwdTip;
        @ViewInject(R.id.setLiceId)
        private View setLiceId;

        @ViewInject(R.id.info_complete_paypwd_edittext)
        private EditText edtPayPwd;

        @ViewInject(R.id.info_complete_repaypwd_edittext)
        private EditText edtRePayPwd;

        @ViewInject(R.id.info_complete_submit_btn)
        private Button btnSubmit;

        private String idNumber;
        private String realName;
        private String liceId;
        private String payPwd;
        private String rePayPwd;

        private KUserInfoStub userInfoStub;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.mParent = (KLoginPageActivity) activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mRoot = inflater.inflate(R.layout.k_fragment_login_second_step, container, false);
            x.view().inject(this, mRoot);
            this.userInfoStub = (KUserInfoStub) getArguments().getSerializable(KEY_ARGU_USER_INFO_STUB);
            if (this.userInfoStub == null) {
                Log.e(TAG, "调用了登录第二步完善信息页,但是没有传递KUserInfoStub信息");
            }
            this.setLiceId.setVisibility(View.GONE);
            this.edtLiceId.setVisibility(View.GONE);
            if (!this.enableSetPayPassword) {
                this.payPwdTip.setVisibility(View.GONE);
                this.edtPayPwd.setVisibility(View.GONE);
                this.edtRePayPwd.setVisibility(View.GONE);
            }
            return mRoot;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mParent.setTitleVisibility(true);
            mParent.changeTitle("完善资料");
            this.idNumber = this.userInfoStub.getId_number();
            if (this.idNumber != null && !this.idNumber.equals("")) {
                this.edtIdNumber.getEditableText().clear();
                this.edtIdNumber.getEditableText().append(this.idNumber);
                this.edtIdNumber.setEnabled(false);
            }
            this.realName = this.userInfoStub.getManager();
            if (this.realName != null && !this.realName.equals("")) {
                this.edtRealName.getEditableText().clear();
                this.edtRealName.getEditableText().append(this.realName);
                this.edtRealName.setEnabled(false);
            }
        }

        @Event(R.id.info_complete_submit_btn)
        private void onSubmitClick(View view) {
            if (checkInputField()) {
                hideSoftInputKeyBoard();
                DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        KHttpClient.singleInstance().cancel(mParent);
                    }
                });
                KUserUpdateRequestBean infoUpdatePostBean = new KUserUpdateRequestBean();
                infoUpdatePostBean.setIs_reg("0");
                if (enableSetPayPassword) {
                    infoUpdatePostBean.setPay_pwd(MD5Tools.MD5(this.payPwd));
                } else {
                    infoUpdatePostBean.setPay_pwd("");
                }
//                登录完善信息不开通南粤电子账户
                infoUpdatePostBean.setIs_reg_nanyue("0");
                infoUpdatePostBean.setId_number(this.idNumber);
                infoUpdatePostBean.setManager(this.realName);
                infoUpdatePostBean.setLice_id(this.liceId);
                KHttpClient.singleInstance().postData(mParent, KHttpAdress.USER_UPDATE, infoUpdatePostBean, userInfoStub, new KHttpCallBack() {
                    @Override
                    public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                        DialogProvider.hideProgressBar();
                        if (isReturnSuccess) {
                            KBaseApplication.getInstance().setUserInfoStub(userInfoStub);
                            mParent.onLoginSecondStepSuccess();
                        }
                    }
                });
            }
        }

        private boolean checkInputField() {
            this.idNumber = edtIdNumber.getText().toString().trim();
            this.realName = edtRealName.getText().toString().trim();
//            this.liceId = edtLiceId.getText().toString().trim();
            this.liceId = this.userInfoStub.getLice_id().trim();
            this.payPwd = edtPayPwd.getText().toString().trim();
            this.rePayPwd = edtRePayPwd.getText().toString().trim();
            if (!CheckUtil.vaildateIdCard(idNumber)) {
                Toast.makeText(getActivity(), "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (this.realName.equals("")) {
                Toast.makeText(getActivity(), "请输入真实姓名", Toast.LENGTH_SHORT).show();
                return false;
            }
//            if (this.liceId.equals("")) {
//                Toast.makeText(getActivity(), "请输入烟草专卖证号", Toast.LENGTH_SHORT).show();
//                return false;
//            }
            if (this.liceId.equals("")) {
                String phoneNum = this.userInfoStub.getTelephone().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getActivity(), "手机号为空，请联系客服配置", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    this.liceId = "k" + phoneNum;
                }
            }
            if (this.enableSetPayPassword) {
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
            }
            return true;
        }

        private void hideSoftInputKeyBoard() {
            InputMethodManager imManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}
