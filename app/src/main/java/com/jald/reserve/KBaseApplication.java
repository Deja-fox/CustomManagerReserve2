package com.jald.reserve;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.iss.base.util.PersistenceUtil;
import com.jald.reserve.bean.http.response.KSildeAdResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.open.imageloader.core.ImageLoader;
import com.open.imageloader.core.ImageLoaderConfiguration;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class KBaseApplication extends Application {

    private static KBaseApplication application;

    public static KBaseApplication getInstance() {
        return application;
    }

    public static final String KEY_USER_INFO_STUB = "UserInfoStub";
    public static final String KEY_PUBLICKEY = "PublicKey";

    private PersistenceUtil persistenceUtil;
    private KUserInfoStub userLoginInfo;
    private String publicKey;
    private Map<String, Object> memoryTagMap = new HashMap<>();
    private KSildeAdResponseBean adsBean;
    private ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initXUtil();
        initHotfix();
        this.persistenceUtil = PersistenceUtil.getInstance(this);


        //推送相关
        final PushAgent mPushAgent = PushAgent.getInstance(this);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("mytoken", "deviceTokenSuccess----------------" + deviceToken + "");

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("mytoken", "deviceTokenFailed----------------" + s + "" + s1);
            }
        });
//
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Log.e("mytoken", msg.custom + "");
                    }
                });
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

    }


    public KUserInfoStub getUserInfoStub() {
        if (this.userLoginInfo == null) {
            this.userLoginInfo = (KUserInfoStub) this.persistenceUtil.getSavedObject(KEY_USER_INFO_STUB);
        }
        return this.userLoginInfo;
    }

    public void setUserInfoStub(KUserInfoStub loginInfo) {
        this.userLoginInfo = loginInfo;
        this.persistenceUtil.saveObject(KEY_USER_INFO_STUB, loginInfo);
    }

    public void removeUserInfoStub() {
        this.userLoginInfo = null;
        this.persistenceUtil.removeObject(KEY_USER_INFO_STUB);
    }

    public String getPublicKey() {
        if (this.publicKey == null) {
            this.publicKey = (String) persistenceUtil.getSavedObject(KEY_PUBLICKEY);
        }
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        this.persistenceUtil.saveObject(KEY_PUBLICKEY, publicKey);
    }

    public void removePublicKey() {
        this.publicKey = null;
        this.persistenceUtil.removeObject(KEY_PUBLICKEY);
    }

    public void logout() {
        this.removeUserInfoStub();
    }

    public void setTag(String key, Object object) {
        memoryTagMap.put(key, object);
    }

    public Object getTag(String key) {
        return memoryTagMap.get(key);
    }

    public KSildeAdResponseBean getAdsBean() {
        return adsBean;
    }

    public void setAdsBean(KSildeAdResponseBean adsBean) {
        this.adsBean = adsBean;
    }

    public ImageLoader getImageLoader() {
        if (imageLoader == null) {
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
        }
        return imageLoader;
    }


    private void initXUtil() {
        x.Ext.init(this);
        x.Ext.setDebug(true);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    private void initHotfix() {
        String appVersion = getVersionName(this);
        Log.e("SophixManager", "app版本为：" + appVersion);
        //初始化热修复
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        /**
                         code: 1 补丁加载成功
                         code: 6 服务端没有最新可用的补丁
                         code: 12 当前应用已经存在一个旧补丁, 应用重启尝试加载新补丁
                         code: 13 补丁加载失败, 导致的原因很多种, 比如UnsatisfiedLinkError等异常, 此时应该严格检查logcat异常日志
                         */
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        Log.e("SophixManager", msg);
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            Log.e("SophixManager", info + "----补丁加载成功,当前的补丁版本为:" + handlePatchVersion);
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            Log.e("SophixManager", info + "---补丁加载成功,下次启动生效!");
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            Log.e("SophixManager", info + "---补丁加载失败,下面将清空本地补丁再重新拉取补丁!");
                            SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    /**
     * 获取本应用的VersionName
     */
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context, null);
        if (info != null) {
            return info.versionName;
        } else {
            return null;
        }
    }

    /**
     * 根据packageName获取packageInfo
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (null == context) {
            return null;
        }
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }
        PackageInfo info = null;
        PackageManager manager = context.getPackageManager();
        // 根据packageName获取packageInfo
        try {
            info = manager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.toString());
        }
        return info;
    }
}
