package com.jald.reserve.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jald.reserve.KBaseApplication;
import com.jald.reserve.R;
import com.jald.reserve.bean.http.response.KBaseHttpResponseBean;
import com.jald.reserve.extension.bean.response.CustomListResponseBean;
import com.jald.reserve.http.KHttpAdress;
import com.jald.reserve.http.KHttpCallBack;
import com.jald.reserve.http.KHttpClient;
import com.jald.reserve.http.KHttpConst;
import com.jald.reserve.util.DialogProvider;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by zbw on 2017/5/9.
 * 说明：二维码扫描
 */

public class ScannerActivity extends KBaseActivity implements QRCodeView.Delegate {

    @Bind(R.id.zxingview)
    ZXingView zxingview;
    private ArrayList<CustomListResponseBean.KCustomBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        list = (ArrayList<CustomListResponseBean.KCustomBean>) this.getIntent().getSerializableExtra("custom");
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //打开后置摄像头预览,但并没有开始扫描
        zxingview.startCamera();
        //开启扫描框
        zxingview.showScanRect();
//        zxingview.startSpot();
        zxingview.startSpotDelay(100);
    }

    @Override
    protected void onStop() {
        zxingview.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zxingview.onDestroy();
        super.onDestroy();
    }

    private void initView() {
        //设置二维码的代理
        zxingview.setDelegate(this);
    }

    //扫描成功解析二维码成功后调用,可做对应的操作
    @Override
    public void onScanQRCodeSuccess(String result) {
        //扫描成功后调用震动器
        vibrator();
        //显示扫描结果
        checkResult(result);
        //再次延时1.5秒后启动
        zxingview.startSpot();

    }

    private void vibrator() {
        //获取系统震动服务
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    //校验扫描结果
    private void checkResult(String result) {
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        String user_name;
        String user_tp_id;
        if (result.contains("**")) {
            String[] sourceStrArray = result.split("\\*\\*");
            user_name = sourceStrArray[0];
            user_tp_id = sourceStrArray[1];
            //验证客户关系
            if (checkList(user_tp_id)) {
                //添加短保险验证
                checkId(user_name, user_tp_id);
//                Intent intent = new Intent(ScannerActivity.this, ScannerOrderActivity.class);
//                intent.putExtra(ScannerOrderActivity.EXTRA_USER_NAME, user_name);
//                intent.putExtra(ScannerOrderActivity.EXTRA_USER_AC_ID, user_tp_id);
//                startActivity(intent);
            } else {
                Toast.makeText(this, "请先建立客户关系", Toast.LENGTH_SHORT).show();
            }
//            //验证成功跳转
//            Intent intent = new Intent(ScannerActivity.this, ScannerOrderActivity.class);
//            intent.putExtra(ScannerOrderActivity.EXTRA_USER_NAME, user_name);
//            intent.putExtra(ScannerOrderActivity.EXTRA_USER_AC_ID, user_tp_id);
//            startActivity(intent);

        } else {
            Toast.makeText(this, "请扫描零售户二维码", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 检查用户短保险身份
     *
     * @param user_name
     * @param user_tp_id
     */
    private void checkId(final String user_name, final String user_tp_id) {
        JSONObject postJson = new JSONObject();
        postJson.put("p_type", 2);
        postJson.put("u_tp_id", user_tp_id);
        DialogProvider.showProgressBar(this);
        KHttpClient.singleInstance().postData(this, KHttpAdress.CHECK_INSURANCE, postJson, KBaseApplication.getInstance().getUserInfoStub(), new KHttpCallBack() {
            @Override
            public void handleBusinessLayerSuccess(int command, KBaseHttpResponseBean result, boolean isReturnSuccess) {
                if (isReturnSuccess && result.getRet_code().equals(KHttpConst.HTTP_SERVER_RETCODE_SUCCESS)) {
                    //验证成功跳转
                    Intent intent = new Intent(ScannerActivity.this, ScannerOrderActivity.class);
                    intent.putExtra(ScannerOrderActivity.EXTRA_USER_NAME, user_name);
                    intent.putExtra(ScannerOrderActivity.EXTRA_USER_AC_ID, user_tp_id);
                    startActivity(intent);
                }
                DialogProvider.hideProgressBar();
            }
        });


    }

    //扫描失败后调用的方法
    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
    }

    private boolean checkList(final String tp_id) {
        boolean isExist = false;
        for (CustomListResponseBean.KCustomBean item : list) {
            if (item.getTp_id().equals(tp_id)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }
}
