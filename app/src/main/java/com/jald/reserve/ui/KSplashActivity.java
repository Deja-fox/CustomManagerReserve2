package com.jald.reserve.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KSysInfoResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.consts.KBaseConfig;
import com.jald.reserve.extension.ui.KCustomManagerMainActivity;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.service.KDeamonService;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.KAppManager;
import com.jald.reserve.util.UpdateManager;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ContentView(R.layout.k_welcome)
public class KSplashActivity extends KBaseActivity {

    public static final String TAG = "WelcomeActivity";

    private static final long DELAY_TIME = 1 * 1000;

    public static final int REQUEST_CODE_START_LOGINPAGE = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        KBaseConfig.width = dm.widthPixels;
        KBaseConfig.height = dm.heightPixels;
        ProgressBar ringBar = (ProgressBar) findViewById(R.id.huan);
        try {
            Field field = ProgressBar.class.getDeclaredField("mDuration");
            field.setAccessible(true);
            field.setInt(ringBar, 1000);
        } catch (Exception e) {
        }
        startDownloadServie();
        getPublicKeyAndVersionInfo();
    }

    private void startDownloadServie() {
        Intent intent = new Intent(KDeamonService.ACTION_START_SERVICE);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    private void getPublicKeyAndVersionInfo() {
        KHttpClient httpTools = KHttpClient.singleInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_type", "13");
        KUserInfoStub user = new KUserInfoStub();
        KBaseApplication.getInstance().logout();
        httpTools.postData(this, KHttpAdress.SYS_INFO, jsonObject, user, new KHttpCallBack() {
            protected void handleHttpLayerFailure(Throwable ex, boolean isOnCallback) {
                final Activity mActivity = KAppManager.getAppManager().currentActivity();
                DialogProvider.hideProgressBar();
                DialogProvider.alert(mActivity, "网络连接异常", "网络连接异常，请查看本机网络连接是否正常。", "确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProvider.hideAlertDialog();
                        DialogProvider.hideProgressBar();
                        finish();
                    }
                });
            }

            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                DialogProvider.hideProgressBar();
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    switch (command) {
                        case KHttpAdress.SYS_INFO:
                            final KSysInfoResponseBean bean = JSON.parseObject(result.getContent(), KSysInfoResponseBean.class);
                            KBaseApplication.getInstance().setPublicKey(bean.getPublic_key());
                            int localVersionCode = getVersionCode();
                            String serverVersion = bean.getVersion();
                            String serverSupportVersion = bean.getSupport_version();
                            int serverVersionCode = Integer.parseInt(serverVersion);
                            int serverSupportVersionCode = Integer.parseInt(serverSupportVersion);
                            if (serverVersionCode > localVersionCode) {
                                if (localVersionCode < serverSupportVersionCode) {
                                    // 强制
                                    DialogProvider.alert(KSplashActivity.this, "升级提示", "您当前的客户端太老,请升级后使用", "升级", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideAlertDialog();
                                            startDownlaod(bean.getVersion_url());
                                            Toast.makeText(KSplashActivity.this, "升级包已开始下载,请等待", Toast.LENGTH_LONG).show();
//                                            finish();
                                        }
                                    }, "退出系统", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideAlertDialog();
                                            KAppManager.getAppManager().appExit();
                                        }
                                    });
                                } else {
                                    // 可选
                                    String alertMsg = bean.getAlert_msg();
                                    if (alertMsg == null || alertMsg.equals("")) {
                                        alertMsg = "有新版本啦,是否升级?";
                                    }
                                    DialogProvider.alert(KSplashActivity.this, "升级提示", alertMsg, "立即升级", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideAlertDialog();
                                            startDownlaod(bean.getVersion_url());
//                                            timer.start();
                                        }
                                    }, "以后再说", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DialogProvider.hideAlertDialog();
                                            timer.start();
                                        }
                                    });
                                }
                            } else {
                                timer.start();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    DialogProvider.alert(KSplashActivity.this, "网络交互失败", result.getRet_msg() + "(" + result.getRet_code() + ")", "退出系统", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogProvider.hideAlertDialog();
                            finish();
                        }
                    });
                }
            }
        });
    }

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private RequestParams params;

    private void startDownlaod(final String url) {
//
//        if (android.os.Build.BRAND != null && contain2(android.os.Build.BRAND, "vivo")) {
//            final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            SimpleDateFormat sDateFormat = new SimpleDateFormat("MMddhhmmss");
//            final String date = sDateFormat.format(new java.util.Date());
//
//            mNotifyManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(this);
//            mBuilder.setContentTitle("版本更新")
//                    .setContentText("正在下载...")
//                    .setContentInfo("0%")
//                    .setSmallIcon(R.drawable.ic_launcher);
//
//
//            params = new RequestParams(url);
//            //设置断点续传
//            params.setAutoResume(true);
//            params.setSaveFilePath(filepath + "/youshidinghuo" + date + ".apk");
//
//            x.http().get(params, new Callback.ProgressCallback<File>() {
//
//
//                @Override
//                public void onWaiting() {
//
//                }
//
//                @Override
//                public void onStarted() {
//                    Toast.makeText(x.app(), "开始下载", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onLoading(long total, long current, boolean isDownloading) {
//                    BigDecimal b = new BigDecimal((float) current / (float) total);
//                    float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
//                    mBuilder.setProgress(100, (int) (f1 * 100), false);
//                    mBuilder.setContentInfo((int) (f1 * 100) + "%");
//                    mNotifyManager.notify(1, mBuilder.build());
//                }
//
//                @Override
//                public void onSuccess(File result) {
//                    mBuilder.setContentText("正在下载...")
//                            // Removes the progress bar
//                            .setProgress(0, 0, false);
//                    mNotifyManager.notify(1, mBuilder.build());
//                    mNotifyManager.cancel(1);
//                    Toast.makeText(x.app(), "下载成功", Toast.LENGTH_LONG).show();
//                    File _file = new File(filepath + "/youshidinghuo" + date + ".apk");
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(Uri.fromFile(_file),
//                            "application/vnd.android.package-archive");
//                    startActivity(intent);
//                    finish();
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//                    Toast.makeText(KSplashActivity.this, "获取文件失败，尝试通过浏览器下载", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse(url);
//                    intent.setData(content_url);
//                    startActivity(intent);
//                }
//
//                @Override
//                public void onCancelled(CancelledException cex) {
//
//                }
//
//                @Override
//                public void onFinished() {
//
//                }
//            });
//            return;
//        }
//
//
//        DownloadUtil downloadUtil = DownloadUtil.getInstance(KSplashActivity.this);
//        downloadUtil.setTipsOnStartDownload("升级包已开始下载,请等待");
//        downloadUtil.setDownloadCompleteListener(new DownloadUtil.DownloadListener() {
//            @Override
//            public void onDownloadedFileNotExistErr(String url, String localFileUri, String msg) {
//                Toast.makeText(KSplashActivity.this, msg, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDownloadComplete(String url, String localFileUri, FileDescriptor fd) {
//                Intent it = new Intent();
//                it.setAction("android.intent.action.VIEW");
//                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                it.addCategory("android.intent.category.DEFAULT");
//                it.setDataAndType(Uri.parse(localFileUri), "application/vnd.android.package-archive");
//                startActivity(it);
//            }
//        });
//        downloadUtil.start(url, "优市网更新");
//        //线程下载
        UpdateManager updateManager = new UpdateManager(KSplashActivity.this, url);
        updateManager.showDownloadDialog();
    }

    private CountDownTimer timer = new CountDownTimer(DELAY_TIME, DELAY_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            // Intent intent = new Intent(KSplashActivity.this, KHighwayApplyActivity.class);
            Intent intent = new Intent(KSplashActivity.this, KLoginPageActivity.class);
            startActivityForResult(intent, REQUEST_CODE_START_LOGINPAGE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_START_LOGINPAGE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, KCustomManagerMainActivity.class);
                intent.putExtra("datalist",data.getExtras().getString("datalist"));
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }
    }

    ;

    private int getVersionCode() {
        int versionCode = -1;
        try {
            PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return versionCode;
    }

    /***
     * 是否包含指定字符串,不区分大小写
     *
     * @param input : 原字符串
     * @param regex
     * @return
     */
    public boolean contain2(String input, String regex) {
        if (input == null) {
            return false;
        }
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        boolean result = m.find();
        return result;
    }
}
