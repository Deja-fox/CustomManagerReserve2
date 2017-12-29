package com.jald.reserve.http;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.ui.KLoginPageActivity;
import com.jald.reserve.util.BusinessEncryptUtil;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KAppManager;
import com.umeng.analytics.MobclickAgent;

import org.xutils.common.Callback;

import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;

public abstract class KHttpCallBack implements Callback.PrepareCallback<String, String> {

    public static String TAG = KHttpCallBack.class.getSimpleName();

    public static class ResponseDecryptInfo {
        public int mOperation = -1;
        public int mEncryptLevel = -1;
        public KUserInfoStub mUserToken = null;

        public ResponseDecryptInfo(int mOperation, KUserInfoStub mUserToken) {
            this.mOperation = mOperation;
            this.mEncryptLevel = KHttpAdress.LEVELS[mOperation];
            this.mUserToken = mUserToken;
        }

    }

    static UIHandler uiHandler;

    private ResponseDecryptInfo decryptInfo;

    public KHttpCallBack() {
        init();
    }


    private void init() {
        uiHandler = new UIHandler(Looper.getMainLooper(), this);
    }

    private static class UIHandler extends Handler {

        private WeakReference<KHttpCallBack> httpCallback;

        public UIHandler(Looper looper, KHttpCallBack callback) {
            super(looper);
            this.httpCallback = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            if (httpCallback.get() != null) {
                httpCallback.get().handleMessage(msg);
            }
        }
    }

    public void sendDataEncryptErrorMessage() {
        Message msg = new Message();
        msg.obj = KHttpConst.HTTP_CLIENT_SIDE_DATA_ENCRYPT_ERROR;
        uiHandler.sendMessage(msg);
    }

    public void sendEntityBuildErrorMessage() {
        Message msg = new Message();
        msg.obj = KHttpConst.HTTP_CLIENT_SIDE_ENTITY_BUILD_ERROR;
        uiHandler.sendMessage(msg);
    }

