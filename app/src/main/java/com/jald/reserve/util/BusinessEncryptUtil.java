package com.jald.reserve.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jald.reserve.bean.http.request.KBaseHttpRequestBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;

import java.security.GeneralSecurityException;


public class BusinessEncryptUtil {

    public static final String TAG = BusinessEncryptUtil.class.getSimpleName();

    public static final int PUBLIC_LEVEL = 0;
    public static final int ENCRYPT_LEVEL = 1;
    public static final int LOGIN_LEVEL = 4;
    public static final int SMS_LEVEL = 5;

    public static String getVerifyCode(int level, String soureJson, KUserInfoStub user) {
        StringBuffer sb = new StringBuffer();
        switch (level) {
            case PUBLIC_LEVEL:
            case LOGIN_LEVEL:
                sb.append(soureJson);
                break;
            case ENCRYPT_LEVEL:
                sb.append(soureJson);
                sb.append(user.getTempPassword().substring(16, 24));
                break;
            case SMS_LEVEL:
                sb.append(soureJson);
                sb.append(user.getSmsCode());
                break;
            default:
                break;
        }
        Log.e("NetCommEncy", "source:" + sb.toString() + " md5:" + MD5Tools.MD5(sb.toString()));
        return MD5Tools.MD5(sb.toString());
    }

    public static String getDesEnCode(int level, String soureJson, KUserInfoStub user) throws GeneralSecurityException {
        String desKey = null;
        switch (level) {
            case PUBLIC_LEVEL:
            case LOGIN_LEVEL:
                return soureJson;
            case ENCRYPT_LEVEL:
                desKey = user.getTempPassword().substring(0, 8);
                break;
            case SMS_LEVEL:
                desKey = StringUtil.appendStr(user.getSmsCode(), 8, "0", true, true);
                break;
            default:
                break;
        }
        System.out.println(desKey);
        return DES.encryptDES(soureJson, desKey);
    }

    public static String getDesDeCode(int level, String decryptString, KUserInfoStub user) throws GeneralSecurityException {
        String desKey = null;
        switch (level) {
            case PUBLIC_LEVEL:
                Log.e(TAG, "明文结果:" + decryptString);
                return decryptString;
            case SMS_LEVEL:
                desKey = StringUtil.appendStr(user.getSmsCode(), 8, "0", true, true);
                break;
            case LOGIN_LEVEL:
                desKey = user.getTempPassword();
                break;
            case ENCRYPT_LEVEL:
                desKey = user.getTempPassword().substring(0, 8);
                break;
            default:
                break;
        }
        System.out.println(desKey);
        String result = DES.decryptDES(decryptString, desKey);
        Log.e(TAG, "解密结果:" + result);
        return result;
    }

    public static boolean isVerifyCodeRight(int level, String soureStr, String verifyCode, KUserInfoStub user) throws GeneralSecurityException {
        String desKey = null;
        switch (level) {
            case PUBLIC_LEVEL:
            case LOGIN_LEVEL:
            case SMS_LEVEL:
                return verifyCode.equals(MD5Tools.MD5(soureStr));
            case ENCRYPT_LEVEL:
                desKey = user.getTempPassword().substring(16, 24);
                return verifyCode.equals(MD5Tools.MD5(soureStr + desKey));
            default:
                break;
        }
        return false;
    }

    public static String makeSendJson(int level, String contentBean, KUserInfoStub user) throws GeneralSecurityException {
        String enCodeString = getDesEnCode(level, contentBean, user);
        KBaseHttpRequestBean bean;
        if (KHttpAdress.isEncrypt) {
            //加密
            bean = new KBaseHttpRequestBean(enCodeString, user.getUuid());
        } else {
            //不加密
            bean = new KBaseHttpRequestBean(contentBean, user.getUuid());
        }
        bean.setSignature(getVerifyCode(level, contentBean, user));
        return JSON.toJSONString(bean);
    }
}
