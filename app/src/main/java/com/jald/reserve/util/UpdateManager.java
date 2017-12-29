package com.jald.reserve.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jald.reserve.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UpdateManager {

    private Context mContext;
    // 接口地址
    private String versionUrl;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory().getPath() + "/com.jald.reserve/";

    private static final String saveFileName = savePath + "ysdh.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private TextView progress_text;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_ERROR = 3;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    progress_text.setText(progress + "%");
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    Toast.makeText(mContext, "已下载完成，请安装~", Toast.LENGTH_LONG).show();
                    installApk();
                    break;
                case DOWN_ERROR:
                    downloadDialog.dismiss();
                    Toast.makeText(mContext, "下载失败，尝试通过浏览器下载", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(versionUrl);
                    intent.setData(content_url);
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context, String versionUrl) {
        this.mContext = context;
        this.versionUrl = versionUrl;
    }

    public int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    public void showDownloadDialog() {
        Builder builder = null;
        if (getAndroidSDKVersion() >= 11) {
            builder = new Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        } else {
            builder = new Builder(mContext);
        }
        builder.setTitle("软件版本更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.lay_update_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        progress_text = (TextView) v.findViewById(R.id.progress_text);
        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        downloadDialog = builder.create();
        downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                interceptFlag = true;
                KAppManager.getAppManager().finishAllActivity();
            }
        });
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(versionUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWN_ERROR);
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(DOWN_ERROR);
            } finally {
                try {
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(DOWN_ERROR);
                }
            }

        }
    };

    /**
     * 下载apk
     *
     * @param
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     *
     * @param
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }
}
