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
public class KRegisterThirdNanYueEAccountActiveFragment extends Fragment {

    public static final String TAG = KRegisterThirdNanYueEAccountActiveFragment.class.getSimpleName();

    public static final String KEY_ARGU_OP_TYPE = "KeyOpType";
    public static final String KEY_ARGU_USER_INFO_STUB = "KeyArguUserInfoStub";

    public static final String OP_TYPE_SETPWD = "TypeSetPWD";
    public static final String OP_TYPE_BIND_CARD = "TypeBindCard";

    @Bind(R.id.bridgeWebView)
    BridgeWebView bridgeWebView;

    private View mRoot;

    private String curOpType;
    private KUserInfoStub userInfoStub;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (getArguments() != null) {
            this.curOpType = getArguments().getString(KEY_ARGU_OP_TYPE);
            if (this.curOpType == null || this.curOpType.equals("")) {
                Log.e(TAG,"调用南粤电子账户管理Fragment,但是传递的参数错误,没有传递操作类型,如设置电子账户密码还是绑卡操作");
            }

            this.userInfoStub = (KUserInfoStub) getArguments().getSerializable(KEY_ARGU_USER_INFO_STUB);
            if (this.userInfoStub == null) {
                Log.e(TAG,"调用南粤电子账户管理Fragment,但是没有传递KUserInfoStub信息,这些信息是必须要有的");
            }
        } else {
            Log.e(TAG,"调用南粤电子账户管理Fragment,但是没有传递KUserInfoStub信息，这些信息是必须要有的");
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

        bridgeWebView.setWebChromeClient(new WebChromeClient());
        exposeJavaInterface();

        if (curOpType.equals(OP_TYPE_SETPWD)) {
            loadSetPwdWebPage();
        } else if (curOpType.equals(OP_TYPE_BIND_CARD)) {
            loadBindCardWebPage();
        }
        return mRoot;
    }


    // 请求设置南粤电子账户密码页
    private void loadSetPwdWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD];
        int level = KHttpAdress.LEVELS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD];
        try {
            JSONObject requestContent = new JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG,"南粤电子账户设密页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败,您可以在登录后在账户管理模块重新进行设密操作", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败,您可以在登录后在账户管理模块重新进行设密操作", "确定");
        }

    }


    // 请求南粤电子账户绑卡页
    private void loadBindCardWebPage() {
        String url = KHttpAdress.URLS[KHttpAdress.NANYUE_EACCOUNT_BIND_CARD];
        int level = KHttpAdress.LEVELS[KHttpAdress.NANYUE_EACCOUNT_BIND_CARD];
        try {
            JSONObject requestContent = new JSONObject();
            requestContent.put("random", new Random().nextLong() + "");
            String requestJSONStr = requestContent.toJSONString();

            String user = userInfoStub.getUuid();
            String content = BusinessEncryptUtil.getDesEnCode(level, requestJSONStr, userInfoStub);
            String signature = BusinessEncryptUtil.getVerifyCode(level, requestJSONStr, userInfoStub);

            url += "&user=" + user + "&content=" + URLEncoder.encode(content, "UTF-8") + "&signature=" + signature;
            Log.e(TAG,"南粤电子账户绑卡页请求URL：" + url);
            bridgeWebView.loadUrl(url);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败,您可以在登录后在账户管理模块重新进行绑卡操作", "确定");
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            DialogProvider.alert(getActivity(), "温馨提示", "请求数据构建失败:url编码失败,您可以在登录后在账户管理模块重新进行绑卡操作", "确定");
        }

    }

    private void exposeJavaInterface() {
        // 设置南粤电子账户密码页的结果回调
        bridgeWebView.registerHandler("nanYueSetPasswordWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG,"从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:" + responseBean.getRet_msg() + ",您可以在登录后在账户管理模块重新对南粤电子账户设置交易密码");
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD];
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
                                        DialogProvider.alert(getActivity(), "温馨提示", "恭喜您注册成功", "去登录", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                getActivity().finish();
                                            }
                                        });
                                    } else if (accountFlag.equals("3")) {
                                        // 去绑卡
                                        curOpType = OP_TYPE_BIND_CARD;
                                        loadBindCardWebPage();
                                    }
                                } else {
                                    DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新对南粤电子账户设置交易密码");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新对南粤电子账户设置交易密码");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新对南粤电子账户设置交易密码");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "设置南粤电子账户交易密码失败:服务端返回的数据解析错误,您可以在登录后在账户管理模块重新对南粤电子账户设置交易密码");
                }
            }
        });

        // 南粤电子账户绑卡结果响应
        bridgeWebView.registerHandler("nanYueCardWebInterfaceCallback", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    data = URLDecoder.decode(data, "UTF-8");
                    Log.e(TAG,"从网页得到的URL解码后的数据:" + data);
                    KBaseHttpResponseBean responseBean = JSONObject.parseObject(data, KBaseHttpResponseBean.class);
                    String retCode = responseBean.getRet_code();
                    if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                        DialogProvider.alert(getActivity(), "设置南粤电子账户绑卡失败:" + responseBean.getRet_msg() + ",您可以在登录后在账户管理模块重新进行绑卡操作");
                    } else {
                        try {
                            int encryptLevel = KHttpAdress.LEVELS[KHttpAdress.SET_NANYUE_EACCOUNT_PWD];
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
                                        DialogProvider.alert(getActivity(), "温馨提示", "恭喜您注册成功", "去登录", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DialogProvider.hideAlertDialog();
                                                getActivity().finish();
                                            }
                                        });
                                    }
                                    // 其他码理论上不会返回吧
                                } else {
                                    DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新进行绑卡操作");
                                }
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新进行绑卡操作");
                            }
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务器响应的数据校验失败,您可以在登录后在账户管理模块重新进行绑卡操作");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogProvider.alert(getActivity(), "南粤电子账户绑卡失败:服务端返回的数据解析错误,您可以在登录后在账户管理模块重新进行绑卡操作");
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}