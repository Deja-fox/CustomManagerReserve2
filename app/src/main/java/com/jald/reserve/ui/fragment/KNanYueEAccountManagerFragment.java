package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KNanYueEAccountMainActivity;
import com.jald.reserve.util.BusinessEncryptUtil;
import com.jald.reserve.util.DialogProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

// 传递操作类型和用户信息
public class KNanYueEAccountManagerFragment extends Fragment {

    public static final String TAG = KNanYueEAccountManagerFragment.class.getSimpleName();

    public static final String KEY_ARGU_OP_TYPE = "KeyOpType";
    public static final String KEY_ARGU_USER_INFO_STUB = "KeyArguUserInfoStub";
    // 充值用到的参数
    public static final String KEY_CHARGE_AMT = "KeyChargeAmt";
    // 提现用到的参数
    public static final String KEY_WITHDRAW_AMT = "KeyWithdrawAmt";


    public static final String OP_TYPE_SETPWD = "TypeSetPWD";
    public static final String OP_TYPE_BIND_CARD = "TypeBindCard";
    public static final String OP_TYPE_MY_BANK_CARD = "TypeMyBankCard";
    public static final String OP_TYPE_CHANGE_PWD = "TypeChangePwd";
    public static final String OP_TYPE_RESET_PWD = "TypeResetPwd";
    public static final String OP_TYPE_ACCOUNT_CHARGE = "TypeAccountCharge";
    public static final String OP_TYPE_ACCOUNT_WITHDRAW = "TypeAccountWithdraw";


    @Bind(R.id.bridgeWebView)
    BridgeWebView bridgeWebView;

    private View mRoot;

    private String curOpType;
    private KUserInfoStub userInfoStub;

    private KNanYueEAccountMainActivity mParent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParent = (KNanYueEAccountMainActivity) activity;
        if (getArguments() != null) {
            this.curOpType = getArguments().getString(KEY_ARGU_OP_TYPE);
            if (this.curOpType == null || this.curOpType.equals("")) {
                Log.e(TAG, "调用南粤电子账户管理Fragment,但是传递的参数错误,没有传递操作类型,如设置电子账户密码还是绑卡操作");
            }

            this.userInfoStub = (KUserInfoStub) getArguments().getSerializable(KEY_ARGU_USER_INFO_STUB);
            if (this.userInfoStub == null) {
                Log.e(TAG, "调用南粤电子账户管理Fragment,但是没有传递KUserInfoStub信息,这些信息是必须要有的");
            }
        } else {
            Log.e(TAG, "调用南粤电子账户管理Fragment,但是没有传递KUserInfoStub信息，这些信息是必须要有的");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.k_fragment_nanyue_eaccount_manager, container, false);
        ButterKnife.bind(this, mRoot);

        bridgeWebView.setWebChromeClient(new WebChromeClient() {

        });

        exposeJavaInterface();

