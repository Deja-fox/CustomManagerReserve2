package com.jald.reserve.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.bean.http.response.KSysInfoResponseBean;
import com.jald.reserve.bean.normal.KUserInfoStub;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.ui.KLoginPageActivity;
import com.jald.reserve.ui.KLoginPwdUpdateActivity;
import com.jald.reserve.ui.KPayPwdUpdateActivity;
import com.jald.reserve.ui.KPhoneUpdateActivity;
import com.jald.reserve.util.DialogProvider;
import com.jald.reserve.util.DownloadUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.FileDescriptor;

public class KUserCenterFragment extends Fragment {
    public static final String TAG = KUserCenterFragment.class.getSimpleName();

    public static final int REQUEST_CODE_START_LOGIN = 1;

    @ViewInject(R.id.top_pic)
    private ImageView imgTopPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.k_fragment_about_me, container, false);
        x.view().inject(this, view);
        float density = getResources().getDisplayMetrics().density;
        int height = (int) (getResources().getDisplayMetrics().widthPixels * 0.35);
        if (height < density * 180) {
            height = (int) (density * 180);
        }
        imgTopPic.getLayoutParams().height = height;
        return view;
    }

    @Event(R.id.me_btn_change_login_password)
    public void OnLoginPasswordUpdateBtnClick(View v) {
        if (!checkLogin()) {
            Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), KLoginPwdUpdateActivity.class);
        startActivity(intent);
    }

    @Event(R.id.change_the_mobile_phone_number_to_bind)
    public void OnChangeTheMobilePhoneNumberToBind(View v) {
        if (!checkLogin()) {
            Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), KPhoneUpdateActivity.class);
        startActivity(intent);
    }

    @Event(R.id.me_btn_reset_password)
    public void OnPayPasswordUpdateBtnClick(View v) {
        if (!checkLogin()) {
            Toast.makeText(getActivity(), "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), KPayPwdUpdateActivity.class);
        startActivity(intent);
    }

    @Event(R.id.me_btn_login_out)
    public void OnLogoutBtnClick(View v) {
        if (KBaseApplication.getInstance().getUserInfoStub() != null) {
            KBaseApplication.getInstance().logout();
            Intent intent = new Intent(getActivity(), KLoginPageActivity.class);
            startActivityForResult(intent, REQUEST_CODE_START_LOGIN);
        } else {
            Toast.makeText(getActivity(), "您已经退出了登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_START_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Event(R.id.me_btn_update)
    public void OnUpdateBtnClick(View v) {
        KHttpClient httpTools = KHttpClient.singleInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_type", "11");
        KUserInfoStub user = new KUserInfoStub();
        httpTools.postData(getActivity(), KHttpAdress.SYS_INFO, jsonObject, user, httpCallBackListener);
        DialogProvider.showProgressBar(getActivity(), new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                KHttpClient.singleInstance().cancel(getActivity());
            }
        });
    }

    KHttpCallBack httpCallBackListener = new KHttpCallBack() {
        @Override
        public void handleBusinessLayerSuccess(int operation, KBaseHttpResponseBean result, boolean isReturnSuccess) {
            if (result == null) {
                return;
            }
            if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                switch (operation) {
                    case KHttpAdress.SYS_INFO:
                        DialogProvider.hideProgressBar();
                        final KSysInfoResponseBean bean = JSON.parseObject(result.getContent(), KSysInfoResponseBean.class);
                        int localVersionCode = getVersionCode();
                        String serverVersion = bean.getVersion();
                        int serverVersionCode = Integer.parseInt(serverVersion);
                        if (serverVersionCode > localVersionCode) {
                            String alertMsg = bean.getAlert_msg();
                            if (alertMsg == null || alertMsg.equals("")) {
                                alertMsg = "有新版本啦,是否升级?";
                            }
                            DialogProvider.alert(getActivity(), "升级提示", alertMsg, "立即升级", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogProvider.hideAlertDialog();
                                    startDownlaod(bean.getVersion_url());
                                }
                            }, "以后再说", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogProvider.hideAlertDialog();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    };

    private void startDownlaod(String url) {
        DownloadUtil downloadUtil = DownloadUtil.getInstance(getActivity());
        downloadUtil.setTipsOnStartDownload("升级包已开始下载,请等待");
        downloadUtil.setDownloadCompleteListener(new DownloadUtil.DownloadListener() {
            @Override
            public void onDownloadedFileNotExistErr(String url, String localFileUri, String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadComplete(String url, String localFileUri, FileDescriptor fd) {
                Intent it = new Intent();
                it.setAction("android.intent.action.VIEW");
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.addCategory("android.intent.category.DEFAULT");
                it.setDataAndType(Uri.parse(localFileUri), "application/vnd.android.package-archive");
                startActivity(it);
            }
        });
        downloadUtil.start(url, "优市网更新");
    }

    private boolean checkLogin() {
        if (KBaseApplication.getInstance().getUserInfoStub() == null) {
            Intent intent = new Intent(getActivity(), KLoginPageActivity.class);
            getActivity().startActivity(intent);
            return false;
        }
        return true;
    }

    private int getVersionCode() {
        int versionCode = -1;
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return versionCode;
    }
}