    public void handleMessage(Message msg) {
        if (msg.obj.toString().equals(KHttpConst.HTTP_CLIENT_SIDE_DATA_ENCRYPT_ERROR)) {
            this.handleBusinessLayerFailure(null, KHttpConst.HTTP_CLIENT_SIDE_DATA_ENCRYPT_ERROR);
        }
    }


    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        handleHttpLayerFailure(ex, isOnCallback);
    }

    @Override
    public String prepare(String rawData) {
        return rawData;
    }

    @Override
    public void onSuccess(String result) {
        if (this.decryptInfo == null) {
            String err = "KHttpCallBack对象必须设置ResponseDecryptInfo对象来进行业务数据的解密";
            Log.e(TAG, err);
            onError(new Exception(err), true);
            return;
        }

        Log.e(TAG, "返回结果是：" + result);
        KBaseHttpResponseBean responseBean = JSONObject.parseObject(result, KBaseHttpResponseBean.class);
        String retCode = responseBean.getRet_code();
        if (retCode != null && !retCode.equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
            handleBusinessLayerFailure(responseBean, retCode);
        } else {
            if (KHttpAdress.isEncrypt) {
                //加密
                try {
                    String encryptedBusinessBeanStr = responseBean.getContent();
                    String signature = responseBean.getSignature();
                    String decryptedBusinessBeanStr = BusinessEncryptUtil.getDesDeCode(decryptInfo.mEncryptLevel, encryptedBusinessBeanStr, decryptInfo.mUserToken);
                    //设置返回content
                    responseBean.setContent(decryptedBusinessBeanStr);
                    try {
                        if (BusinessEncryptUtil.isVerifyCodeRight(decryptInfo.mEncryptLevel, responseBean.getContent(), signature, decryptInfo.mUserToken)) {
                            handleBusinessLayerSuccess(decryptInfo.mOperation, responseBean, true);
                        } else {
                            handleBusinessLayerFailure(responseBean, KHttpConst.HTTP_CLIENT_SIDE_DATA_VERIFY_ERROR);
                        }
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        handleBusinessLayerFailure(responseBean, KHttpConst.HTTP_CLIENT_SIDE_DATA_VERIFY_ERROR);
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    handleBusinessLayerFailure(responseBean, KHttpConst.HTTP_CLIENT_SIDE_DATA_DECRYPT_ERROR);
                } catch (Exception e) {
                    // 其他业务逻辑处理时发生的异常
                    e.printStackTrace();
                    MobclickAgent.reportError(KBaseApplication.getInstance(), e);
                    handleBusinessLayerFailure(responseBean, KHttpConst.HTTP_CLIENT_SIDE_OTHER_ERROR);
                }
            } else {
                //不加密
                responseBean.setContent(responseBean.getContent());
                handleBusinessLayerSuccess(decryptInfo.mOperation, responseBean, true);
            }
        }
    }

    @Override
    public void onFinished() {

    }

    protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
        final Activity mActivity = KAppManager.getAppManager().currentActivity();
        DialogProvider.hideProgressBar();
        DialogProvider.alert(mActivity, "网络连接异常", "网络连接异常，请查看本机网络连接是否正常。", "确定", new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProvider.hideAlertDialog();
                DialogProvider.hideProgressBar();
            }
        });
    }

    protected abstract void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess);

    protected void handleBusinessLayerFailure(KBaseHttpResponseBean responseBean, String statusCode) {
        DialogProvider.hideProgressBar();
        final Activity mActivity = KAppManager.getAppManager().currentActivity();

        if (statusCode.equals(KHttpConst.HTTP_SERVER_SIDE_DATA_DECRYPT_ERROR) || statusCode.equals(KHttpConst.HTTP_SERVER_SIDE_DATA_VERIFY_ERROR)) {
            DialogProvider.alert(mActivity, " 温馨提示", "您提交的数据没有通过服务器端的校验,可能是您的账号在其他机器登录或您距上次登录已超过24小时,请您重新登录。", "重新登录", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(mActivity instanceof KLoginPageActivity)) {
                        Intent intent = new Intent(mActivity, KLoginPageActivity.class);
                        mActivity.startActivity(intent);
                    }
                    DialogProvider.hideAlertDialog();
                }
            }, "取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });
        } else if (statusCode.equals(KHttpConst.HTTP_CLIENT_SIDE_DATA_DECRYPT_ERROR) || statusCode.equals(KHttpConst.HTTP_CLIENT_SIDE_DATA_VERIFY_ERROR)) {
            DialogProvider.alert(mActivity, " 温馨提示", "服务器响应的数据校验失败,可能是您的账号在其他机器登录或您距上次登录已超过24小时,请您重新登录。", "重新登录", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(mActivity instanceof KLoginPageActivity)) {
                        Intent intent = new Intent(mActivity, KLoginPageActivity.class);
                        mActivity.startActivity(intent);
                    }
                    DialogProvider.hideAlertDialog();
                }
            }, "取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProvider.hideAlertDialog();
                }
            });
        } else if (statusCode.equals(KHttpConst.HTTP_CLIENT_SIDE_DATA_ENCRYPT_ERROR)) {
            DialogProvider.alert(mActivity, "温馨提示", "客户端数据加密失败,请重试", "确定");
        } else if (statusCode.equals(KHttpConst.HTTP_CLIENT_SIDE_ENTITY_BUILD_ERROR)) {
            DialogProvider.alert(mActivity, "温馨提示", "请求数据构建失败,请重试", "确定");
        } else if (statusCode.equals(KHttpConst.HTTP_CLIENT_SIDE_OTHER_ERROR)) {
            DialogProvider.alert(mActivity, "温馨提示", "抱歉,程序异常,请联系开发者解决问题", "确定");
        } else {
            DialogProvider.alert(mActivity, responseBean.getRet_msg());
        }
    }

    public void setDecryptInfo(ResponseDecryptInfo decryptInfo) {
        if (decryptInfo != null) {
            this.decryptInfo = decryptInfo;
        }
    }
}