        if (curOpType.equals(OP_TYPE_SETPWD)) {
            loadSetPwdWebPage();
        } else if (curOpType.equals(OP_TYPE_BIND_CARD)) {
            loadBindCardWebPage();
        } else if (curOpType.equals(OP_TYPE_MY_BANK_CARD)) {
            loadMyBankCardListWebPage();
        } else if (curOpType.equals(OP_TYPE_CHANGE_PWD)) {
            loadChangePwdWebPage();
        } else if (curOpType.equals(OP_TYPE_RESET_PWD)) {
            loadResetPwdWebPage();
        } else if (curOpType.equals(OP_TYPE_ACCOUNT_CHARGE)) {
            loadAccountChargeWebPage();
        } else if (curOpType.equals(OP_TYPE_ACCOUNT_WITHDRAW)) {
            loadAccountWithdrawWebPage();
        }
        return mRoot;
    }


    // 请求设置南粤电子账户密码页
    private void loadSetPwdWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD_SESSION];
        int level = KHttpAdress.LEVELS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD_SESSION];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户设密页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }


    // 请求南粤电子账户绑卡页
    private void loadBindCardWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_BIND_CARD_SESSION];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_BIND_CARD_SESSION];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户绑卡页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }

    // 请求南粤电子账户我的银行卡列表页
    private void loadMyBankCardListWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_MY_CARD];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_MY_CARD];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户我的银行卡列表页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }

    // 请求修改南粤电子账户支付密码接口
    private void loadChangePwdWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_CHANGE_PWD];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_CHANGE_PWD];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户修改支付密码页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }

    // 请求重置南粤电子账户支付密码接口
    private void loadResetPwdWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_RESET_PWD];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_RESET_PWD];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户重置支付密码页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }

    // 请求南粤电子账户充值接口
    private void loadAccountChargeWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_CHARGE];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_CHARGE];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            String chargeAmt = getArguments().getString(KEY_CHARGE_AMT);
            requestContent.put("amt", chargeAmt);
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户充值页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }


    // 请求南粤电子账户取现接口
    private void loadAccountWithdrawWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_WITHDRAW];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_WITHDRAW];
        try {
            com.alibaba.fastjson.JSONObject requestContent = new com.alibaba.fastjson.JSONObject();
            String amt = getArguments().getString(KEY_WITHDRAW_AMT);
            requestContent.put("amt", amt);
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG, "南粤电子账户取现页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败", "确定");
        }

    }

    private void exposeJavaInterface() {
        // 设置南粤电子账户密码页的结果回调
        bridgeWebView.registerHandler("nanYueSetPasswordSessionWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD_SESSION];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String accountFlag = contentJSON.getString("user_flag");
                                    if (accountFlag == null || accountFlag.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    }
                                    if (accountFlag.equals("1") || accountFlag.equals("4")) {
                                        DialogProvider.alert(getActivity(), "温馨提示", "恭喜您南粤电子账户激活成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    } else if (accountFlag.equals("3")) {
                                        // 去绑卡
                                        curOpType = OP_TYPE_BIND_CARD;
                                        loadBindCardWebPage();
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务端返回的数据解析错误");
                }
            }
        });

        // 南粤电子账户绑卡结果响应
        bridgeWebView.registerHandler("nanYueCardSessionWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "设置南粤电子账户绑卡失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_BIND_CARD_SESSION];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String accountFlag = contentJSON.getString("user_flag");
                                    if (accountFlag == null || accountFlag.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    }
                                    if (accountFlag.equals("1") || accountFlag.equals("4")) {
                                        DialogProvider.alert(getActivity(), "温馨提示", "恭喜您南粤电子账户激活成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                    // 其他码理论上不会返回吧
                                } else {
                                    DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务端返回的数据解析错误");
                }
            }
        });

        // 南粤电子账户我的银行卡列表请求结果响应
        bridgeWebView.registerHandler("nanYueMyCardWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "获取我的银行卡列表失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_MY_CARD];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String random = contentJSON.getString("random");
                                    if (random == null || random.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    } else {
                                        DialogProvider.alert(getActivity(), "温馨提示", "操作成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "获取我的银行卡列表失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "获取我的银行卡列表失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "获取我的银行卡列表失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "获取我的银行卡列表失败:服务端返回的数据解析错误");
                }
            }
        });

        // 南粤电子账户修改支付密码接口响应
        bridgeWebView.registerHandler("nanYueUpdatePasswordWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "修改南粤电子账户支付密码失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_CHANGE_PWD];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String random = contentJSON.getString("random");
                                    if (random == null || random.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    } else {
                                        DialogProvider.alert(getActivity(), "温馨提示", "操作成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "修改南粤电子账户支付密码失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "修改南粤电子账户支付密码失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "修改南粤电子账户支付密码失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "修改南粤电子账户支付密码失败:服务端返回的数据解析错误");
                }
            }
        });


        // 南粤电子账户重置支付密码接口响应
        bridgeWebView.registerHandler("nanYueResetPasswordWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "重置南粤电子账户支付密码失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_RESET_PWD];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String random = contentJSON.getString("random");
                                    if (random == null || random.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    } else {
                                        DialogProvider.alert(getActivity(), "温馨提示", "操作成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "重置南粤电子账户支付密码失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "重置南粤电子账户支付密码失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "重置南粤电子账户支付密码失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "重置南粤电子账户支付密码失败:服务端返回的数据解析错误");
                }
            }
        });

        // 南粤电子账户充值接口响应
        bridgeWebView.registerHandler("nanYueRechargeWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "南粤电子账户充值失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_CHARGE];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String random = contentJSON.getString("random");
                                    if (random == null || random.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    } else {
                                        DialogProvider.alert(getActivity(), "温馨提示", "操作成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "南粤电子账户充值失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "南粤电子账户充值失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "南粤电子账户充值失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "南粤电子账户充值失败:服务端返回的数据解析错误");
                }
            }
        });


        // 南粤电子账户取现接口响应
        bridgeWebView.registerHandler("nanYueWithdrawWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG, "从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "南粤电子账户提现失败:" + responseBean.getRet_msg());
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_WITHDRAW];
                            String encryptedBusinessBeanStr = responseBean.getContent();
                            String signature = responseBean.getSignature();
                            String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(encryptLevel, encryptedBusinessBeanStr, userInfoStub);
                            responseBean.setContent(decryptedBusinessBeanStr);
                            try {
                                if (BusinessEncryptUtil.isVerifyCodeRight(encryptLevel, responseBean.getContent(), signature, userInfoStub)) {
                                    // 响应成功的业务逻辑处理
                                    JSONObject contentJSON = JSON.parseObject(responseBean.getContent());
                                    String random = contentJSON.getString("random");
                                    if (random == null || random.equals("")) {
                                        throw new Exception("响应数据解析错误");
                                    } else {
                                        DialogProvider.alert(getActivity(), "温馨提示", "操作成功", "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                mParent.popupToRootFragment();
                                            }
                                        });
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "南粤电子账户提现失败:服务器响应的数据校验失败");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "南粤电子账户提现失败:服务器响应的数据校验失败");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "南粤电子账户提现失败:服务器响应的数据校验失败");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "南粤电子账户提现失败:服务端返回的数据解析错误");
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}