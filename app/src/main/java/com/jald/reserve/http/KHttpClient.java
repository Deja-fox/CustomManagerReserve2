package com.jald.reserve.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpCallBack.ResponseDecryptInfo;
import com.jald.reserve.util.BusinessEncryptUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KHttpClient {

    public static final String TAG = "KHttpClient";

    private static KHttpClient instance;

    public static KHttpClient singleInstance() {
        if (instance == null) {
            synchronized (KHttpClient.class) {
                if (instance == null) {
                    instance = new KHttpClient();
                }
            }
        }
        return instance;
    }

    public Map<Context, List<WeakReference<Callback.Cancelable>>> requestMap = new HashMap<>();

    private static String lastUrl = null;
    private static String lastSendJson = null;
    private static KHttpCallBack lastCallBack = null;

    private KHttpClient() {
    }

    public void postData(Context context, int operation, Object contentBean, KUserInfoStub userToken, KHttpCallBack callBack) {
        String jsonBean = JSON.toJSONString(contentBean);
        postData(context, operation, jsonBean, userToken, callBack);
    }

    public void postData(Context context, int operation, String contentBean, KUserInfoStub userToken, KHttpCallBack callBack) {
        String url = KHttpAdress.URLS[operation];
        int level = KHttpAdress.LEVELS[operation];
        callBack.setDecryptInfo(new ResponseDecryptInfo(operation, userToken));

        Log.e(TAG, "请求地址:" + url);
        try {
            Log.e(TAG, "请求数据:" + contentBean);
            String sendJson = BusinessEncryptUtil.makeSendJson(level, contentBean, userToken);
            doPostData(context, url, sendJson, callBack);
        } catch (GeneralSecurityException e) {
            callBack.sendDataEncryptErrorMessage();
        }
    }

    private void doPostData(Context context, String url, String sendJson, KHttpCallBack callBack) {
        lastUrl = url;
        lastSendJson = sendJson;
        lastCallBack = callBack;
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(60000);
        params.setCharset("UTF-8");
        params.setBodyContent(sendJson);

        Callback.Cancelable cancelable = x.http().post(params, callBack);

        List<WeakReference<Callback.Cancelable>> list = this.requestMap.get(context);
        if (list == null) {
            list = new ArrayList<>();
            this.requestMap.put(context, list);
        }
        list.add(new WeakReference<>(cancelable));
    }

    public void cancel(Context context) {
        List<WeakReference<Callback.Cancelable>> list = this.requestMap.get(context);
        if (list != null) {
            for (WeakReference<Callback.Cancelable> task : list) {
                if (task.get() != null) {
                    task.get().cancel();
                }
            }
        }
    }

    public void rePostLastSendData(Context context) {
        if (lastUrl != null && lastSendJson != null && lastCallBack != null) {
            doPostData(context, lastUrl, lastSendJson, lastCallBack);
        }
    }

}